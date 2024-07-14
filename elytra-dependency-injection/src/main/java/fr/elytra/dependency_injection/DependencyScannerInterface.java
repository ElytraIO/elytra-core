package fr.elytra.dependency_injection;

import java.util.Collection;

/**
 * The dependency scanner interface
 */
public interface DependencyScannerInterface {

    /**
     * Get the dependencies of a class
     * 
     * @param clazz The class to scan
     * @return The dependencies
     */
    Collection<DependencyData> getDependencies(Class<?> clazz);

}
