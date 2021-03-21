package art.arcane.chimera.core.object.account;

import art.arcane.archon.element.Element;
import art.arcane.archon.element.Identity;
import art.arcane.archon.element.Type;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.collections.ID;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Dart
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPersonal extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();

    @Type("VARCHAR(10)")
    @Builder.Default
    private String phone = "";

    @Type("VARCHAR(64)")
    @Builder.Default
    private String carrier = "";

    @Override
    public String getTableName() {
        return "user_personal";
    }
}
