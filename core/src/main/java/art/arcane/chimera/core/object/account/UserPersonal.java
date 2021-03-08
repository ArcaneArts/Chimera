package art.arcane.chimera.core.object.account;

import art.arcane.chimera.core.object.ID;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.sql.Column;
import art.arcane.quill.sql.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Dart
@Data
@Table("user_personal")
public class UserPersonal {
    @Getter
    @Setter
    @Column(name = "id", type = ID.SQTYPE, placeholder = "UNDEFINED", primary = true)
    private ID id;

    @Getter
    @Setter
    @Column(name = "phone", type = "VARCHAR(10)", placeholder = "")
    private String phone;

    @Getter
    @Setter
    @Column(name = "carrier", type = "VARCHAR(32)", placeholder = "")
    private String carrier;

    public UserPersonal(ID id) {
        this.id = id;
        phone = "";
        carrier = "";
    }
}
