package fr.elytra.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import fr.elytra.dependency_injection.ContainerInterface;
import fr.elytra.dependency_injection.filter.ClassSubsetData;
import fr.elytra.dependency_injection.filter.ClassType;
import fr.elytra.dependency_injection.filter.ClassSubsetData.AnnotationData;
import fr.elytra.dependency_injection.impl.DefaultContainer;
import fr.elytra.processor.annotations.Plugin;
import fr.elytra.processor.plugins.PluginDefinitionData;
import fr.elytra.processor.plugins.PluginFileDefinitionGenerator;

@SupportedAnnotationTypes({
        "fr.elytra.processor.annotations.Plugin",
})
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class PluginProcessor extends AbstractProcessor {

    private Messager messenger;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messenger = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messenger.printMessage(javax.tools.Diagnostic.Kind.NOTE, "Processing annotations");
        Set<? extends Element> annotatedPlugin = roundEnv.getElementsAnnotatedWith(
                Plugin.class);
        if (annotatedPlugin.isEmpty()) {
            return false;
        }
        if (annotatedPlugin.size() > 1) {
            String elementsName = annotatedPlugin.stream()
                    .map(e -> e.getSimpleName().toString())
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("You must have only one plugin class, found " + elementsName);
        }

        try {
            ContainerInterface container = DefaultContainer.getInstance();
            PluginFileDefinitionGenerator pluginFileGenerator = container.getServiceLocator()
                    .getServices(PluginFileDefinitionGenerator.class)
                    .stream().findFirst().orElseThrow();

            for (Element element : annotatedPlugin) {
                if (element.getKind().isClass()) {
                    String packageName = element.getEnclosingElement().toString();
                    String className = element.getSimpleName().toString();
                    Plugin annotation = element.getAnnotation(Plugin.class);

                    // Get interfaces of the class
                    ClassSubsetData classSubsetData = loadSubsetData(element);

                    try {
                        FileObject file = processingEnv.getFiler()
                                .createResource(
                                        StandardLocation.SOURCE_OUTPUT, "resources", pluginFileGenerator.getFileName());
                        if (file.getLastModified() > 0) {
                            // File already exists, remove it
                            file.delete();
                        }
                        Writer writer = file.openWriter();
                        writer.write(pluginFileGenerator.generateFile(new PluginDefinitionData(
                                packageName + "." + className,
                                annotation.name(),
                                annotation.version(),
                                annotation.description(),
                                List.of(annotation.authors())), classSubsetData));
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    private ClassSubsetData loadSubsetData(Element element) {
        String className = element.asType().toString();
        String classPackage = element.getEnclosingElement().toString();
        String superClassName = null;
        List<String> interfaces = element.getEnclosedElements().stream()
                .filter(e -> e.getKind().isInterface())
                .map(e -> e.asType().toString())
                .collect(Collectors.toList());
        ClassType classType = ClassType.CLASS;
        int modifier = element.getModifiers().stream()
                .mapToInt(m -> (int) Math.pow(2, m.ordinal()))
                .reduce(0, (a, b) -> a + b);
        List<AnnotationData> classAnnotations = new ArrayList<>();
        element.getAnnotationMirrors().forEach(a -> {
            Map<String, Object> annotationsValues = a.getElementValues().entrySet().stream()
                    .collect(
                            Collectors.toMap(e -> e.getKey().getSimpleName().toString(), e -> e.getValue().getValue()));
            classAnnotations.add(new AnnotationData(a.getAnnotationType().toString(), annotationsValues));
        });

        return new ClassSubsetData(
                className, classPackage, null,
                superClassName, interfaces,
                classAnnotations, null,
                classType, modifier);
    }

}
