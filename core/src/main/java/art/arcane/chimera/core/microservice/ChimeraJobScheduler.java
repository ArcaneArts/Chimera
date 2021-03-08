package art.arcane.chimera.core.microservice;

import art.arcane.quill.collections.KList;
import art.arcane.quill.execution.ChronoLatch;

public class ChimeraJobScheduler extends ChimeraTickingServiceWorker {
    private transient final KList<Runnable> schedulers = new KList<>();

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onTick() {
        synchronized (schedulers) {
            schedulers.forEach(Runnable::run);
        }
    }

    public void schedule(Runnable r, long interval) {
        synchronized (schedulers) {
            schedulers.add(ChronoLatch.wrap(interval, r));
        }
    }
}
