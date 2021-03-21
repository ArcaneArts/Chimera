package art.arcane.chimera.mail;

import art.arcane.chimera.core.Chimera;
import art.arcane.chimera.core.microservice.ChimeraService;
import art.arcane.chimera.core.protocol.generation.Protocol;
import art.arcane.quill.service.Service;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MailService extends ChimeraService {
    public static void main(String[] a) {
        Chimera.start(a);
    }

    @Service
    @Protocol
    private ProtoMail mail = new ProtoMail();

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}