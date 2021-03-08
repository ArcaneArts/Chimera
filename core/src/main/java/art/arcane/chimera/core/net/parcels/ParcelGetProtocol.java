package art.arcane.chimera.core.net.parcels;

import art.arcane.quill.web.Parcel;
import art.arcane.quill.web.Parcelable;

public class ParcelGetProtocol extends Parcel {
    public ParcelGetProtocol() {
        super("protocol");
    }

    @Override
    public Parcelable respond() {
        return new ParcelSendProtocol();
    }
}
