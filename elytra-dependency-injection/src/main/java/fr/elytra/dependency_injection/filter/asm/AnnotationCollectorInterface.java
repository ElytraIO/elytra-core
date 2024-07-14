package fr.elytra.dependency_injection.filter.asm;

import fr.elytra.dependency_injection.filter.ClassSubsetData.AnnotationData;

/**
 * Interface for the annotation collector
 */
public interface AnnotationCollectorInterface {
    
    /**
     * Merge the data of the annotation
     * 
     * @param root The root of the annotation
     * @param data The data of the annotation
     */
    void mergeData(String root, AnnotationData data);
}
