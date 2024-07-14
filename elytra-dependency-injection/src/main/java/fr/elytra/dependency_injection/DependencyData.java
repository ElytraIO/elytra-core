package fr.elytra.dependency_injection;

import java.util.Optional;

public record DependencyData(
    Class<?> clazz,
    Optional<String> serviceName
) {
    
}
