package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.quill.collections.KList;
import lombok.Setter;

public class ParcelInvoke extends art.arcane.quill.web.Parcel {
    @Setter
    private String method = "";
    @Setter
    private KList<Object> parameters = new KList<>();
    @Setter
    private ChimeraContext context = null;

    public ParcelInvoke() {
        super("invoke");
    }

    @Override
    public art.arcane.quill.web.Parcelable respond() {
        Object result = ((ChimeraBackendService) Chimera.delegate).getBackendService().getProtocolAccess().executeTypeWithContext(context, null, method, parameters.toArray(new Object[0]));

        return new ParcelResult(result);
    }
}
