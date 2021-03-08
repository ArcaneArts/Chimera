package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.quill.collections.KList;
import art.arcane.quill.web.DownloadParcelable;
import art.arcane.quill.web.Parcel;
import art.arcane.quill.web.Parcelable;
import lombok.Setter;

import java.io.InputStream;

public class ParcelInvokeDownstream extends Parcel implements DownloadParcelable {
    @Setter
    private String method = "";
    @Setter
    private KList<Object> parameters = new KList<>();
    @Setter
    private ChimeraContext context = null;

    public ParcelInvokeDownstream() {
        super("invoke-downstream");
    }

    @Override
    public Parcelable respond() {
        InputStream result = ((ChimeraBackendService) Chimera.delegate).getBackendService().getProtocolAccess().executeTypeDownstreamWithContext(context, null, method, parameters.toArray(new Object[0]));

        if (result == null) {
            return new ParcelError("Null Downstream for " + method);
        }

        return new ParcelResultDownstream(result);
    }

    @Override
    public InputStream getStream() {
        return null;
    }

    @Override
    public String getName() {
        return getNode();
    }
}
