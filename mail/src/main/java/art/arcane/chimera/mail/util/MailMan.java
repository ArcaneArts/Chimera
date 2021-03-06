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

package art.arcane.chimera.mail.util;

public class MailMan {
    private String server = "some.server.something";
    private int port = 123;
    private boolean tls = true;
    private boolean ssl = false;
    private boolean smtpAuth = true;
    private String email = "you@something.something";
    private String name = "Chimera";
    private String username = "you@something.something";
    private String password = "somethingSecure";

    public MailMan() {

    }

    public MailMan(String server, int port) {
        this.server = server;
        this.port = port;
        tls = true;
        ssl = false;
        smtpAuth = true;
    }

    public MailMan smtpAuth(boolean v) {
        this.smtpAuth = v;
        return this;
    }

    public MailMan email(String v) {
        this.email = v;
        return this;
    }

    public MailMan name(String v) {
        this.name = v;
        return this;
    }

    public MailMan smtpAuth(String username, String password) {
        return smtpAuth(true).username(username).password(password);
    }

    public MailMan password(String v) {
        this.password = v;
        return this;
    }

    public MailMan username(String v) {
        this.username = v;
        return this;
    }

    public MailMan tls(boolean v) {
        this.tls = v;
        return this;
    }

    public MailMan ssl(boolean v) {
        this.ssl = v;
        return this;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public boolean isTls() {
        return tls;
    }

    public boolean isSsl() {
        return ssl;
    }

    public boolean isSmtpAuth() {
        return smtpAuth;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
