package fr.elytra.dependency_injection.injector.impl;

import java.lang.annotation.ElementType;
import java.lang.reflect.Modifier;

import fr.elytra.dependency_injection.ContainerInterface;
import fr.elytra.dependency_injection.ServiceLocator;
import fr.elytra.dependency_injection.injector.DependencyElement;
import fr.elytra.dependency_injection.injector.DependencyInjector;

public class InterfacesPropertyInjector implements DependencyInjector {

    @Override
    public Object get(ContainerInterface containerInterface, DependencyElement dependencyElement) {
        final ServiceLocator serviceLocator = containerInterface.getServiceLocator();
        switch (dependencyElement.getType()) {
            case ElementType.PARAMETER:
                var parameter = ((DependencyElement.ParameterElement) dependencyElement).parameter();
                if (parameter.getType().isInterface()
                        || Modifier.isAbstract(parameter.getType().getModifiers())) {
                    return serviceLocator.getServices(parameter.getType()).stream().findFirst()
                            .orElse(null);
                }
                break;
            default:
                break;
        }
        return null;
    }

}
