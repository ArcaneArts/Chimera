package art.arcane.chimera.core.util.web;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface ParcelResponseError {
    public Class<? extends Parcelable> type();

    public String reason();
}
