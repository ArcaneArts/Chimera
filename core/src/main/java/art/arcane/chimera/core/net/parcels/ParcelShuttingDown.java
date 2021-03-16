package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;
import lombok.Getter;
import lombok.Setter;

public class ParcelShuttingDown extends Parcel {
    @Getter
    @Setter
    private String key = null;

    public ParcelShuttingDown(String key) {
        this();
        this.key = key;
    }

    public ParcelShuttingDown() {
        super("isleepnow");
    }

    @Override
    public Parcelable respond() {
        // TODO: J.a(() -> ((ChimeraBackendService) Chimera.delegate).getBackendService().notifyRemoteShuttingDown(getKey()));
        return new ParcelOK();
    }
}
