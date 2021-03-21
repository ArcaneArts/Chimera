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

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.chimera.core.util.web.DownloadParcelable;
import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;
import art.arcane.quill.collections.KList;
import lombok.Setter;

import java.io.InputStream;

public class ParcelInvokeDownstream extends Parcel implements DownloadParcelable {
    @Setter
    private String method = "";
    @Setter
    private KList<Object> parameters = new KList<>();
    @Setter
    private ChimeraContext context = null;

    public ParcelInvokeDownstream() {
        super("invoke-downstream");
    }

    @Override
    public Parcelable respond() {
        InputStream result = Chimera.backend.getProtocolAccess().executeTypeDownstreamWithContext(context, null, method, parameters.toArray(new Object[0]));

        if (result == null) {
            return new ParcelError("Null Downstream for " + method);
        }

        return new ParcelResultDownstream(result);
    }

    @Override
    public InputStream getStream() {
        return null;
    }

    @Override
    public String getName() {
        return getNode();
    }
}
