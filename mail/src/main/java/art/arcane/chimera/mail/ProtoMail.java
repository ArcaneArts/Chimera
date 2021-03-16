package art.arcane.chimera.mail;

import art.arcane.chimera.core.protocol.generation.ServiceFunction;
import art.arcane.chimera.mail.util.Email;
import art.arcane.chimera.mail.util.MailMan;
import art.arcane.quill.service.QuillService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProtoMail extends QuillService {
    private MailMan mailMan = new MailMan();

    @ServiceFunction
    public Boolean sendMail(String emailAddress, String subject, String message) {
        Email.to(emailAddress).subject(subject).message(message).send(getMailMan());
        return true;
    }

    @ServiceFunction
    public Boolean sendMailHtml(String emailAddress, String subject, String html) {
        Email.to(emailAddress).subject(subject).html(html).send(getMailMan());
        return true;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
