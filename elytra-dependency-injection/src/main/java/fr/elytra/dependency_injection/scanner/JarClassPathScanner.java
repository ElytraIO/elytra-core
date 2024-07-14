package fr.elytra.dependency_injection.scanner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * ClassPathScanner implementation for JAR files.
 */
public class JarClassPathScanner implements ClassPathScanner {

    Logger logger = Logger.getLogger(JarClassPathScanner.class.getName());

    @Override
    public Set<ClassPathInfoInterface> scan(String packageName) throws IOException, URISyntaxException {
        String packageUrl = packageName.replace('.', '/');
        CodeSource src = JarClassPathScanner.class.getProtectionDomain().getCodeSource();
        Set<ClassPathInfoInterface> classes = new HashSet<>();

        // Use CodeSource to harvest all classes in the JAR file
        if (src != null) {
            URL jar = src.getLocation();
            // NEW METHOD
            try (JarFile jarFile = new JarFile(jar.getFile())) {
                jarFile.stream()
                        .filter(jarEntry -> jarEntry.getName().startsWith(packageUrl)
                                && jarEntry.getName().endsWith(".class"))
                        .map(jarEntry -> {
                            String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
                            className = className.replace('/', '.');
                            return new ClassPathInfo(jarEntry.getName(), className,
                                    JarClassPathScanner.class.getClassLoader());
                        })
                        .forEach(classes::add);
            } catch (IOException e) {
                logger.severe("Error while scanning jar file");
                throw new IOException("Error while scanning jar file", e);
            }
        }
        return classes;
    }

}
