package edu.aitu.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TraversalTest {

    private static final class AuditVisitor extends TraversingVisitor {

        private final Map<Element, Integer> visits = new IdentityHashMap<>();
        private final List<String> order = new ArrayList<>();
        private final List<String> exits = new ArrayList<>();

        @Override
        protected void startRun() {
            visits.clear();
            order.clear();
            exits.clear();
        }

        @Override
        protected void onFile(FileNode file, int depth) {
            visits.merge(file, 1, Integer::sum);
            order.add(file.name());
        }

        @Override
        protected void onDirectoryEnter(DirectoryNode directory, int depth) {
            visits.merge(directory, 1, Integer::sum);
            order.add(directory.name());
        }

        @Override
        protected void onDirectoryExit(DirectoryNode directory, int depth) {
            exits.add(directory.name());
        }
    }

    @Test
    void everyReachableNodeIsVisitedExactlyOnce() {
        SampleTree tree = SampleTree.create();
        AuditVisitor visitor = new AuditVisitor();

        tree.root().accept(visitor);

        assertEquals(11, visitor.visits.size());
        assertTrue(visitor.visits.values().stream().allMatch(count -> count == 1));
    }

    @Test
    void traversalIsDepthFirstInNameOrder() {
        SampleTree tree = SampleTree.create();
        AuditVisitor visitor = new AuditVisitor();

        tree.root().accept(visitor);

        assertEquals(
                List.of("root", "a.txt", "docs", "deep", "a.txt", "data.log", "void",
                        "guide.txt", "spec.txt", "empty", "notes.log"),
                visitor.order);
    }

    @Test
    void everyDirectoryIsEnteredAndExitedOnce() {
        SampleTree tree = SampleTree.create();
        AuditVisitor visitor = new AuditVisitor();

        tree.root().accept(visitor);

        assertEquals(List.of("void", "deep", "docs", "empty", "root"), visitor.exits);
    }

    @Test
    void emptyDirectoryIsVisitedAsSingleNode() {
        AuditVisitor visitor = new AuditVisitor();

        new DirectoryNode("lonely").accept(visitor);

        assertEquals(List.of("lonely"), visitor.order);
        assertEquals(List.of("lonely"), visitor.exits);
    }

    @Test
    void subtreeTraversalStaysInsideChosenRoot() {
        SampleTree tree = SampleTree.create();
        AuditVisitor visitor = new AuditVisitor();

        tree.deep().accept(visitor);

        assertEquals(List.of("deep", "a.txt", "data.log", "void"), visitor.order);
    }
}
