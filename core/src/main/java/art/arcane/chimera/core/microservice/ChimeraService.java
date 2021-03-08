package art.arcane.chimera.core.microservice;

import art.arcane.chimera.core.Chimera;
import art.arcane.quill.collections.KList;
import art.arcane.quill.collections.KMap;
import art.arcane.quill.execution.J;
import art.arcane.quill.io.IO;
import art.arcane.quill.json.JSONObject;
import art.arcane.quill.logging.L;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

;

/**
 * Represents a basic service of chimera. Each JVM typically has one service.
 */
@Data
public abstract class ChimeraService {
    @ServiceWorker
    @Getter
    private ChimeraConsoleWorker console = new ChimeraConsoleWorker();

    @Getter
    @ServiceWorker
    private ChimeraDatabaseWorker database = new ChimeraDatabaseWorker();

    @Getter
    @ServiceWorker
    private ChimeraDatabaseWorker serviceDatabase = new ChimeraDatabaseWorker("chimera_core");

    private boolean verbose = true;
    private transient final String serviceName;
    private transient ExecutorService sparkplugs;
    private transient boolean failing;
    private transient final InstanceCreator<ChimeraService> instanceCreator;
    private transient final Gson configGson;

    /**
     * A service must have a service name (capitalization is suggested)
     *
     * @param serviceName the fancy service name that will run on this jvm
     */
    public ChimeraService(String serviceName) {
        //@builder
        this.serviceName = serviceName;
        Thread.currentThread().setName("Chimera " + getServiceName());
        sparkplugs = null;
        failing = false;
        instanceCreator = type -> this;
        KMap<Class<?>, InstanceCreator<?>> c = new KMap<>();

        configGson = new GsonBuilder()
                .registerTypeAdapter(Chimera.delegateClass, instanceCreator)
                .create();
        //@done
    }

    /**
     * Queue a task to be run in parallel with other async init jobs. This service wont be considered online until these tasks finish
     *
     * @param r the runnable to execute before startup completes
     */
    protected void asyncInit(Runnable r) {
        if (sparkplugs == null) {
            throw new RuntimeException("You can only use asyncInit while in onEnable");
        }

        sparkplugs.submit(r);
    }

