package art.arcane.chimera.paragon;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.chimera.core.microservice.ServiceWorker;
import art.arcane.chimera.core.protocol.generation.Protocol;

public class ParagonService extends ChimeraBackendService {
    public static void main(String[] a) {
        Chimera.start(a);
    }

    @ServiceWorker
    @Protocol
    private ProtoParagon paragon = new ProtoParagon();

    public ParagonService() {
        super("Paragon");
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }
}
