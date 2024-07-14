package fr.elytra.dependency_injection.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to define a class as a provider.
 * A provider is a class that contains Methods producing services
 */
@Target({
        ElementType.TYPE
})
@Retention(RetentionPolicy.SOURCE)
public @interface Provider {

    String value() default "";

}
