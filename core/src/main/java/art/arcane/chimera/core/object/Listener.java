package art.arcane.chimera.core.object;

import art.arcane.archon.element.Element;
import art.arcane.archon.element.Identity;
import art.arcane.archon.element.Type;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.collections.ID;
import art.arcane.quill.math.M;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Dart
@Builder
public class Listener extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();

    private ID target;

    @Type("VARCHAR(64)")
    private String session;

    @Builder.Default
    private long time = M.ms();

    @Override
    public String getTableName() {
        return "listener";
    }
}
