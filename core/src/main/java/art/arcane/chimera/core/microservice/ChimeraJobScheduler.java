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
