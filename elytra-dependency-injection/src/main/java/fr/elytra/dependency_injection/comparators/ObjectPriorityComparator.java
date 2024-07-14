package fr.elytra.dependency_injection.comparators;

import java.util.Comparator;

import fr.elytra.dependency_injection.annotations.Priority;

public class ObjectPriorityComparator implements Comparator<Object> {

    public static final ObjectPriorityComparator INSTANCE = new ObjectPriorityComparator();

    @Override
    public int compare(Object o1, Object o2) {
        return PriorityComparator.INSTANCE.compare(loadPriority(o1), loadPriority(o2));
    }

    Priority loadPriority(Object object) {
        if (object == null) {
            return null;
        }
        return object.getClass().getAnnotation(Priority.class);
    }

}