    /**
     * Called by Chimera service management. Do not call this.
     */
    public void startService() {
        L.i("Starting Chimera" + getServiceName() + " Service");
        sparkplugs = Executors.newCachedThreadPool(new ThreadFactory() {
            int tid = 0;

            @Override
            public Thread newThread(Runnable r) {
                tid++;
                Thread t = new Thread(r);
                t.setName("Chimera Sparkplug " + tid);
                t.setPriority(Thread.MAX_PRIORITY);
                t.setUncaughtExceptionHandler((et, e) ->
                {
                    L.f("Exception encountered in " + et.getName());
                    fail(e);
                });

                return t;
            }
        });

        for (Field i : new KList<>(getAllFields(getClass())).reverse()) {
            enableServiceWorker(i);
        }

        onEnable();
        sparkplugs.shutdown();

        try {
            sparkplugs.awaitTermination(30, TimeUnit.SECONDS);
        } catch (Throwable e) {
            L.f("Waited a full minute, can't shut down the thread pool. Skipping...");
        }

        sparkplugs = null;
        L.i("Chimera" + getServiceName() + " Service has Started");
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopService));
    }

    /**
     * Called by Chimera service management. Do not call this as a service
     *
     * @param delegateClass the delegated chimera service
     * @return the configured service (from json)
     * @throws NoSuchMethodException     shit happens
     * @throws IllegalAccessException    shit happens
     * @throws InvocationTargetException shit happens
     * @throws InstantiationException    shit happens
     */
    public static ChimeraService initializeConfigured(Class<? extends ChimeraService> delegateClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ChimeraService delegateDummy = delegateClass.getConstructor().newInstance();
        File configFile = new File("config/" + delegateDummy.getServiceName().toLowerCase() + ".json");
        configFile.getParentFile().mkdirs();
        JSONObject defaultConfig = new JSONObject(new Gson().toJson(delegateDummy));
        JSONObject currentConfig = new JSONObject();

        if (configFile.exists()) {
            try {
                currentConfig = new JSONObject(IO.readAll(configFile));
            } catch (Throwable e) {
                L.w("Failed to read config file. Regenerating...");
                L.ex(e);
            }
        }

        for (String i : defaultConfig.keySet()) {
            if (!currentConfig.has(i)) {
                L.v("Adding Default config value: " + i);
                currentConfig.put(i, defaultConfig.get(i));
            }
        }

        for (String i : currentConfig.keySet()) {
            if (!defaultConfig.has(i)) {
                L.w("Configuration key " + i + " is not reconized. Remove this from " + configFile.getPath());
            }
        }

        try {
            IO.writeAll(configFile, currentConfig.toString(4));
            L.v("Updated Configuration");
        } catch (Throwable e) {
            L.ex(e);
            Chimera.crashStack("Failed to write a config file... This is bad");
            return null;
        }

        ChimeraService svc = new Gson().fromJson(currentConfig.toString(), delegateClass);
        L.i("Configuration Loaded");
        return svc;
    }

    /**
     * Called by the chimera service management. Do not call this. Instead use Chimera.shutdown();
     */
    public void stopService() {
        L.i("Stopping Chimera" + getServiceName() + " Service");
        onDisable();

        for (Field i : getAllFields(getClass())) {
            disableServiceWorker(i);
        }

        L.i("Chimera" + getServiceName() + " Service has Stopped");
    }

    private void enableServiceWorker(Field i) {
        enableServiceWorker(i, this);
    }

    private void enableServiceWorker(Field i, Object o) {
        if (i.isAnnotationPresent(ServiceWorker.class)) {
            i.setAccessible(true);
            Class<? extends ChimeraServiceWorker> worker = (Class<? extends ChimeraServiceWorker>) i.getType();

            try {
                ChimeraServiceWorker sw = (ChimeraServiceWorker) i.get(o);
                sw.setServiceDepth(1);
                try {
                    sw.start();
                } catch (Throwable ex) {
                    L.ex(ex);
                    Chimera.crash("Failed to enable service worker " + worker.getCanonicalName());
                }
            } catch (Throwable e) {
                L.ex(e);
                Chimera.crash("Failed to initialize service worker " + worker.getCanonicalName());
            }
        }
    }

    private void disableServiceWorker(Field i) {
        disableServiceWorker(i, this);
    }

    private void disableServiceWorker(Field i, Object o) {
        if (i.isAnnotationPresent(ServiceWorker.class)) {
            i.setAccessible(true);
            Class<? extends ChimeraServiceWorker> worker = (Class<? extends ChimeraServiceWorker>) i.getType();

            try {
                ChimeraServiceWorker sw = (ChimeraServiceWorker) i.get(o);

                try {
                    sw.stop();
                } catch (Throwable ex) {
                    L.ex(ex);
                    Chimera.crash("Faield to disable service worker " + worker.getCanonicalName());
                }
            } catch (Throwable e) {
                L.ex(e);
                Chimera.crash("Failed to stop service worker " + worker.getCanonicalName());
            }
        }
    }

    private void fail(Throwable e) {
        failing = true;
        L.f("Service CRASH!");
        L.ex(e);

        L.w("Attempting to disable if possible...");
        J.attempt(() -> onDisable());
    }

    /**
     * This is called after all of the sub-service workers have been enabled. As a service, this is the last stage to initialize your components that are not service workers. You can also use asyncInit(Runnable) to initialize in parallel.
     */
    public abstract void onEnable();

    /**
     * This is called BEFORE all sub-service workers have been disabled. As a service this is typically the first stage in shutdown.
     */
    public abstract void onDisable();

    protected static List<Field> getAllFields(Class<?> aClass) {
        List<Field> fields = new ArrayList<>();
        do {
            Collections.addAll(fields, aClass.getDeclaredFields());
            aClass = aClass.getSuperclass();
        } while (aClass != null);
        return fields;
    }
}
