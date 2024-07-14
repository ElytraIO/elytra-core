package fr.elytra.dependency_injection.scanner;

import java.lang.reflect.Field;

import com.google.common.reflect.ClassPath;

public record ClassPathInfo(
        String classPath,
        String className,
        ClassLoader loader) implements ClassPathInfoInterface {

    static ClassPathInfo from(ClassPath.ClassInfo classInfo) {
        ClassLoader loader = null;
        try {
            Field field = ClassPath.ResourceInfo.class.getDeclaredField("loader");
            field.setAccessible(true);
            loader = (ClassLoader) field.get(classInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ClassPathInfo(classInfo.getResourceName(), classInfo.getName(), loader);
    }

    public Class<?> loadClass() throws ClassNotFoundException {
        return loader.loadClass(className);
    }

    @Override
    public byte[] readAllBytes() throws Exception {
        return loader.getResource(classPath).openStream().readAllBytes();
    }

}
