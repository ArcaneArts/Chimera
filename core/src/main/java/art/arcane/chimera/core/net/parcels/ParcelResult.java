package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;
import lombok.Getter;
import lombok.Setter;

public class ParcelResult extends Parcel {
    @Getter
    @Setter
    private Object result = null;

    public ParcelResult(Object o) {
        this();
        this.result = o;
    }

    public ParcelResult() {
        super("result");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
