package fr.elytra.dependency_injection.filter.asm;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

import fr.elytra.dependency_injection.filter.ClassSubsetData;
import fr.elytra.dependency_injection.filter.ClassSubsetData.AnnotationData;
import fr.elytra.dependency_injection.filter.ClassType;
import fr.elytra.dependency_injection.utils.ArrayUtils;

public class ClassSubsetDataLoader extends ClassVisitor implements AnnotationCollectorInterface {

    private String className;
    private String classPackage;
    private String classPath;
    private String superClassPath;
    private List<String> interfacesPath = List.of();
    private ClassType classType;
    private int classModifiers;
    private List<ClassSubsetData.AnnotationData> classAnnotations;
    private Map<String, List<ClassSubsetData.AnnotationData>> fieldAnnotations;
    private boolean visitAnnotations = false;

    public ClassSubsetDataLoader(int api, boolean visitAnnotations) {
        super(api);
        this.visitAnnotations = visitAnnotations;
    }

    public ClassSubsetData getClassSubsetData() {
        return new ClassSubsetData(
                className, classPackage, classPath, superClassPath,
                interfacesPath,
                classAnnotations, fieldAnnotations,
                classType,
                classModifiers);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        // return new AnnotationSubsetDataLoader(descriptor, api, this);
        return visitAnnotations ? new AnnotationSubsetDataLoader(descriptor, api, this) : null;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        this.className = name;
        this.classType = ClassType.fromAccess(access);
        this.classModifiers = access;

        if (superName != null)
            this.superClassPath = superName;
        if (interfaces != null) {
            this.interfacesPath = List.of(ArrayUtils.map(interfaces, e -> e.replace("/", "."), String[]::new));
        }
    }

    @Override
    public void mergeData(String root, AnnotationData data) {
        if (root == null) {
            classAnnotations.add(data);
        } else {
            fieldAnnotations.putIfAbsent(root, List.of());
            fieldAnnotations.get(root).add(data);
        }
    }

}
