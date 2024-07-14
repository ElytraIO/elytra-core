package fr.elytra.dependency_injection.scanner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * Interface for scanning the classpath.
 */
public interface ClassPathScanner {

    /**
     * Scans the classpath for classes in the given package.
     * 
     * @param packageName The package to scan.
     * @return A list of class pathes.
     * @throws IOException
     * @throws URISyntaxException
     */
    Set<ClassPathInfoInterface> scan(String packageName) throws IOException, URISyntaxException;

}
