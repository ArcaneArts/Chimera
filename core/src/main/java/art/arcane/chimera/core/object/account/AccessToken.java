package art.arcane.chimera.core.object.account;

import art.arcane.chimera.core.object.ID;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.math.M;
import art.arcane.quill.sql.Column;
import art.arcane.quill.sql.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Dart
@Data
@Table("user_access")
public class AccessToken {
    @Getter
    @Setter
    @Column(name = "id", type = ID.SQTYPE, placeholder = "UNDEFINED", primary = true)
    private ID id;

    @Getter
    @Setter
    @Column(name = "account", type = ID.SQTYPE, placeholder = "UNDEFINED")
    private ID account;

    @Getter
    @Setter
    @Column(name = "type", type = "VARCHAR(36)", placeholder = "normal")
    private String type;

    @Getter
    @Setter
    @Column(name = "lastUse", type = "BIGINT", placeholder = "-1")
    private long lastUse;

    public AccessToken(ID id) {
        this.id = id;
        lastUse = M.ms();
        type = "normal";
        account = ID.randomUUID();
    }
}
