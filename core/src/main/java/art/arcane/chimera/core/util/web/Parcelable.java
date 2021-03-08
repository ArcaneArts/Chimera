package art.arcane.quill.web;

import art.arcane.quill.collections.KList;

public interface Parcelable {
    public String getParcelType();

    public KList<String> getParameterNames();

    public String getExample();
}
