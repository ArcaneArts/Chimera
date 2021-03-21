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

package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;
import lombok.Getter;
import lombok.Setter;

public class ParcelShuttingDown extends Parcel {
    @Getter
    @Setter
    private String key = null;

    public ParcelShuttingDown(String key) {
        this();
        this.key = key;
    }

    public ParcelShuttingDown() {
        super("isleepnow");
    }

    @Override
    public Parcelable respond() {
        // TODO: J.a(() -> ((ChimeraService) Chimera.delegate).getBackendService().notifyRemoteShuttingDown(getKey()));
        return new ParcelOK();
    }
}
