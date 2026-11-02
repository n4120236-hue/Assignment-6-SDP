package edu.aitu.visitor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class SearchVisitor extends TraversingVisitor {

    private final Predicate<String> nameCriterion;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private final Deque<String> directoryPath = new ArrayDeque<>();

    public SearchVisitor(Predicate<String> nameCriterion, long minimumSize) {
        this.nameCriterion = Objects.requireNonNull(nameCriterion, "nameCriterion");
        if (minimumSize < 0) {
            throw new IllegalArgumentException("Minimum size must not be negative: " + minimumSize);
        }
        this.minimumSize = minimumSize;
    }

    public List<String> matches() {
        return matches.stream().sorted().toList();
    }

    @Override
    protected void startRun() {
        matches.clear();
        directoryPath.clear();
    }

    @Override
    protected void onDirectoryEnter(DirectoryNode directory, int depth) {
        if (depth > 0) {
            directoryPath.addLast(directory.name());
        }
    }

    @Override
    protected void onDirectoryExit(DirectoryNode directory, int depth) {
        if (depth > 0) {
            directoryPath.removeLast();
        }
    }

    @Override
    protected void onFile(FileNode file, int depth) {
        if (isMatch(file)) {
            matches.add(relativePath(file));
        }
    }

    private boolean isMatch(FileNode file) {
        return nameCriterion.test(file.name()) && file.size() >= minimumSize;
    }

    private String relativePath(FileNode file) {
        List<String> parts = new ArrayList<>(directoryPath);
        parts.add(file.name());
        return String.join("/", parts);
    }
}
