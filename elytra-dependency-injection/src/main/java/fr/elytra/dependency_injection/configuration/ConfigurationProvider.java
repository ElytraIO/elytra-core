package fr.elytra.dependency_injection.configuration;

import java.util.Map;

/**
 * Configuration loader

 */
public interface ConfigurationProvider {
    
    /**
     * Load the configuration
     * Each property is a key-value pair, the value are all boxed types, String, List or Map of boxed types
     * Each key is a string, there is no nested key
     * Each key is unique, and each section is separated by a dot
     * 
     * @return The configuration
     */
    Map<String, Object> loadConfiguration();

}
