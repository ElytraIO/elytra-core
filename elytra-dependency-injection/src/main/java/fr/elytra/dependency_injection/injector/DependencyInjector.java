package fr.elytra.dependency_injection.injector;

import fr.elytra.dependency_injection.ContainerInterface;

/**
 * Dependency injector
 */
public interface DependencyInjector {

    /**
     * Get an object from the container
     * 
     * The DependencyInjector must return null and not throw an exception if the object is not found
     * 
     * @param containerInterface The container
     * @param dependencyElement  The element to inject
     * @return The object, or null if not found
     */
    Object get(ContainerInterface containerInterface, DependencyElement dependencyElement);

}
