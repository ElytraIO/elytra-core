package fr.elytra.dependency_injection.scanner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultipleClassPathScanner implements ClassPathScanner {

    private List<ClassPathScanner> scanners = new ArrayList<>();

    public MultipleClassPathScanner(List<ClassPathScanner> scanners) {
        this.scanners = scanners;
    }

    public MultipleClassPathScanner() {
        this(new ArrayList<>());
        this.scanners.add(new GuavaPathScanner());
        this.scanners.add(new JarClassPathScanner());
        this.scanners.add(new CurrentClassPathScanner());
    }

    @Override
    public Set<ClassPathInfoInterface> scan(String packageName) throws IOException, URISyntaxException {
        Set<ClassPathInfoInterface> classes = new HashSet<>();
        for (ClassPathScanner scanner : scanners) {
            try {
                classes.addAll(scanner.scan(packageName));
            } catch (Exception e) {
            }
        }
        return classes;
    }

}
