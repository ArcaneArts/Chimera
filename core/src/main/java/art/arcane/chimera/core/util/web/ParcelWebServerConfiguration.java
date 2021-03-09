package art.arcane.chimera.core.util.web;

public class ParcelWebServerConfiguration {
    private transient ParcelWebServer server;
    private boolean https;
    private boolean http;
    private int httpsPort;
    private int httpPort;
    private String sslKeystore;
    private String sslKeystorePassword;
    private String sslKeystoreKeyName;
    private String sslKeystoreKeyPassword;
    private String serverPath;
    private int maxFormContentSize;

    public ParcelWebServerConfiguration() {
        this(null);
    }

    public ParcelWebServerConfiguration(ParcelWebServer server) {
        this.server = server;
        this.http = true;
        this.https = false;
        this.httpsPort = 8443;
        this.httpPort = 80;
        sslKeystore = "";
        sslKeystoreKeyName = "";
        sslKeystoreKeyPassword = "";
        sslKeystorePassword = "";
        serverPath = "/";
        maxFormContentSize = 1024 * 1024 * 1024;
    }

    public ParcelWebServer applySettings() {
        return server;
    }

    public boolean https() {
        return https;
    }

    public ParcelWebServerConfiguration https(boolean https) {
        this.https = https;
        return this;
    }

    public boolean http() {
        return http;
    }

    public ParcelWebServerConfiguration http(boolean http) {
        this.http = http;
        return this;
    }

    public int httpsPort() {
        return httpsPort;
    }

    public ParcelWebServerConfiguration httpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
        return this;
    }

    public int httpPort() {
        return httpPort;
    }

    public ParcelWebServerConfiguration httpPort(int httpPort) {
        this.httpPort = httpPort;
        return this;
    }

    public String sslKeystore() {
        return sslKeystore;
    }

    public ParcelWebServerConfiguration sslKeystore(String sslKeystore) {
        this.sslKeystore = sslKeystore;
        return this;
    }

    public String sslKeystorePassword() {
        return sslKeystorePassword;
    }

    public ParcelWebServerConfiguration sslKeystorePassword(String sslKeystorePassword) {
        this.sslKeystorePassword = sslKeystorePassword;
        return this;
    }

    public String sslKeystoreKeyName() {
        return sslKeystoreKeyName;
    }

    public ParcelWebServerConfiguration sslKeystoreKeyName(String sslKeystoreKeyName) {
        this.sslKeystoreKeyName = sslKeystoreKeyName;
        return this;
    }

    public String sslKeystoreKeyPassword() {
        return sslKeystoreKeyPassword;
    }

    public ParcelWebServerConfiguration sslKeystoreKeyPassword(String sslKeystoreKeyPassword) {
        this.sslKeystoreKeyPassword = sslKeystoreKeyPassword;
        return this;
    }

    public int maxFormContentSize() {
        return maxFormContentSize;
    }

    public ParcelWebServerConfiguration maxFormContentSize(int maxFormContentSize) {
        this.maxFormContentSize = maxFormContentSize;
        return this;
    }

    public String serverPath() {
        return serverPath;
    }

    public ParcelWebServerConfiguration serverPath(String serverPath) {
        this.serverPath = serverPath;
        return this;
    }
}