package art.arcane.chimera.core.microservice;

import art.arcane.archon.server.ArchonServiceWorker;
import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.net.ServiceSet;
import art.arcane.chimera.core.net.parcels.ParcelPing;
import art.arcane.chimera.core.net.parcels.ParcelPong;
import art.arcane.chimera.core.object.HostedService;
import art.arcane.quill.collections.KList;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.collections.KSet;
import art.arcane.quill.execution.J;
import art.arcane.quill.format.Form;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import art.arcane.quill.service.QuillServiceWorker;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class ChimeraServiceAccess extends QuillServiceWorker {
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

    public ServiceSet getServiceSet(String type) {
        ServiceSet s = services.get(type);

        if (s == null) {
            s = new ServiceSet();
        }

        return s;
    }

    public void withService(Runnable r, String... services) {
        J.a(() ->
        {
            while (!hasRequestedServices(services)) {
                J.sleep(1000);
            }

            r.run();
        });
    }

    public void waitForServices(String... services) {
        while (!hasRequestedServices(services)) {
            J.sleep(1000);
        }
    }

    public void waitForServices(long time, String... services) {
        long m = M.ms();
        while (!hasRequestedServices(services)) {
            if (M.ms() - m > time) {
                break;
            }

            J.sleep(1000);
        }
    }

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

            int f = ((ChimeraBackendService) Chimera.delegate).getProtocolAccess().getAllFunctions().size();
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

    public HostedService getService(String type, boolean now) {
        if (services.containsKey(type)) {
            return services.get(type).getNextService();
        }

        if (now) {
            return findServiceNow(type);
        }

        return null;
    }

    public HostedService getService(String type) {
        return getService(type, true);
    }

    private HostedService findServiceNow(String type) {
        HostedService svc = getService(type, false);

        if (svc != null) {
            return svc;
        }
        ArchonServiceWorker archon = firstParentService();

        HostedService f = HostedService.builder().build();
        try {
            if (getServiceDatabase().getSql().getWhere(f, "type", type) && verifyService(f)) {
                registerService(f);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
                getServiceDatabase().deleteAsync(svc);
            }
        } catch (Throwable ignored) {

        }

        return false;
    }

    private boolean ping(HostedService svc) {
        try {
            ParcelPong pong = (ParcelPong) ((ChimeraBackendService) Chimera.delegate).requestImpatient(svc, new ParcelPing(), true);

            if (pong != null) {
                return true;
            }
        } catch (Throwable e) {

        }
        return false;
    }

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
            ((ChimeraBackendService) Chimera.delegate).registerProtocols(i.getType());
            logNetworkUpdate();
        } else {
            if (services.containsKey(i.getType()) && services.get(i.getType()).isEmpty()) {
                services.remove(i.getType());
            }
        }
    }

    public void tick() {
        HostedService h = HostedService.builder().build();

        if (getServiceDatabase().getRandom(h)) {
            registerService(h, maxConservativeServiceGroupSize);
        }

        if (startupFastTicks <= 1) {
            try {
                HostedService id = services.v().getRandom().getRandom();

                if (!getServiceDatabase().getSql().exists(id)) {
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

    public KList<HostedService> getAllServices() {
        KList<HostedService> s = new KList<>();

        for (ServiceSet i : services.values()) {
            s.addAll(i);
        }

        return s;
    }

    private void notifyShuttingDown(HostedService i) {
        ((ChimeraBackendService) Chimera.delegate).requestImpatient(i, null, true);
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

        J.sleep(((ChimeraBackendService) Chimera.delegate).getImpatientWebClient().getTimeoutMs() + 50);
    }
}
