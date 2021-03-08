package art.arcane.chimera.core.microservice;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.net.parcels.ParcelGetProtocol;
import art.arcane.chimera.core.net.parcels.ParcelSendProtocol;
import art.arcane.chimera.core.object.HostedService;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.io.IO;
import art.arcane.quill.json.JSONObject;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import art.arcane.quill.reaction.O;
import art.arcane.quill.web.Parcelable;
import com.google.gson.Gson;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChimeraHostedServiceWorker extends ChimeraServiceWorker {
    @ServiceWorker
    private ChimeraWebServiceWorker web = new ChimeraWebServiceWorker();

    @ServiceWorker
    private ChimeraServiceAccess serviceAccess = new ChimeraServiceAccess();

    @ServiceWorker
    private ChimeraWebClientWorker hangingWebClient = new ChimeraWebClientWorker();

    @ServiceWorker
    private ChimeraWebImpatientClientWorker impatientWebClient = new ChimeraWebImpatientClientWorker();

    @ServiceWorker
    private ChimeraProtocolAccess protocolAccess = new ChimeraProtocolAccess();

    @ServiceWorker
    private ChimeraJobServiceWorker jobService = new ChimeraJobServiceWorker();

    @ServiceWorker
    private ChimeraJobScheduler schedulerService = new ChimeraJobScheduler();

    private boolean logRequests = true;
    private transient String id;
    private transient HostedService host;
    private transient KMap<String, Class<? extends Parcelable>> parcelTypeCache = new KMap<>();

    public ChimeraHostedServiceWorker() {
        super();
        id = IO.hash(UUID.randomUUID().toString()).toLowerCase();
    }

    public <T> T invokeFunction(String name, Object... params) {
        return (T) getProtocolAccess().execute(name, params);
    }

    public InputStream invokeDownstreamFunction(String name, Object... params) {
        return getProtocolAccess().executeDownstream(name, params);
    }

    private void publish(HostedService service) {
        getServiceDatabase().setAsync(service);
    }

    private void unpublish(HostedService host) {
        getServiceDatabase().deleteAsync(host);
    }

    public Parcelable request(HostedService svc, Parcelable request) {
        return request(svc, request, false);
    }

    public Parcelable request(HostedService svc, Parcelable request, boolean suppress) {
        String url = svc.getURL() + "" + request.getParcelType() + "?b=" + IO.encode(new Gson().toJson(request).getBytes(StandardCharsets.UTF_8)).replaceAll("\\Q//\\E", "/").replaceAll("\\Q//\\E", "/");

        if (logRequests) {
            L.v("Request: " + url);
        }

        String r = hangingWebClient.request(url, suppress);
        JSONObject response = new JSONObject(r);
        return new Gson().fromJson(r, parcelTypeCache.get(response.getString("type")));
    }

    public Parcelable requestImpatient(HostedService svc, Parcelable request) {
        return requestImpatient(svc, request, false);
    }

    public Parcelable requestImpatient(HostedService svc, Parcelable request, boolean suppress) {
        String url = svc.getURL() + "" + request.getParcelType() + "?b=" + IO.encode(new Gson().toJson(request).getBytes(StandardCharsets.UTF_8)).replaceAll("\\Q//\\E", "/").replaceAll("\\Q//\\E", "/");

        if (logRequests) {
            L.v("Request: " + url);
        }

        String r = impatientWebClient.request(url, suppress);
        JSONObject response = new JSONObject(r);
        return new Gson().fromJson(r, parcelTypeCache.get(response.getString("type")));
    }

    private boolean dropService(HostedService s) {
        serviceAccess.getServiceSet(s.getType()).remove(s);
        L.i("Lost connection to " + s.toString());
        return true;
    }

    public HostedService internalRequest(String serviceType, Parcelable request, O<Parcelable> o) {
        O<Boolean> fail = new O<Boolean>();
        fail.set(false);
        HostedService hs = serviceAccess.getService(serviceType);
        int tries = 8;

        while (hs == null && tries-- >= 0) {
            L.i("Waiting for service: " + serviceType);
            getServiceAccess().waitForServices(5000, serviceType);
            hs = serviceAccess.getService(serviceType);
        }

        try {
            String url = hs.getURL() + "" + request.getParcelType() + "?b=" + IO.encode(new Gson().toJson(request).getBytes(StandardCharsets.UTF_8)).replaceAll("\\Q//\\E", "/").replaceAll("\\Q//\\E", "/");

            if (logRequests) {
                L.v("Request: " + url);
            }

            String r = hangingWebClient.request(url, false, () ->
            {
                fail.set(true);
            });

            if (fail.get()) {
                return hs;
            }

            JSONObject response = new JSONObject(r);
            o.set(new Gson().fromJson(r, parcelTypeCache.get(response.getString("type"))));
            return null;
        } catch (Throwable e) {
            L.f("Exception trying to switch hosts");
            L.ex(e);
            return hs;
        }
    }

    public HostedService internalDownstreamRequest(String serviceType, Parcelable request, O<InputStream> o) {
        O<Boolean> fail = new O<Boolean>();
        fail.set(false);
        HostedService hs = serviceAccess.getService(serviceType);
        int tries = 8;

        while (hs == null && tries-- >= 0) {
            L.i("Waiting for service: " + serviceType);
            getServiceAccess().waitForServices(5000, serviceType);
            hs = serviceAccess.getService(serviceType);
        }

        try {
            String url = hs.getURL() + "" + request.getParcelType() + "?b=" + IO.encode(new Gson().toJson(request).getBytes(StandardCharsets.UTF_8)).replaceAll("\\Q//\\E", "/").replaceAll("\\Q//\\E", "/");

            if (logRequests) {
                L.v("Request Downstream: " + url);
            }

            InputStream r = hangingWebClient.requestDownstream(url, false, () ->
            {
                fail.set(true);
            });

            if (fail.get()) {
                return hs;
            }

            o.set(r);
            return null;
        } catch (Throwable e) {
            L.f("Exception trying to switch hosts");
            L.ex(e);
            return hs;
        }
    }

    public Parcelable request(String serviceType, Parcelable request) {
        O<Parcelable> res = new O<Parcelable>();
        HostedService dead = internalRequest(serviceType, request, res);

        if (dead != null) {
            dropService(dead);
            L.w("Retrying request on another service...");
            return request(serviceType, request);
        }

        return res.get();
    }

    public InputStream requestDownstream(String serviceType, Parcelable request) {
        O<InputStream> res = new O<InputStream>();
        HostedService dead = internalDownstreamRequest(serviceType, request, res);

        if (dead != null) {
            dropService(dead);
            L.w("Retrying request on another service...");
            return requestDownstream(serviceType, request);
        }

        return res.get();
    }

    @Override
    public void onEnable() {
        for (Class<? extends Parcelable> i : web.getServer().getParcelables()) {
            try {
                Parcelable v = i.getConstructor().newInstance();
                parcelTypeCache.put(v.getParcelType(), i);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                L.ex(e);
                Chimera.crash("Failed to process parcel " + i.getCanonicalName());
            }
        }

        try {
            //@builder
            setHost(HostedService.builder()
                    .address(InetAddress.getLocalHost().getHostAddress())
                    .port(web.getWebServer().httpPort())
                    .id(getId())
                    .time(M.ms())
                    .dir(web.getWebServer().serverPath())
                    .type(Chimera.getDelegateModuleName())
                    .build());
            publish(getHost());
            //@done
            L.i("Published Host: " + getHost().toString());
        } catch (Throwable e) {
            L.ex(e);
            Chimera.crash("Failed to build host information for service publication.");
        }

        for (String i : getServiceAccess().getServices()) {
            registerProtocols(i);
        }
    }

    public void registerProtocols(String i) {
        if (getProtocolAccess().hasProtocolFor(i)) {
            return;
        }

        ParcelSendProtocol proto = (ParcelSendProtocol) request(i, new ParcelGetProtocol());
        getProtocolAccess().registerRemoteFunctions(getServiceAccess().getService(i), proto.getFunctions());
    }

    public void notifyRemoteShuttingDown(String key) {
        getServiceAccess().unregisterService(key);
    }

    @Override
    public void onDisable() {
        unpublish(getHost());
    }
}
