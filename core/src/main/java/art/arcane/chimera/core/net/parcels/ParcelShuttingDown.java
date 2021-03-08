package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.quill.execution.J;
import art.arcane.quill.web.Parcel;
import art.arcane.quill.web.Parcelable;
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
        J.a(() -> ((ChimeraBackendService) Chimera.delegate).getBackendService().notifyRemoteShuttingDown(getKey()));
        return new ParcelOK();
    }
}
