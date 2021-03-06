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
import art.arcane.quill.io.IO;
import art.arcane.quill.logging.L;
import art.arcane.quill.random.RNG;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Dart
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthentication extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();

    @Builder.Default
    @Type("VARCHAR(64)")
    private String password = "";

    @Builder.Default
    @Type("VARCHAR(32)")
    private String salt = "";

    @Builder.Default
    @Type("VARCHAR(32)")
    private String pepper = "";

    public UserAuthentication(ID id) {
        this.id = id;
    }

    public UserAuthentication(ID id, String password) {
        this.id = id;
        setPassword(password);
    }

    public UserAuthentication setPassword(String password) {
        this.salt = RNG.r.s(32);
        this.pepper = RNG.r.s(32);
        this.password = season(password.toLowerCase());
        return this;
    }

    public boolean auth(String password) {
        L.i("INPUT " + password);
        L.i("SEASONED INPUT " + season(password.toLowerCase()));
        L.i("SEASONED: " + getSeasonedPassword());

        return getSeasonedPassword().equals(season(password.toLowerCase()));
    }

    public String getSeasonedPassword() {
        return password;
    }

    public String season(String hash) {
        return IO.hash(pepper.toLowerCase() + hash.substring(32).toUpperCase() + salt.toUpperCase() + hash.substring(0, 32).toLowerCase() + salt + hash.toUpperCase().substring(24, 48) + pepper);
    }

    @Override
    public String getTableName() {
        return "user_auth";
    }
}
