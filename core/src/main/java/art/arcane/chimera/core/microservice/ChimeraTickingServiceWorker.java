package art.arcane.chimera.core.microservice;

import art.arcane.quill.execution.J;
import art.arcane.quill.logging.L;
import art.arcane.quill.random.RNG;

public abstract class ChimeraTickingServiceWorker extends ChimeraServiceWorker {
    private long minInterval = 1000;
    private long maxInterval = 1000;
    private int maxTicksPerInterval = 1;
    private int minTicksPerInterval = 3;
    private transient boolean stop = false;

    @Override
    public void onEnable() {
        doTick();
    }

    private void doTick() {
        if (stop) {
            return;
        }

        J.a(() -> {
            J.sleep(RNG.r.l(minInterval, maxInterval));

            if (stop) {
                return;
            }

            tick(RNG.r.i(minTicksPerInterval, maxTicksPerInterval));
            doTick();
        });
    }

    @Override
    public void onDisable() {
        stop = true;
    }

    public void tick(int times) {
        if (times <= 0) {
            return;
        }

        if (times == 1) {
            tick();
            return;
        }

        for (int i = 0; i < times; i++) {
            tick();
        }
    }

    public void tick() {
        try {
            onTick();
        } catch (Throwable e) {
            L.f("Failed to tick " + getName());
            e.printStackTrace();
        }
    }

    public abstract void onTick();
}
