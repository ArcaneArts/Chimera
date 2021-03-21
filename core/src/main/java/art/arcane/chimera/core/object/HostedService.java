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

package art.arcane.chimera.core.object;

import art.arcane.archon.element.Element;
import art.arcane.archon.element.Identity;
import art.arcane.archon.element.Type;
import art.arcane.quill.collections.ID;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HostedService extends Element {
    @Type("SMALLINT")
    @Builder.Default
    public int health = 100;
    @Identity
    @Builder.Default
    private ID id = new ID();
    @Builder.Default
    @Type("VARCHAR(64)")
    private String type = "";
    @Builder.Default
    @Type("VARCHAR(64)")
    private String address = "localhost";
    @Builder.Default
    @Type("VARCHAR(64)")
    private String dir = "/";
    @Builder.Default
    private long time = 0;
    @Builder.Default
    private int port = 0;

    public String getURL() {
        return "http://" + address + ":" + port + (dir.startsWith("/") ? dir : ("/" + dir));
    }

    @Override
    public String getTableName() {
        return "service";
    }
}
