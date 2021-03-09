package art.arcane.chimera.core.object.account;

import art.arcane.archon.element.Element;
import art.arcane.archon.element.Identity;
import art.arcane.archon.element.Type;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.collections.ID;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Dart
@Data
@Builder
@NoArgsConstructor
public class User extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();

    @Type("VARCHAR(50)")
    @Builder.Default
    private String firstName = "Jackson";

    @Type("VARCHAR(50)")
    @Builder.Default
    private String lastName = "Doe";

    @Type("VARCHAR(64)")
    @Builder.Default
    private String email = "poof@arcane.art";

    @Builder.Default
    private long createdDate = -1;

    @Builder.Default
    private boolean suspended = false;

    public User(ID id) {
        this.id = id;
    }

    public User(ID id, String email) {
        this(id);
        this.email = email;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public String getTableName() {
        return "user";
    }
}
