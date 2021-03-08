package art.arcane.quill.web;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface ParcelResponseSuccess {
    public Class<? extends Parcelable> type();

    public String reason();
}
