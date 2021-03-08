package art.arcane.chimera.paragon.api;

import java.util.Locale;

public class Platforms {
    public static boolean isWindows() {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).indexOf("win") >= 0;
    }

    public static boolean isMac() {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).indexOf("mac") >= 0 || System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).indexOf("darwin") >= 0;
    }
}
