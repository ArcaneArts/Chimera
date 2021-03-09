package art.arcane.chimera.core.object;

import art.arcane.archon.element.Element;
import art.arcane.archon.element.Identity;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.collections.ID;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Dart
@Builder
public class Session extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();
    private ID gateway;
    private ID token;
    private ID user;
    @Builder.Default
    private long last = 0;

    @Override
    public String getTableName() {
        return "session";
    }
}
