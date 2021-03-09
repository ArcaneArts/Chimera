package art.arcane.chimera.core.net.parcels;


import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;

public class ParcelGetProtocol extends Parcel {
    public ParcelGetProtocol() {
        super("protocol");
    }

    @Override
    public Parcelable respond() {
        return new ParcelSendProtocol();
    }
}
