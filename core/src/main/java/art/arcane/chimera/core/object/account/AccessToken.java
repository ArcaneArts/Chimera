package art.arcane.chimera.core.object.account;

import art.arcane.archon.element.Element;
import art.arcane.archon.element.Identity;
import art.arcane.archon.element.Type;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.collections.ID;
import art.arcane.quill.math.M;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Dart
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessToken extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();

    private ID account;

    @Type("VARCHAR(36)")
    @Builder.Default
    private String type = "normal";

    @Builder.Default
    private long lastUse = M.ms();

    @Override
    public String getTableName() {
        return "user_access";
    }
}
