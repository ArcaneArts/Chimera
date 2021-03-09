package art.arcane.chimera.core.util.web;

import art.arcane.quill.collections.KList;

public interface Parcelable {
    public String getParcelType();

    public KList<String> getParameterNames();

    public String getExample();
}
