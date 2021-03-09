package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;

public class ParcelPong extends Parcel {
    public ParcelPong() {
        super("pong");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
