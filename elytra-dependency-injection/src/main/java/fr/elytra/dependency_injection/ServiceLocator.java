package fr.elytra.dependency_injection;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * The service locator
 */
public interface ServiceLocator {

    /**
     * Get a service from the container
     * 
     * @param prefix The prefix of the service
     * @return The services
     */
    Collection<Object> getServices(String prefix);

    /**
     * Get a service from the container
     * 
     * @param interfaceClass The interface of the service
     * @return The services
     */
    <T> Collection<? extends T> getServices(Class<? extends T> interfaceClass);

    /**
     * Get a service from the container
     * 
     * @param predicate The predicate to filter the services
     * @return The services
     */
    Collection<Object> getServices(Predicate<Object> predicate);

    /**
     * Get all services from the container
     * 
     * @return The services
     */
    Collection<Object> getServices();
}
