package edu.aitu.visitor;

import java.util.Objects;
import java.util.function.Predicate;

public final class NameCriteria {

    private NameCriteria() {
    }

    public static Predicate<String> any() {
        return name -> true;
    }

    public static Predicate<String> exact(String expected) {
        Objects.requireNonNull(expected, "expected");
        return name -> name.equals(expected);
    }

    public static Predicate<String> contains(String fragment) {
        Objects.requireNonNull(fragment, "fragment");
        return name -> name.contains(fragment);
    }

    public static Predicate<String> endsWith(String suffix) {
        Objects.requireNonNull(suffix, "suffix");
        return name -> name.endsWith(suffix);
    }
}
