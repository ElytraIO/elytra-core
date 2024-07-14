package fr.elytra.dependency_injection.impl;

import java.util.Set;

import fr.elytra.dependency_injection.PackagesScanProvider;

public class DefaultPackageScanProvider implements PackagesScanProvider{

    @Override
    public Set<String> getPackages() {
        return Set.of("fr.elytra");
    }
    
}
