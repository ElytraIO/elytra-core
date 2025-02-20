package fr.elytra.dependency_injection.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
        ElementType.FIELD, ElementType.PARAMETER
})
@Retention(RetentionPolicy.SOURCE)
public @interface Inject {

}
