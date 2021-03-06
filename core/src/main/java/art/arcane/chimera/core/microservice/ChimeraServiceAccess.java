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

import art.arcane.archon.data.ArchonResult;
import art.arcane.archon.server.ArchonService;
import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.net.ServiceSet;
import art.arcane.chimera.core.net.parcels.ParcelPing;
import art.arcane.chimera.core.net.parcels.ParcelPong;
import art.arcane.chimera.core.object.HostedService;
import art.arcane.quill.Quill;
import art.arcane.quill.collections.ID;
import art.arcane.quill.collections.KList;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.collections.KSet;
import art.arcane.quill.execution.J;
import art.arcane.quill.format.Form;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import art.arcane.quill.service.QuillService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages Chimera services communicating with each other
 * and registering themselves for each other
 */
public class ChimeraServiceAccess extends QuillService {
    private transient KMap<String, ServiceSet> services = new KMap<>();
    private int startupFastTicks = 7;
    private long fastTickTime = 950;
    private boolean networkUpdates = true;
    private long tickTime = 7000;
    private int maxConservativeServiceGroupSize = 64;
    private int maxServiceGroupSize = 64;
    private transient KSet<String> waitingFor = new KSet<>();
    private transient String lastNodeUpdate = "";

    public ConcurrentHashMap.KeySetView<String, ServiceSet> getServices() {
        return services.keySet();
    }

    /**
     * Get all connected services under a specific type
     *
     * @param type the service type
     * @return the service set (list)
     */
    public ServiceSet getServiceSet(String type) {
        ServiceSet s = services.get(type);

        if (s == null) {
            s = new ServiceSet();
        }

        return s;
    }

    /**
     * Execute a runnable (async wait) when services are available
     *
     * @param r        the runnable to run
     * @param services the services required
     */
    public void withService(Runnable r, String... services) {
        J.a(() ->
        {
            while (!hasRequestedServices(services)) {
                J.sleep(1000);
            }

            r.run();
        });
    }

    /**
     * Waits for a list of services to be online before continuing (sync wait)
     *
     * @param services the services to wait for
     */
    public void waitForServices(String... services) {
        while (!hasRequestedServices(services)) {
            J.sleep(1000);
        }
    }

    /**
     * Waits for a list of services to be online before continuing (sync wait)
     *
     * @param time     the timeout
     * @param services the services to wait for
     * @return returns true if all services are online, or false if we timed out
     */
    public boolean waitForServices(long time, String... services) {
        long m = M.ms();
        while (!hasRequestedServices(services)) {
            if (M.ms() - m > time) {
                return false;
            }

            J.sleep(1000);
        }
        return true;
    }

    /**
     * Triggers a log to the console about what nodes are connected
     */
    public void logNetworkUpdate() {
        if (!networkUpdates) {
            return;
        }

        J.a(() ->
        {
            StringBuilder svc = new StringBuilder();

            int nodes = 0;

            for (String i : services.keySet()) {
                nodes += services.get(i).size();
                svc.append(", " + i + "(" + services.get(i).size() + ")");
            }

            int f = ((ChimeraService) Quill.delegate).getProtocolAccess().getAllFunctions().size();
            String ff = f + " Function" + (f == 1 ? "" : "s");
            String g = nodes + " Node" + (nodes == 1 ? "" : "s") + ", " + ff + " ";

            if (svc.length() > 0) {
                g += "-> " + svc.substring(2);
            } else {
                g += svc.toString();
            }

            if (g.equals(lastNodeUpdate)) {
                return;
            }

            lastNodeUpdate = g;
            L.v("Network: " + g);
        });
    }

    /**
     * Checks if the given services are online and reachable
     *
     * @param services the list of services
     * @return true if they are all online
     */
    public boolean hasRequestedServices(String... services) {
        boolean ready = true;

        for (String i : services) {
            if (getService(i) == null) {
                waitingFor.add(i);
                ready = false;
            } else {
                waitingFor.remove(i);
            }
        }

        return ready;
    }

    /**
     * Get a random service from a service type
     *
     * @param type the type of service
     * @param now  if now is false, we will lookup an internal map of existing connections.
     *             If its not there, we return nothing.
     *             If now is set to TRUE however, we will lock & look for a service,
     *             connect it, then return it.
     * @return the hosted service
     */
    public HostedService getService(String type, boolean now) {
        if (services.containsKey(type)) {
            return services.get(type).getNextService();
        }

        if (now) {
            return findServiceNow(type);
        }

        return null;
    }

    /**
     * Get an already connected service by it's id
     *
     * @param serviceID the id
     * @return the service or null
     */
    public HostedService getService(ID serviceID) {
        for (String i : getServices()) {
            for (HostedService j : getServiceSet(i)) {
                if (j.getId().equals(serviceID)) {
                    return j;
                }
            }
        }

        return null;
    }

    /**
     * Get a random service from a service type. If there isnt one
     * we will continue looking for one and connect it then finally return it
     *
     * @param type the service type
     * @return the hosted service or null
     */
    public HostedService getService(String type) {
        return getService(type, true);
    }

