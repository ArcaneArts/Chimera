package art.arcane.chimera.core.microservice;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.protocol.generation.ProtoBuilder;
import art.arcane.chimera.core.protocol.generation.ProtoFunction;
import art.arcane.chimera.core.protocol.generation.Protocol;
import art.arcane.quill.collections.KList;
import art.arcane.quill.execution.J;
import art.arcane.quill.logging.L;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ChimeraBackendService extends ChimeraService {
    @Getter
    @ServiceWorker
    private ChimeraHostedServiceWorker backendService = new ChimeraHostedServiceWorker();

    @Getter
    private transient KList<ProtoFunction> functions;

    public ChimeraBackendService(String serviceName) {
        super(serviceName);
    }

    public void scheduleRepeatingJob(Runnable delegate, long interval) {
        getBackendService().getSchedulerService().schedule(delegate, interval);
    }

    public void serviceWork(Runnable r, String... services) {
        AtomicBoolean b = new AtomicBoolean(false);
        getBackendService().getServiceAccess().withService(() -> {
            r.run();
            b.set(true);
        }, services);

        while (!b.get()) {
            J.sleep(100);
        }
    }

    public void serviceWorkAsync(Runnable r, String... services) {
        getBackendService().getServiceAccess().withService(r, services);
    }

    public <T> T invokeFunction(String name, Object... params) {
        return getBackendService().invokeFunction(name, params);
    }

    public InputStream invokeDownstreamFunction(String name, Object... params) {
        return getBackendService().invokeDownstreamFunction(name, params);
    }

    @Override
    public void startService() {
        functions = new KList<>();

        for (Field i : ChimeraService.getAllFields(getClass())) {
            if (i.isAnnotationPresent(Protocol.class)) {
                try {
                    i.setAccessible(true);
                    functions.add(ProtoBuilder.functions(i.getType(), i.get(Modifier.isStatic(i.getModifiers()) ? null : this)));
                } catch (IllegalAccessException e) {
                    L.ex(e);
                    Chimera.crash("Failed to read function list in field " + i.getName() + " in " + getClass().getCanonicalName());
                }
            }
        }
        super.startService();

        getConsole().registerCommand("list-functions", (args) ->
        {
            backendService.getProtocolAccess().getAllFunctions().forEach((i) -> L.i(i.getName() + ": " + new Gson().toJson(i)));
            return true;
        });
    }
}
