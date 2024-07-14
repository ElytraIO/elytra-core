package fr.elytra.dependency_injection.scanner;

public interface ClassPathInfoInterface {
    
    public String classPath();

    public String className();

    public ClassLoader loader();

    public Class<?> loadClass() throws ClassNotFoundException;

    public byte[] readAllBytes() throws Exception;

}
