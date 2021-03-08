package art.arcane.chimera.core.net.parcels;

import art.arcane.quill.web.Parcel;
import art.arcane.quill.web.Parcelable;

public class ParcelPing extends Parcel {
    public ParcelPing() {
        super("ping");
    }

    @Override
    public Parcelable respond() {
        return new ParcelPong();
    }
}
