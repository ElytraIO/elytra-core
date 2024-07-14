package fr.elytra.dependency_injection.injector;

import java.lang.annotation.ElementType;
import java.lang.reflect.Parameter;

/**
 * Dependency element
 */
public interface DependencyElement {

    /**
     * Get the type of the element
     * 
     * @return The type of the element
     */
    ElementType getType();

    record ParameterElement(Parameter parameter) implements DependencyElement {
        @Override
        public ElementType getType() {
            return ElementType.PARAMETER;
        }
    }

}
