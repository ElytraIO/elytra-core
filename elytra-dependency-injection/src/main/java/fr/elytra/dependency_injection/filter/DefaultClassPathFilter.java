package fr.elytra.dependency_injection.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import fr.elytra.dependency_injection.filter.asm.ClassSubsetDataLoader;
import fr.elytra.dependency_injection.scanner.ClassPathInfoInterface;

public class DefaultClassPathFilter implements ClassPathFilter {

    @Override
    public List<ClassPathInfoInterface> filter(Collection<ClassPathInfoInterface> classPathes,
            List<Predicate<ClassSubsetData>> filters, boolean visitAnnotations) {
        List<ClassPathInfoInterface> filteredClassPath = new ArrayList<>();
        ClassSubsetDataLoader classSubsetDataLoader = new ClassSubsetDataLoader(Opcodes.ASM9, visitAnnotations);

        elementLoop: for (ClassPathInfoInterface classPathElement : classPathes) {
            try {
                ClassReader classReader = new ClassReader(classPathElement.readAllBytes());
                classReader.accept(classSubsetDataLoader,
                        ClassReader.SKIP_FRAMES & ClassReader.SKIP_DEBUG & ClassReader.SKIP_CODE);

                ClassSubsetData classPreloadedData = classSubsetDataLoader.getClassSubsetData();
                for (Predicate<ClassSubsetData> filter : filters) {
                    if (!filter.test(classPreloadedData)) {
                        continue elementLoop;
                    }
                }
                filteredClassPath.add(classPathElement);
            } catch (Exception e) {
                throw new RuntimeException("Scanning " + classPathElement.classPath() + " failed", e);
            }

        }
        return filteredClassPath;
    }

}
