package art.arcane.chimera.core.object.guide;

import art.arcane.chimera.core.object.ID;
import art.arcane.chimera.core.protocol.generation.Dart;
import lombok.Data;

@Dart
@Data
public class GuideComponent {
    private String name = "";
    private ID id = ID.randomUUID();
    private String type;
}
