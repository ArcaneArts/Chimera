/*
 * This file is part of Chimera by Arcane Arts.
 *
 * Chimera by Arcane Arts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Chimera by Arcane Arts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License in this package for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chimera.  If not, see <https://www.gnu.org/licenses/>.
 */

package art.arcane.chimera.core.microservice;

import art.arcane.archon.element.ElementList;
import art.arcane.archon.server.ArchonService;
import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.net.parcels.ParcelGetProtocol;
import art.arcane.chimera.core.net.parcels.ParcelSendProtocol;
import art.arcane.chimera.core.object.HostedService;
import art.arcane.chimera.core.object.Session;
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

/**
 * All root services in chimera need to use ChimeraService instead of QuillService
 */
public abstract class ChimeraService extends QuillService {
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

    public ChimeraService() {
        Chimera.backend = this;
    }

    /**
     * Publish a service into the network so it is discoverable by other services
     *
     * @param service the hosted service object
     */
    private void publish(HostedService service) {
        service.setArchon(Chimera.archon);
        service.push();
    }

    /**
     * Unpublish a service from the network so it is no longer discoverable
     *
     * @param host the host to unpublish
     */
    private void unpublish(HostedService host) {
        host.setArchon(Chimera.archon);
        host.delete();
    }

    /**
     * Send a parcel request to the given host with a message and get a reply
     * This will hang until the service is available.
     *
     * @param svc     the service to request to
     * @param request the request parcel
     * @return the responding parcel
     */
    public Parcelable request(HostedService svc, Parcelable request) {
        return request(svc, request, false);
    }

    /**
     * Send a parcel request to the given host with a message and get a reply
     * This will hang until the service is available.
     *
     * @param svc      the service to request to
     * @param request  the request parcel
     * @param suppress if suppressed exceptions will be ignored
     * @return the responding parcel
     */
    public Parcelable request(HostedService svc, Parcelable request, boolean suppress) {
        String url = svc.getURL() + "" + request.getParcelType() + "?b=" + IO.encode(new Gson().toJson(request).getBytes(StandardCharsets.UTF_8)).replaceAll("\\Q//\\E", "/").replaceAll("\\Q//\\E", "/");

        if (logRequests) {
            L.v("Request: " + url);
        }

        String r = hangingWebClient.request(url, suppress);
        JSONObject response = new JSONObject(r);
        return new Gson().fromJson(r, parcelTypeCache.get(response.getString("type")));
    }

    /**
     * Send a parcel request to the given host with a message and get a reply
     * This request will eventually time out
     *
     * @param svc     the service to request to
     * @param request the request parcel
     * @return the responding parcel
     */
    public Parcelable requestImpatient(HostedService svc, Parcelable request) {
        return requestImpatient(svc, request, false);
    }

    /**
     * Send a parcel request to the given host with a message and get a reply
     * This request will eventually time out
     *
     * @param svc      the service to request to
     * @param request  the request parcel
     * @param suppress if true will ignore exceptions
     * @return the responding parcel
     */
    public Parcelable requestImpatient(HostedService svc, Parcelable request, boolean suppress) {
        String url = svc.getURL() + "" + request.getParcelType() + "?b=" + IO.encode(new Gson().toJson(request).getBytes(StandardCharsets.UTF_8)).replaceAll("\\Q//\\E", "/").replaceAll("\\Q//\\E", "/");

        if (logRequests) {
            L.v("Request: " + url);
        }

        String r = impatientWebClient.request(url, suppress);
        JSONObject response = new JSONObject(r);
        return new Gson().fromJson(r, parcelTypeCache.get(response.getString("type")));
    }

    /**
     * Drop a service from this services connection map. This does not remove the host
     * It simply drops the connection (meaning it will eventually reconnect)
     *
     * @param s the host to drop
     */
    public void dropService(HostedService s) {
        serviceAccess.getServiceSet(s.getType()).remove(s);
        L.i("Lost connection to " + s.toString());
    }

