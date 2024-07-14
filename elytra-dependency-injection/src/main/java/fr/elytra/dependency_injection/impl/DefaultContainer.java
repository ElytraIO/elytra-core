package fr.elytra.dependency_injection.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;

import fr.elytra.dependency_injection.ContainerInterface;
import fr.elytra.dependency_injection.PackagesScanProvider;
import fr.elytra.dependency_injection.ServiceLocator;
import fr.elytra.dependency_injection.annotations.Singleton;
import fr.elytra.dependency_injection.comparators.ObjectPriorityComparator;
import fr.elytra.dependency_injection.configuration.ConfigurationProvider;
import fr.elytra.dependency_injection.filter.ClassPathFilter;
import fr.elytra.dependency_injection.filter.DefaultClassPathFilter;
import fr.elytra.dependency_injection.filter.MultithreadedClassPathFilter;
import fr.elytra.dependency_injection.injector.DependencyElement;
import fr.elytra.dependency_injection.injector.DependencyInjector;
import fr.elytra.dependency_injection.scanner.ClassPathInfoInterface;
import fr.elytra.dependency_injection.scanner.ClassPathScanner;
import fr.elytra.dependency_injection.scanner.GuavaPathScanner;

public final class DefaultContainer implements ContainerInterface {

    private static DefaultContainer INSTANCE = new DefaultContainer();

