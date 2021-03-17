package art.arcane.chimera.core.microservice;

import art.arcane.archon.server.ArchonService;
import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.net.parcels.ParcelGetProtocol;
import art.arcane.chimera.core.net.parcels.ParcelSendProtocol;
import art.arcane.chimera.core.object.HostedService;
import art.arcane.chimera.core.protocol.generation.ProtoBuilder;
import art.arcane.chimera.core.protocol.generation.ProtoFunction;
import art.arcane.chimera.core.protocol.generation.Protocol;
import art.arcane.chimera.core.util.web.Parcelable;
import art.arcane.quill.Quill;
import art.arcane.quill.collections.ID;
import art.arcane.quill.collections.KList;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.execution.J;
import art.arcane.quill.io.IO;
import art.arcane.quill.json.JSONObject;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import art.arcane.quill.reaction.O;
import art.arcane.quill.service.QuillService;
import art.arcane.quill.service.Service;
import art.arcane.quill.service.services.ConsoleService;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ChimeraBackendService extends QuillService {
    @Getter
    @Service
    private ConsoleService console = new ConsoleService();

    @Getter
    @Service
    private ChimeraWebServiceWorker web = new ChimeraWebServiceWorker();

    @Getter
    @Service
    private ChimeraServiceAccess serviceAccess = new ChimeraServiceAccess();

    @Getter
    @Service
    private ChimeraWebClientWorker hangingWebClient = new ChimeraWebClientWorker();

    @Getter
    @Service
    private ChimeraWebImpatientClientWorker impatientWebClient = new ChimeraWebImpatientClientWorker();

    @Getter
    @Service
    private ChimeraProtocolAccess protocolAccess = new ChimeraProtocolAccess();

    @Getter
    @Service
    private ChimeraJobServiceWorker jobService = new ChimeraJobServiceWorker();

    @Getter
    @Service
    private ChimeraJobScheduler schedulerService = new ChimeraJobScheduler();

    @Getter
    @Service
    private ArchonService database = new ArchonService();

    @Getter
    private transient KList<ProtoFunction> functions;
    private boolean logRequests = true;
    @Getter
    private transient ID id;
    private transient HostedService host;
    private transient KMap<String, Class<? extends Parcelable>> parcelTypeCache = new KMap<>();

    public ChimeraBackendService() {
        Chimera.backend = this;
    }

    private void publish(HostedService service) {
        service.setArchon(Chimera.archon);
        service.push();
    }

    private void unpublish(HostedService host) {
        host.setArchon(Chimera.archon);
        host.delete();
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
        id = new ID();
        functions = new KList<>();

        for (Field i : QuillService.getAllFields(getClass())) {
            if (i.isAnnotationPresent(Protocol.class)) {
                try {
                    i.setAccessible(true);
                    functions.add(ProtoBuilder.functions(i.getType(), i.get(Modifier.isStatic(i.getModifiers()) ? null : this)));
                } catch (IllegalAccessException e) {
                    L.ex(e);
                    Quill.crash("Failed to read function list in field " + i.getName() + " in " + getClass().getCanonicalName());
                }
            }
        }

        console.registerCommand("list-functions", (args) ->
        {
            getProtocolAccess().getAllFunctions().forEach((i) -> L.i(i.getName() + ": " + new Gson().toJson(i)));
            return true;
        });

        for (Class<? extends Parcelable> i : web.getServer().getParcelables()) {
            try {
                Parcelable v = i.getConstructor().newInstance();
                parcelTypeCache.put(v.getParcelType(), i);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                L.ex(e);
                Quill.crash("Failed to process parcel " + i.getCanonicalName());
            }
        }

        try {
            //@builder
            host = HostedService.builder()
                    .address(InetAddress.getLocalHost().getHostAddress())
                    .port(web.getWebServer().httpPort())
                    .id(id)
                    .time(M.ms())
                    .dir(web.getWebServer().serverPath())
                    .type(Quill.getDelegateModuleName())
                    .build();
            Quill.postJob(() -> host.archon(Chimera.archon).push());
            //@done);
        } catch (Throwable e) {
            L.ex(e);
            Quill.crash("Failed to build host information for service publication.");
        }

        try {
            for (String i : getServiceAccess().getServices()) {
                registerProtocols(i);
            }
        } catch (Throwable e) {
            L.ex(e);
            Quill.crashStack("Failed to register service protocols");
        }
        Chimera.archon = getDatabase();
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
        unpublish(host);
    }

    public void scheduleRepeatingJob(Runnable delegate, long interval) {
        getSchedulerService().schedule(delegate, interval);
    }

    public void serviceWork(Runnable r, String... services) {
        AtomicBoolean b = new AtomicBoolean(false);
        getServiceAccess().withService(() -> {
            r.run();
            b.set(true);
        }, services);

        while (!b.get()) {
            J.sleep(100);
        }
    }

    public void serviceWorkAsync(Runnable r, String... services) {
        getServiceAccess().withService(r, services);
    }


    public <T> T invokeFunction(String name, Object... params) {
        return (T) getProtocolAccess().execute(name, params);
    }

    public InputStream invokeDownstreamFunction(String name, Object... params) {
        return getProtocolAccess().executeDownstream(name, params);
    }
}
