package fr.elytra.dependency_injection.scanner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.ClassPath.ResourceInfo;

public class GuavaPathScanner implements ClassPathScanner {

    @Override
    public Set<ClassPathInfoInterface> scan(String packageName) throws IOException, URISyntaxException {
        ClassPath classPath = ClassPath.from(GuavaPathScanner.class.getClassLoader());
        if(packageName.isBlank()) {
            return classPath.getTopLevelClasses().stream()
                    .map(GuavaPathInfo::new)
                    .collect(Collectors.toSet());
        }
        return classPath.getTopLevelClassesRecursive(
                packageName).stream()
                .map(GuavaPathInfo::new)
                .collect(Collectors.toSet());
    }

    public record GuavaPathInfo(
            ClassInfo classInfo) implements ClassPathInfoInterface {

        @Override
        public String classPath() {
            return classInfo.getResourceName();
        }

        @Override
        public String className() {
            return classInfo.getName();
        }

        @Override
        public ClassLoader loader() {
            try {
                Field field = ResourceInfo.class.getDeclaredField("loader");
                field.setAccessible(true);
                return (ClassLoader) field.get(classInfo);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        public Class<?> loadClass() throws ClassNotFoundException {
            return loader().loadClass(className());
        }

        @Override
        public byte[] readAllBytes() throws Exception {
            return classInfo.asByteSource().read();
        }

    }

}