    public synchronized static DefaultContainer getInstance() {
        if (!INSTANCE.isInitialized()) {
            try {
                INSTANCE.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return INSTANCE;
    }

    private Map<String, SortedSet<Object>> objects = new HashMap<>();
    private boolean initialized = false;

    private DefaultContainer() {

    }

    @Override
    public synchronized void init() throws Exception {
        if (initialized) {
            throw new IllegalStateException("Container already initialized");
        }
        ClassPathScanner classPathScanner = new GuavaPathScanner();
        ClassPathFilter classPathFilter = new MultithreadedClassPathFilter();
        var classPathInterfaces = classPathScanner.scan("");

        // Load dependency injectors
        loadSomeClasses(classPathFilter, classPathInterfaces, DependencyInjector.class);

        // Load the configuration
        var configurationLoaders = classPathFilter.filter(classPathInterfaces,
                cd -> cd.hasInterface(ConfigurationProvider.class));
        for (ClassPathInfoInterface cd : configurationLoaders) {
            @SuppressWarnings("unchecked")
            Class<? extends ConfigurationProvider> configurationLoaderClass = (Class<? extends ConfigurationProvider>) cd
                    .loadClass();
            ConfigurationProvider configurationProvider = init(configurationLoaderClass);

            if (configurationProvider != null) {
                configurationProvider.loadConfiguration().forEach((key, values) -> {
                    register(key, values);
                });
            }
        }

        // Load all PackageScanProvider
        var packageScanProviders = classPathFilter.filter(classPathInterfaces,
                cd -> cd.hasInterface(PackagesScanProvider.class));

        Set<String> packages = new HashSet<>();
        packages.addAll(new DefaultPackageScanProvider().getPackages());

        for (ClassPathInfoInterface cd : packageScanProviders) {
            @SuppressWarnings("unchecked")
            Class<? extends PackagesScanProvider> packageScanProviderClass = (Class<? extends PackagesScanProvider>) cd
                    .loadClass();
            PackagesScanProvider packageScanProvider = init(packageScanProviderClass);

            if (packageScanProvider != null) {
                packages.addAll(packageScanProvider.getPackages());
            }
        }

        // Load all the objects of the packages found
        for (String packagePrefix : packages) {
            var classPathInfo = classPathScanner.scan(packagePrefix);
            var classes = classPathFilter.filter(classPathInfo, cd -> !cd.isInterface() && !cd.isAbstract());
            for (ClassPathInfoInterface cd : classes) {
                Class<?> clazz = cd.loadClass();
                String name = clazz.getSimpleName();
                if (clazz.isAnnotationPresent(Singleton.class)) {
                    Singleton data = clazz.getAnnotation(Singleton.class);
                    if (!data.value().isEmpty()) {
                        name = data.value();
                    }
                }
                register(name, clazz);
            }
        }

        initialized = true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T init(Class<? extends T> clazz) throws Exception {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        if (clazz.getDeclaredConstructors().length == 0) {
            throw new IllegalArgumentException("Class must have a constructor");
        }
        if (clazz.getDeclaredConstructors().length > 1) {
            throw new IllegalArgumentException("Class must have only one constructor");
        }
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        Object[] parametersInstance = new Object[constructor.getParameterCount()];

        var serviceLocator = getServiceLocator();
        Collection<DependencyInjector> dependencyInjectors = (Collection<DependencyInjector>) serviceLocator
                .getServices(DependencyInjector.class);

        for (int i = 0; i < parametersInstance.length; i++) {
            Parameter parameter = constructor.getParameters()[i];
            final DependencyElement element = new DependencyElement.ParameterElement(parameter);

            if (dependencyInjectors.isEmpty()) {
                System.out.println("ServiceContainer contains " +
                        serviceLocator.getServices().size());
                // TODO: Add a default dependency injector
                throw new IllegalArgumentException("No dependency injector found");
            }

            Object result = dependencyInjectors.stream()
                    .map(di -> di.get(this, element))
                    .filter(o -> o != null)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No dependency injector found"));
            parametersInstance[i] = result;
        }

        return (T) constructor.newInstance(parametersInstance);
    }

    @Override
    public void register(final String name, final Object object) {
        var instancedObject = object;
        if (object != null && object instanceof Class<?>) {
            try {
                instancedObject = init((Class<?>) object);
            } catch (Exception e) {
            }
        }
        this.objects.computeIfAbsent(name, k -> new TreeSet<>(ObjectPriorityComparator.INSTANCE)).add(
                instancedObject);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name) {
        return (T) this.objects.get(name).first();
    }

    @Override
    public ServiceLocator getServiceLocator() {
        return new DefaultServiceLocator();
    }

    public class DefaultServiceLocator implements ServiceLocator {

        @Override
        public Collection<Object> getServices(String prefix) {
            return DefaultContainer.this.objects.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(prefix)).flatMap(entry -> entry.getValue().stream())
                    .toList();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Collection<? extends T> getServices(Class<? extends T> interfaceClass) {
            return DefaultContainer.this.objects.values().stream().flatMap(Collection::stream)
                    .filter(object -> interfaceClass.isAssignableFrom(object.getClass()))
                    .map(object -> (T) object).toList();
        }

        @Override
        public Collection<Object> getServices(Predicate<Object> predicate) {
            return DefaultContainer.this.objects.values().stream().flatMap(Collection::stream).filter(predicate)
                    .toList();
        }

        @Override
        public Collection<Object> getServices() {
            return DefaultContainer.this.objects.values().stream().flatMap(Collection::stream).toList();
        }

    }

    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Load some classes
     * 
     * @param classPathFilter     The class path filter
     * @param classPathInterfaces The class path interfaces
     * @param clazz               The class to load
     */
    private void loadSomeClasses(
            ClassPathFilter classPathFilter,
            Set<ClassPathInfoInterface> classPathInterfaces,
            Class<?> clazz) {

        try {
            long start = System.nanoTime();
            System.out.println("Loading " + clazz.getSimpleName());
            System.out.println("Starting with " + classPathInterfaces.size() + " classpath interfaces");
            var classes = classPathFilter.filter(classPathInterfaces,
                    cd -> cd.hasInterface(clazz));
            System.out.println("Found " + classes.size() + " classes");
            for (ClassPathInfoInterface cd : classes) {
                Class<?> loadedClass = cd.loadClass();
                Object loadedInstance = init(loadedClass);

                if (loadedInstance != null) {
                    register(loadedInstance.getClass().getSimpleName(), loadedInstance);
                }
            }

            long end = System.nanoTime();
            long time = end - start;
            int scanned = classPathInterfaces.size();
            int collected = classes.size();
            double timeInMs = time / 1_000_000.0;
            DecimalFormat df = new DecimalFormat("#.##");
            System.out.println("Scanned " + scanned + " classes and collected " + collected + " classes in "
                    + df.format(timeInMs) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
