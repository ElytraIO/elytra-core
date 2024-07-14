package fr.elytra.dependency_injection.filter.asm;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;

import fr.elytra.dependency_injection.filter.ClassSubsetData.AnnotationData;

public class AnnotationSubsetDataLoader extends AnnotationVisitor implements AnnotationCollectorInterface{

    Map<String, Object> values;
    String annotationName;
    String annotationDescriptor;
    AnnotationCollectorInterface parent;

    public AnnotationSubsetDataLoader(String annotationName, int api) {
        this(annotationName, api, null);
    }

    public AnnotationSubsetDataLoader(String annotationName, int api, AnnotationCollectorInterface parent) {
        super(api);
        this.values = new HashMap<>();
        this.annotationName = annotationName;
        this.parent = parent;
    }

    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        return new AnnotationSubsetDataLoader(name, api, this);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        if(parent != null){
            parent.mergeData(annotationName, getAnnotationData());
        }
    }

    public AnnotationData getAnnotationData() {
        return new AnnotationData(annotationName, values);
    }

    @Override
    public void mergeData(String root, AnnotationData data) {
        values.put(root, data);
    }




}
