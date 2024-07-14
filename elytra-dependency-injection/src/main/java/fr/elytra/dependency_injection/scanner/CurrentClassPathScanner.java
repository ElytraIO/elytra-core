package fr.elytra.dependency_injection.scanner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * ClassPathScanner implementation.
 */
public class CurrentClassPathScanner implements ClassPathScanner {

    @Override
    public Set<ClassPathInfoInterface> scan(String packageName) throws IOException, URISyntaxException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
