package art.arcane.chimera.core.net.parcels;

import art.arcane.quill.web.DownloadParcelable;
import art.arcane.quill.web.Parcel;
import art.arcane.quill.web.Parcelable;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

public class ParcelResultDownstream extends Parcel implements DownloadParcelable {
    @Getter
    @Setter
    private InputStream stream;

    public ParcelResultDownstream(InputStream stream) {
        this();
        this.stream = stream;
    }

    public ParcelResultDownstream() {
        super("result-downstream");
    }

    @Override
    public Parcelable respond() {
        return null;
    }

    @Override
    public InputStream getStream() {
        return stream;
    }

    @Override
    public String getName() {
        return "stream";
    }
}
