package art.arcane.chimera.core.object.account;

import art.arcane.archon.element.Element;
import art.arcane.archon.element.Identity;
import art.arcane.archon.element.Type;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.collections.ID;
import art.arcane.quill.io.IO;
import art.arcane.quill.logging.L;
import art.arcane.quill.random.RNG;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Dart
public class UserAuthentication extends Element {
    @Identity
    @Builder.Default
    private ID id = new ID();

    @Type("VARCHAR(64)")
    private String password;

    @Type("VARCHAR(32)")
    private String salt;

    @Type("VARCHAR(32)")
    private String pepper;

    public UserAuthentication(ID id) {
        this.id = id;
        password = "";
        salt = "";
        pepper = "";
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
