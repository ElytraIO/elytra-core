package fr.elytra.dependency_injection.filter;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public record ClassSubsetData(
        String className,
        String classPackage,
        String classPath,
        String superClassPath,
        List<String> interfacesPath,
        List<AnnotationData> classAnnotations,
        Map<String, List<AnnotationData>> fieldAnnotations,
        ClassType classType,
        int classModifiers) {

    public record AnnotationData(String name, Map<String, Object> values) {
    }

    public boolean isFinal() {
        return Modifier.isFinal(classModifiers);
    }

    public boolean isInterface() {
        return classType == ClassType.INTERFACE;
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(classModifiers);
    }

    public boolean isPublic() {
        return Modifier.isPublic(classModifiers);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(classModifiers);
    }

    public boolean isProtected() {
        return Modifier.isProtected(classModifiers);
    }

    public boolean hasSuperClass() {
        return superClassPath != null;
    }

    public boolean isSuperClass(Class<?> klass) {
        return superClassPath.equals(klass.getName());
    }

    public boolean hasInterface() {
        return !interfacesPath.isEmpty();
    }

    public boolean hasInterface(String interfaceName) {
        return interfacesPath.contains(interfaceName);
    }

    public boolean hasInterface(Class<?> klass) {
        return hasInterface(klass.getName());
    }

    public boolean hasAnnotations(Class<?> annotation) {
        return getAnnotation(annotation) != null;
    }

    public AnnotationData getAnnotation(Class<?> annotation) {
        return classAnnotations.stream().filter(a -> a.name().equals(annotation.getName())).findFirst().orElse(null);
    }

}
