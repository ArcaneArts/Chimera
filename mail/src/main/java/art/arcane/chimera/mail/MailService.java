package art.arcane.chimera.mail;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraBackendService;
import art.arcane.chimera.core.protocol.generation.Protocol;
import art.arcane.quill.service.ServiceWorker;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MailService extends ChimeraBackendService {
    public static void main(String[] a) {
        Chimera.start(a);
    }

    @ServiceWorker
    @Protocol
    private ProtoMail mail = new ProtoMail();

    public MailService() {
        super("Mail");
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
