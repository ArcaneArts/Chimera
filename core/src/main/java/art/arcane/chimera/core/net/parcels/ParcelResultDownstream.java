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

import art.arcane.chimera.core.util.web.DownloadParcelable;
import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;
import lombok.Setter;

import java.io.InputStream;

public class ParcelResultDownstream extends Parcel implements DownloadParcelable {
    @Setter
    private InputStream stream;

    public ParcelResultDownstream(InputStream stream) {
        this();
        this.stream = stream;
    }

    public ParcelResultDownstream() {
        super("result-downstream");
    }

    @Override
    public Parcelable respond() {
        return null;
    }

    @Override
    public InputStream getStream() {
        return stream;
    }

    @Override
    public String getName() {
        return "stream";
    }
}
