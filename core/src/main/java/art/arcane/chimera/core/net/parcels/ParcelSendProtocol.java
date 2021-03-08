package art.arcane.chimera.core.net.parcels;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.chimera.core.protocol.generation.ProtoFunction;
import art.arcane.quill.collections.KList;
import art.arcane.quill.web.Parcel;
import art.arcane.quill.web.Parcelable;
import lombok.Getter;

public class ParcelSendProtocol extends Parcel {
    @Getter
    private KList<ProtoFunction> functions = Chimera.delegate instanceof ChimeraBackendService ? ((ChimeraBackendService) Chimera.delegate).getFunctions() : new KList<>();

    public ParcelSendProtocol() {
        super("sendprotocol");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
