package fr.elytra.processor.plugins;

import fr.elytra.dependency_injection.filter.ClassSubsetData;

public interface PluginFileDefinitionGenerator {

    /**
     * Get the file name
     * 
     * @return The file name and extension
     */
    String getFileName();
    
    /**
     * Generate the file
     * 
     * @param data The data to generate the file
     * @param lazyClass The plugin class, lazy loaded (not initialized by the class loader)
     * @return The file content
     */
    String generateFile(PluginDefinitionData data, ClassSubsetData lazyClass);

}
