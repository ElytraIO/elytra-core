package fr.elytra.dependency_injection.filter;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import fr.elytra.dependency_injection.scanner.ClassPathInfoInterface;

/**
 * Interface for filtering the class path.
 */
public interface ClassPathFilter {

    /**
     * Filter the class path.
     * 
     * @param classPathes The class path to filter.
     * @param filter    The filter to apply.
     * @return The filtered class path.
     */
    default List<ClassPathInfoInterface> filter(Collection<ClassPathInfoInterface> classPathes, Predicate<ClassSubsetData> filter) {
        return filter(classPathes, List.of(filter));
    }

    /**
     * Filter the class path.
     * 
     * @param classPathes The class path to filter.
     * @param filters   The filters to apply.
     * @return The filtered class path.
     */
    default List<ClassPathInfoInterface> filter(Collection<ClassPathInfoInterface> classPathes, List<Predicate<ClassSubsetData>> filters){
        return filter(classPathes, filters, false);
    }

    /**
     * Filter the class path.
     * 
     * @param classPathes The class path to filter.
     * @param filters   The filters to apply.
     * @param visitAnnotations If annotations should be visited.
     * @return The filtered class path.
     */
    List<ClassPathInfoInterface> filter(Collection<ClassPathInfoInterface> classPathes, List<Predicate<ClassSubsetData>> filters, boolean visitAnnotations);


}
