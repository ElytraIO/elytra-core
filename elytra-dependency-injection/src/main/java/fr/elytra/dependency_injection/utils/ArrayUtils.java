package fr.elytra.dependency_injection.utils;

import java.util.function.Function;
import java.util.function.IntFunction;

public class ArrayUtils {

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = java.util.Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static <T, U> U[] map(T[] elements, Function<T, U> mapping, IntFunction<U[]> generator) {
        U[] result = generator.apply(elements.length);
        for (int i = 0; i < elements.length; i++) {
            result[i] = mapping.apply(elements[i]);
        }
        return result;
    }

}
