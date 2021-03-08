package art.arcane.quill.sql;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Column {
    String name();

    String type();

    String placeholder() default "<ERROR: UNDEFINED>";

    boolean primary() default false;
}
