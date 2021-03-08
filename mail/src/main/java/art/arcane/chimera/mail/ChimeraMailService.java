package art.arcane.chimera.mail;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.chimera.core.microservice.ServiceWorker;
import art.arcane.chimera.core.protocol.generation.Protocol;

public class ChimeraMailService extends ChimeraBackendService {
    public static void main(String[] a) {
        Chimera.start(a);
    }

    @ServiceWorker
    @Protocol
    private ProtoMail mail = new ProtoMail();

    public ChimeraMailService() {
        super("Mail");
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
