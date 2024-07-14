package fr.elytra.dependency_injection;

import java.util.Set;

/**
 * The packages scan provider
 */
public interface PackagesScanProvider {
    
    /**
     * Get the packages to scan
     * 
     * @return The packages to scan
     */
    Set<String> getPackages();
    
}
