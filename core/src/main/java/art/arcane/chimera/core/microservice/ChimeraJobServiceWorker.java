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
import art.arcane.chimera.core.object.ServiceJob;
import art.arcane.chimera.core.protocol.EDX;
import art.arcane.quill.collections.ID;
import art.arcane.quill.collections.KList;
import art.arcane.quill.logging.L;
import art.arcane.quill.math.M;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Responsible for scheduling network functions in a job list
 * for services to execute in the future
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChimeraJobServiceWorker extends ChimeraTickingServiceWorker {
    private boolean logJobExecutions = true;

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
        String serviceType = Chimera.backend.getServiceName().toLowerCase();
        ArchonService archon = Chimera.archon;
        ArchonResult r = archon.query("SELECT `id`,`function`,`parameters` FROM `jobs` WHERE `service` = '" + serviceType + "' AND `ttl` < " + M.ms() + " ORDER BY `deadline` DESC LIMIT 1;");
        r.forEachRow((s) -> {
            String id = s.getString(0);
            if (archon.update("DELETE FROM `jobs` WHERE `id` = '" + id + "' LIMIT 1;") > 0) {
                String f;
                KList<Object> pars = new KList<Object>(ServiceJob.builder().function(f = s.getString(1))
                        .parameters(s.getString(2)).id(ID.fromString(id)).build().decodeParameters());
                Object o = EDX.invoke(f, pars);

                if (logJobExecutions) {
                    String re = o == null ? "null" : o.toString();
                    L.v("Executed Job " + id + " " + f + "(" + pars.toString(", ") + ") -> " + re);
                }
            } else {
                L.w("Skipping job execution: " + id + " because upon deleting the job, it was already deleted. Pretty sure another " + serviceType + " is executing it already.");
            }
        });
    }
}
