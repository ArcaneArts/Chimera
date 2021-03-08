package art.arcane.chimera.core.net.parcels;

import art.arcane.quill.web.Parcel;
import art.arcane.quill.web.Parcelable;

public class ParcelPong extends Parcel {
    public ParcelPong() {
        super("pong");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
