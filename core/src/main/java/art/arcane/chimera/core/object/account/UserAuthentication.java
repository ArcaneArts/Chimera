package art.arcane.chimera.core.object.account;

import art.arcane.chimera.core.object.ID;
import art.arcane.chimera.core.protocol.generation.Dart;
import art.arcane.quill.io.IO;
import art.arcane.quill.logging.L;
import art.arcane.quill.random.RNG;
import art.arcane.quill.sql.Column;
import art.arcane.quill.sql.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Dart
@EqualsAndHashCode
@Table("user_auth")
public class UserAuthentication {
    @Getter
    @Setter
    @Column(name = "id", type = ID.SQTYPE, placeholder = "UNDEFINED", primary = true)
    private ID id;

    @Column(name = "password", type = "VARCHAR(64)", placeholder = "UNDEFINED")
    private String password;

    @Column(name = "salt", type = "VARCHAR(32)", placeholder = "UNDEFINED")
    private String salt;

    @Column(name = "pepper", type = "VARCHAR(32)", placeholder = "UNDEFINED")
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
}
