package fr.elytra.dependency_injection;

/**
 * The container interface
 */
public interface ContainerInterface {

    /**
     * Initialize the container
     * Load all the objects from the configuration
     * and register them in the container.
     * Steps are:
     * <ul>
     * <li>Load the configuration</li>
     * <li>Load all PackageScanProvider</li>
     * <li>Load all the objects of the packages found</li>
     * </ul>
     * 
     * @throws Exception If an error occurred during the initialization
     */
    void init() throws Exception;

    /**
     * Initialize an object using the container
     * 
     * @param clazz The class of the object
     * @return The object initialized
     * @throws Exception If an error occurred during the initialization
     */
    <T> T init(Class<? extends T> clazz) throws Exception;

    /**
     * Register a new object in the container
     * 
     * @param name   The identifier of the object
     * @param object The object to register
     */
    void register(String name, Object object);

    /**
     * Get an object from the container
     * 
     * @param name The identifier of the object
     * @return The object
     */
    <T> T get(String name);

    /**
     * Get a service locator
     * 
     * @return The service locator of the container
     */
    ServiceLocator getServiceLocator();

}
