package fr.elytra.dependency_injection.comparators;

import java.util.Comparator;

import fr.elytra.dependency_injection.annotations.Priority;

/**
 * Priority comparator
 * The higher the value, the higher the priority
 * If priority is not defined, it will be set to 100
 */
public class PriorityComparator implements Comparator<Priority> {

    public static final PriorityComparator INSTANCE = new PriorityComparator();


    @Override
    public int compare(Priority o1, Priority o2) {
        int value1 = (o1 == null ? 100 : o1.value());
        int value2 = (o2 == null ? 100 : o2.value());
        return Integer.compare(value1, value2);
    }

}