    /**
     * Find a service NOW, meaning lock this until we find one
     *
     * @param type the service type
     * @return the hosted service
     */
    private HostedService findServiceNow(String type) {
        HostedService svc = getService(type, false);

        if (svc != null) {
            return svc;
        }
        ArchonService archon = Chimera.archon;
        HostedService f = HostedService.builder().build();
        f.setArchon(archon);
        if (f.pull(archon.query("SELECT * FROM `" + f.getTableName() + "` WHERE `type` = '" + type + "' LIMIT 1;")) && verifyService(f)) {
            registerService(f);
        }

        return null;
    }

    private boolean verifyService(HostedService svc) {

        if (svc == null) {
            return false;
        }

        try {
            if (ping(svc)) {
                return true;
            } else {
                L.w("Unresponsive Service " + svc.getType() + "/" + svc.getId() + " at " + svc.getAddress() + ":" + svc.getPort() + " online for " + Form.duration(M.ms() - svc.getTime()));
                svc.delete();
            }
        } catch (Throwable ignored) {

        }

        return false;
    }

    private boolean ping(HostedService svc) {
        try {
            ParcelPong pong = (ParcelPong) Chimera.backend
                    .requestImpatient(svc, new ParcelPing(), true);

            if (pong != null) {
                return true;
            }
        } catch (Throwable e) {

        }
        return false;
    }

    /**
     * Unregister a service (doesnt delete any registries on sql)
     *
     * @param i the service to unregister
     */
    public void unregisterService(HostedService i) {
        if (!services.containsKey(i.getType())) {
            return;
        }

        for (HostedService j : services.get(i.getType()).copy()) {
            if (j.getId().equals(i.getId())) {
                services.get(i.getType()).remove(j);
                L.v("Lost Service " + i.getType() + "/" + i.getId() + " at " + j.getAddress() + ":" + j.getPort() + " online for " + Form.duration(M.ms() - j.getTime()));
                logNetworkUpdate();
            }
        }

        if (services.get(i.getType()).isEmpty()) {
            services.remove(i.getType());
        }
    }

    public void unregisterService(String i) {
        String type = i.split("\\Q/\\E")[0];
        String id = i.split("\\Q/\\E")[1];

        if (!services.containsKey(type)) {
            return;
        }

        for (HostedService j : services.get(type).copy()) {
            if (j.getId().equals(id)) {
                services.get(type).remove(j);
                L.v("Lost Service " + type + "/" + id + " at " + j.getAddress() + ":" + j.getPort() + " online for " + Form.duration(M.ms() - j.getTime()));
                logNetworkUpdate();
            }
        }

        if (services.get(type).isEmpty()) {
            services.remove(type);
        }
    }

    private void registerService(HostedService i) {
        registerService(i, maxServiceGroupSize);
    }

    private void registerService(HostedService i, int max) {
        if (!services.containsKey(i.getType())) {
            services.put(i.getType(), new ServiceSet());
        }

        if (services.get(i.getType()).size() > max) {
            return;
        }

        for (HostedService j : services.get(i.getType())) {
            if (j.getId().equals(i.getId())) {
                return;
            }
        }

        if (verifyService(i)) {
            services.get(i.getType()).add(i);
            L.v("Found Service " + i.getType() + "/" + i.getId() + " at " + i.getAddress() + ":" + i.getPort() + " online for " + Form.duration(M.ms() - i.getTime()));
            Chimera.backend.registerProtocols(i.getType());
            logNetworkUpdate();
        } else {
            if (services.containsKey(i.getType()) && services.get(i.getType()).isEmpty()) {
                services.remove(i.getType());
            }
        }
    }

    public void tick() {
        HostedService h = HostedService.builder().build();
        ArchonService archon = Chimera.archon;
        h.setArchon(archon);
        ArchonResult q = archon.query("SELECT * FROM `" + h.getTableName() + "` ORDER BY RAND() LIMIT 1;");
        if (h.pull(q)) {
            registerService(h, maxConservativeServiceGroupSize);
        }

        if (startupFastTicks <= 1) {
            try {
                HostedService id = services.v().getRandom().getRandom();

                if (!id.exists()) {
                    unregisterService(id);
                }
            } catch (Throwable e) {

            }
        }

        if (!waitingFor.isEmpty()) {
            L.w("Waiting for services: " + waitingFor.toString(", "));
        }
    }

    private void doTicking() {
        J.a(() ->
        {
            J.sleep(startupFastTicks-- > 0 ? fastTickTime : tickTime);
            tick();
            doTicking();
        });
    }

    /**
     * Gets all hosted services connected
     *
     * @return the list of hosted services
     */
    public KList<HostedService> getAllServices() {
        KList<HostedService> s = new KList<>();

        for (ServiceSet i : services.values()) {
            s.addAll(i);
        }

        return s;
    }

    private void notifyShuttingDown(HostedService i) {
        Chimera.backend.requestImpatient(i, null, true);
    }

    @Override
    public void onEnable() {
        doTicking();
    }

    @Override
    public void onDisable() {
        for (HostedService i : getAllServices()) {
            J.a(() -> notifyShuttingDown(i));
        }

        J.sleep(Chimera.backend.getImpatientWebClient().getTimeoutMs() + 50);
    }
}
