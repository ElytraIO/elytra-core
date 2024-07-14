package fr.elytra.dependency_injection.filter;

import org.objectweb.asm.Opcodes;

/**
 * Class type.
 */
public enum ClassType {

    CLASS,
    SEALED_CLASS,
    INTERFACE,
    ENUM,
    RECORD,
    ANNOTATION;

    /**
     * Get the class type from a class.
     * 
     * @param clazz The class to get the type from.
     * @return The class type.
     */
    public static ClassType fromClass(Class<?> clazz) {
        if (clazz.isInterface()) {
            return INTERFACE;
        } else if (clazz.isEnum()) {
            return ENUM;
        } else if (clazz.isAnnotation()) {
            return ANNOTATION;
        } else if (clazz.isRecord()) {
            return RECORD;
        } else if (clazz.isSealed()) {
            return SEALED_CLASS;
        } else {
            return CLASS;
        }
    }

    public static ClassType fromAccess(int access) {
        if ((access & Opcodes.ACC_ENUM) != 0) {
            return ENUM;
        } else if ((access & Opcodes.ACC_INTERFACE) != 0) {
            return INTERFACE;
        } else if ((access & Opcodes.ACC_ANNOTATION) != 0) {
            return ANNOTATION;
        } else if ((access & Opcodes.ACC_RECORD) != 0) {
            return RECORD;
        } else if ((access & Opcodes.ACC_FINAL) != 0) {
            return SEALED_CLASS;
        } else {
            return CLASS;
        }
    }

    /**
     * Check if the class type is a class.
     * 
     * @return True if the class type is a class.
     */
    public boolean isClass() {
        return switch (this) {
            case CLASS, SEALED_CLASS -> true;
            default -> false;
        };
    }

}
