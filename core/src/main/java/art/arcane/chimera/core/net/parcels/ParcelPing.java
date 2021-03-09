package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;

public class ParcelPing extends Parcel {
    public ParcelPing() {
        super("ping");
    }

    @Override
    public Parcelable respond() {
        return new ParcelPong();
    }
}
