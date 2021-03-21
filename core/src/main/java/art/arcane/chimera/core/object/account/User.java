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
public class User extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();

    @Type("VARCHAR(50)")
    @Builder.Default
    private String firstName = "Jackson";

    @Type("VARCHAR(50)")
    @Builder.Default
    private String lastName = "McWhirt";

    @Type("VARCHAR(64)")
    @Builder.Default
    private String email = "someone@somewhere.here";

    @Builder.Default
    private long createdDate = M.ms();

    @Builder.Default
    private boolean suspended = false;

    public User(ID id) {
        this.id = id;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public String getTableName() {
        return "user";
    }
}
