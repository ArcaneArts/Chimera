package art.arcane.quill.web;

import art.arcane.quill.collections.KList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface DownloadParcelable extends Parcelable {
    public InputStream getStream();

    public String getName();

    public static DownloadParcelable of(File file) throws FileNotFoundException {
        return of(new FileInputStream(file), file.getName());
    }

    public static DownloadParcelable of(InputStream in, String fileName) {
        return new DownloadParcelable() {
            @Override
            public String getParcelType() {
                return "fancyparcel";
            }

            public String getName() {
                return fileName;
            }

            @Override
            public KList<String> getParameterNames() {
                return new KList<String>();
            }

            @Override
            public InputStream getStream() {
                return in;
            }

            @Override
            public String getExample() {
                return "";
            }
        };
    }
}
