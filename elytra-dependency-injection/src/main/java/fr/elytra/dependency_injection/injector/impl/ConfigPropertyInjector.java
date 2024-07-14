package fr.elytra.dependency_injection.injector.impl;

import java.lang.reflect.Parameter;

import fr.elytra.dependency_injection.ContainerInterface;
import fr.elytra.dependency_injection.annotations.ConfigProperty;
import fr.elytra.dependency_injection.injector.DependencyElement;
import fr.elytra.dependency_injection.injector.DependencyInjector;

public class ConfigPropertyInjector implements DependencyInjector{

    @Override
    public Object get(ContainerInterface containerInterface, DependencyElement dependencyElement) {
        switch(dependencyElement.getType()){
            case PARAMETER:
                Parameter parameter = ((DependencyElement.ParameterElement) dependencyElement).parameter();
                ConfigProperty configProperty = parameter.getAnnotation(ConfigProperty.class);
                return containerInterface.get(configProperty.value());
            default:
                break;
        }
        return null;
    }
    
}
