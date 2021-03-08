package art.arcane.chimera.core.net.parcels;

import art.arcane.quill.web.Parcel;
import art.arcane.quill.web.Parcelable;

public class ParcelOK extends Parcel {
    public ParcelOK() {
        super("ok");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
