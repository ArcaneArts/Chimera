/*
 * This file is part of Chimera by Arcane Arts.
 *
 * Chimera by Arcane Arts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Chimera by Arcane Arts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License in this package for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chimera.  If not, see <https://www.gnu.org/licenses/>.
 */

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
