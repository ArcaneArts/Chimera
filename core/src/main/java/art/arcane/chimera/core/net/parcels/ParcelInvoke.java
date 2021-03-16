package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.protocol.ChimeraContext;
import art.arcane.chimera.core.util.web.Parcel;
import art.arcane.chimera.core.util.web.Parcelable;
import art.arcane.quill.collections.KList;
import lombok.Setter;

public class ParcelInvoke extends Parcel {
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
    public Parcelable respond() {
        Object result = Chimera.backend.getProtocolAccess().executeTypeWithContext(context, null, method, parameters.toArray(new Object[0]));

        return new ParcelResult(result);
    }
}