    private HostedService internalRequest(String serviceType, Parcelable request, O<Parcelable> o) {
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

    private HostedService internalDownstreamRequest(String serviceType, Parcelable request, O<InputStream> o) {
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

    /**
     * Sends a parcel request to a random connected service under the serviceType
     * This method will continue to hang until at least one host under that type
     * is actually online and has handled the request
     *
     * @param serviceType the service type
     * @param request     the request
     * @return the response.
     */
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

    /**
     * Sends a parcel request to a random connected service under the serviceType
     * This method will continue to hang until at least one host under that type
     * is actually online and has handled the request and sends a stream
     *
     * @param serviceType the service type
     * @param request     the request
     * @return the response inputstream.
     */
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

        registerCommands();

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

    /**
     * Registers function protocols for the specified service
     *
     * @param i the specified service
     */
    public void registerProtocols(String i) {
        if (getProtocolAccess().hasProtocolFor(i)) {
            return;
        }

        ParcelSendProtocol proto = (ParcelSendProtocol) request(i, new ParcelGetProtocol());
        getProtocolAccess().registerRemoteFunctions(getServiceAccess().getService(i), proto.getFunctions());
    }

    @Override
    public void onDisable() {
        unpublish(host);
    }

    /**
     * Schedules a repeating runnable job
     *
     * @param delegate the runnable
     * @param interval the interval in ms
     */
    public void scheduleRepeatingJob(Runnable delegate, long interval) {
        getSchedulerService().schedule(delegate, interval);
    }

    /**
     * Will execute a runnable only when there are services available for the job
     *
     * @param r        the runnable
     * @param services the list of services that need to be online for
     *                 the runnable to work
     */
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

    /**
     * Wait for the services async, then run the runnable
     *
     * @param r        the runnable
     * @param services the services to wait for before the runnable will run
     */
    public void serviceWorkAsync(Runnable r, String... services) {
        getServiceAccess().withService(r, services);
    }


    /**
     * Invoke a network function
     *
     * @param name   the function name
     * @param params the parameters
     * @param <T>    the return result type expected
     * @return the return result of the function
     */
    public <T> T invokeFunction(String name, Object... params) {
        return (T) getProtocolAccess().execute(name, params);
    }

    /**
     * Invokes a network function with a downstream
     *
     * @param name   the name
     * @param params the parameters
     * @return the inputstream result
     */
    public InputStream invokeDownstreamFunction(String name, Object... params) {
        return getProtocolAccess().executeDownstream(name, params);
    }

    private void registerCommands() {
        console.registerCommand("list-services", (args) -> {
            for (String i : getServiceAccess().getServices()) {
                L.i(i + ": ");

                for (HostedService j : getServiceAccess().getServiceSet(i)) {
                    L.i("  " + j.getURL() + (host.getId().equals(j.getId()) ? " [self] " : " ") + "(" + j.getId() + ")");
                }
            }
            return true;
        });

        console.registerCommand("list-functions", (args) ->
        {
            getProtocolAccess().getAllFunctions().forEach((i) -> L.i(i.getName() + ": " + new Gson().toJson(i)));
            return true;
        });

        console.registerCommand("list-clients", (args) ->
        {
            J.a(() -> {
                ElementList<Session> sessions = new Session().allWhere("1");

                L.i("Total Connected Clients: " + sessions.size());

                for (int i = 0; i < sessions.size(); i++) {
                    Session s = sessions.get(i);
                    L.i("  - " + s.getId());
                }
            });

            return true;
        });

        console.registerCommand("drop-service", (args) -> {
            if (args.length < 1) {
                L.f("Use 'drop-service ServiceID'");
                return true;
            }

            HostedService s = getServiceAccess().getService(ID.fromString(args[0]));

            if (s == null) {
                L.f("Couldn't find any service connection with ID " + args[0]);
                return true;
            }

            if (s.getId().equals(host.getId())) {
                L.f("You cannot disconnect from yourself!");
                return true;
            }

            getServiceAccess().unregisterService(s);
            L.i("Dropped Service " + s.getId().toString());

            return true;
        });
    }

    /**
     * Get the service host that this service represents
     *
     * @return the host
     */
    public HostedService getHost() {
        return host;
    }
}
