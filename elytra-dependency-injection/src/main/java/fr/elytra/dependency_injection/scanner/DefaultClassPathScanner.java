package fr.elytra.dependency_injection.scanner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

/**
 * ClassPathScanner implementation.
 */
public class DefaultClassPathScanner implements ClassPathScanner {

    @Override
    public Set<ClassPathInfoInterface> scan(String packageName) throws IOException, URISyntaxException {
        ClassLoader classLoader = DefaultClassPathScanner.class.getClassLoader();

        String packageUrl = packageName.replace('.', '/');
        URL packageURL = classLoader.getResource("/" + packageUrl);
        switch (packageURL.getProtocol()) {
            case "jar":
                return new JarClassPathScanner().scan(packageName);
            default:
                return new CurrentClassPathScanner().scan(packageName);
        }
    }

}
