package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;

public class ParcelError extends Parcel {
    private String error;

    public ParcelError(String error) {
        super("error");
        this.error = error;
    }

    public ParcelError() {
        this("Internal Server Error (Unknown Reason)");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
