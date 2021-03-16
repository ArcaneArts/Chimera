package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;

public class ParcelOK extends Parcel {
    public ParcelOK() {
        super("ok");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
