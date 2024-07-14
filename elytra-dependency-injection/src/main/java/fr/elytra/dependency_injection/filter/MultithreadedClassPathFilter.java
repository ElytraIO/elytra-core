package fr.elytra.dependency_injection.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Predicate;

import fr.elytra.dependency_injection.scanner.ClassPathInfoInterface;

public class MultithreadedClassPathFilter
        implements ClassPathFilter {

    private int parallelism;
    private int maxDepth;

    public MultithreadedClassPathFilter() {
        this(4, 2);
    }

    public MultithreadedClassPathFilter(int parallelism, int maxDepth) {
        this.parallelism = parallelism;
        this.maxDepth = maxDepth;
    }

    @Override
    public List<ClassPathInfoInterface> filter(Collection<ClassPathInfoInterface> classPath,
            List<Predicate<ClassSubsetData>> filters, boolean visitAnnotations) {
        try (var pool = new ForkJoinPool(parallelism)) {
            return pool.invoke(new MultithreadedClassPathRecursiveAction(classPath, filters, visitAnnotations));
        }
    }

    public class MultithreadedClassPathRecursiveAction
            extends RecursiveTask<List<ClassPathInfoInterface>> {

        private static final long serialVersionUID = 1L;

        private final int depth;
        private final Collection<ClassPathInfoInterface> classPath;
        private final List<Predicate<ClassSubsetData>> filters;
        private final boolean visitAnnotations;

        public MultithreadedClassPathRecursiveAction(Collection<ClassPathInfoInterface> classPath,
                List<Predicate<ClassSubsetData>> filters,
                boolean visitAnnotations) {
            this(classPath, filters, visitAnnotations, 0);
        }

        public MultithreadedClassPathRecursiveAction(Collection<ClassPathInfoInterface> classPath,
                List<Predicate<ClassSubsetData>> filters,
                boolean visitAnnotations,
                int depth) {
            super();
            this.classPath = classPath;
            this.filters = filters;
            this.visitAnnotations = visitAnnotations;
            this.depth = depth;
        }

        @Override
        protected List<ClassPathInfoInterface> compute() {
            if (depth > maxDepth) {
                return process();
            } else {
                return ForkJoinTask.invokeAll(createSubtasks())
                        .stream()
                        .map(ForkJoinTask::join)
                        .reduce(new ArrayList<>(), (a, b) -> {
                            a.addAll(b);
                            return a;
                        });
            }
        }

        public Collection<MultithreadedClassPathRecursiveAction> createSubtasks() {
            List<MultithreadedClassPathRecursiveAction> dividedTasks = new ArrayList<>();
            int mid = classPath.size() / 2;
            dividedTasks.add(new MultithreadedClassPathRecursiveAction(new ArrayList<>(classPath).subList(0, mid),
                    filters, visitAnnotations, depth + 1));
            dividedTasks.add(new MultithreadedClassPathRecursiveAction(
                    new ArrayList<>(classPath).subList(mid, classPath.size()), filters, visitAnnotations, depth + 1));
            return dividedTasks;
        }

        public List<ClassPathInfoInterface> process() {
            return new DefaultClassPathFilter().filter(classPath, filters, visitAnnotations);
        }

    }

}
