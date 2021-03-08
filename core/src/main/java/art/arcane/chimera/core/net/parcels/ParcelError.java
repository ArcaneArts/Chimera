package art.arcane.chimera.core.net.parcels;

import art.arcane.quill.web.Parcel;
import art.arcane.quill.web.Parcelable;

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
