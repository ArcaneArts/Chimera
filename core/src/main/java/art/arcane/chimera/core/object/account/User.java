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
@Table("user")
public class User {
    @Getter
    @Setter
    @Column(name = "id", type = ID.SQTYPE, placeholder = "UNDEFINED", primary = true)
    private ID id;

    @Getter
    @Setter
    @Column(name = "first_name", type = "VARCHAR(50)", placeholder = "Jackson")
    private String firstName;

    @Getter
    @Setter
    @Column(name = "last_name", type = "VARCHAR(50)", placeholder = "McWhirt")
    private String lastName;

    @Getter
    @Setter
    @Column(name = "email", type = "VARCHAR(64)", placeholder = "undefined@error.error")
    private String email;

    @Getter
    @Setter
    @Column(name = "created_on", type = "BIGINT", placeholder = "-1")
    private long createdDate;

    @Getter
    @Column(name = "suspended", type = "TINYINT", placeholder = "0")
    private int suspended;

    public User(ID id) {
        this.id = id;
        suspended = 0;
    }

    public User(ID id, String email) {
        this(id);
        this.email = email;
    }

    public void setSuspended(boolean b) {
        suspended = b ? 1 : 0;
    }

    public boolean isSuspended() {
        return suspended == 1;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
