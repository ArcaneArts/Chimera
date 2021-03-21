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

import art.arcane.quill.execution.J;
import art.arcane.quill.logging.L;
import art.arcane.quill.random.RNG;
import art.arcane.quill.service.QuillService;

public abstract class ChimeraTickingServiceWorker extends QuillService {
    private long minInterval = 10000;
    private long maxInterval = 60000;
    private int maxTicksPerInterval = 1;
    private int minTicksPerInterval = 1;
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
