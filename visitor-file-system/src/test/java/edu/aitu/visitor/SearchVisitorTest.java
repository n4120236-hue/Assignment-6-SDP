package edu.aitu.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SearchVisitorTest {

    private static List<String> search(Element root, SearchVisitor visitor) {
        root.accept(visitor);
        return visitor.matches();
    }

    @Test
    void combinesNameAndSizeConstraints() {
        SampleTree tree = SampleTree.create();

        List<String> result = search(tree.root(), new SearchVisitor(NameCriteria.endsWith(".txt"), 50));

        assertEquals(List.of("docs/deep/a.txt", "docs/spec.txt"), result);
    }

    @Test
    void nameCriterionAloneSelectsEqualNamesFromDifferentDirectories() {
        SampleTree tree = SampleTree.create();

        List<String> result = search(tree.root(), new SearchVisitor(NameCriteria.exact("a.txt"), 0));

        assertEquals(List.of("a.txt", "docs/deep/a.txt"), result);
    }

    @Test
    void sizeThresholdAloneFiltersSmallFiles() {
        SampleTree tree = SampleTree.create();

        List<String> result = search(tree.root(), new SearchVisitor(NameCriteria.any(), 400));

        assertEquals(List.of("docs/deep/data.log", "docs/spec.txt"), result);
    }

    @Test
    void bothConstraintsMustHoldAtOnce() {
        SampleTree tree = SampleTree.create();

        List<String> result = search(tree.root(), new SearchVisitor(NameCriteria.exact("a.txt"), 50));

        assertEquals(List.of("docs/deep/a.txt"), result);
    }

    @Test
    void thresholdIsInclusive() {
        SampleTree tree = SampleTree.create();

        List<String> atBoundary = search(tree.root(), new SearchVisitor(NameCriteria.endsWith(".txt"), 70));
        List<String> aboveBoundary = search(tree.root(), new SearchVisitor(NameCriteria.endsWith(".txt"), 71));

        assertEquals(List.of("docs/deep/a.txt", "docs/spec.txt"), atBoundary);
        assertEquals(List.of("docs/spec.txt"), aboveBoundary);
    }

    @Test
    void pathsAreRelativeToChosenSubtreeRoot() {
        SampleTree tree = SampleTree.create();

        List<String> result = search(tree.docs(), new SearchVisitor(NameCriteria.any(), 0));

        assertEquals(List.of("deep/a.txt", "deep/data.log", "guide.txt", "spec.txt"), result);
    }

    @Test
    void rootNameIsNotPartOfPaths() {
        SampleTree tree = SampleTree.create();

        List<String> result = search(tree.root(), new SearchVisitor(NameCriteria.any(), 0));

        assertEquals(
                List.of("a.txt", "docs/deep/a.txt", "docs/deep/data.log", "docs/guide.txt", "docs/spec.txt", "notes.log"),
                result);
        assertTrue(result.stream().noneMatch(path -> path.startsWith("root")));
    }

    @Test
    void resultsAreSortedLexicographicallyNotInTraversalOrder() {
        DirectoryNode root = new DirectoryNode("root")
                .add(new DirectoryNode("a").add(new FileNode("b.txt", 1)))
                .add(new FileNode("a-b.txt", 1));

        List<String> result = search(root, new SearchVisitor(NameCriteria.any(), 0));

        assertEquals(List.of("a-b.txt", "a/b.txt"), result);
    }

    @Test
    void insertionOrderDoesNotChangeResult() {
        DirectoryNode first = new DirectoryNode("r")
                .add(new FileNode("z.txt", 1))
                .add(new FileNode("m.txt", 1))
                .add(new FileNode("b.txt", 1));
        DirectoryNode second = new DirectoryNode("r")
                .add(new FileNode("b.txt", 1))
                .add(new FileNode("z.txt", 1))
                .add(new FileNode("m.txt", 1));

        assertEquals(
                search(first, new SearchVisitor(NameCriteria.any(), 0)),
                search(second, new SearchVisitor(NameCriteria.any(), 0)));
    }

    @Test
    void containsCriterionMatchesFragmentOfName() {
        SampleTree tree = SampleTree.create();

        List<String> result = search(tree.root(), new SearchVisitor(NameCriteria.contains("data"), 0));

        assertEquals(List.of("docs/deep/data.log"), result);
    }

    @Test
    void noMatchReturnsEmptyList() {
        SampleTree tree = SampleTree.create();

        List<String> result = search(tree.root(), new SearchVisitor(NameCriteria.exact("missing.txt"), 0));

        assertTrue(result.isEmpty());
    }

    @Test
    void thresholdAboveEveryFileReturnsEmptyList() {
        SampleTree tree = SampleTree.create();

        List<String> result = search(tree.root(), new SearchVisitor(NameCriteria.any(), 10_000));

        assertTrue(result.isEmpty());
    }

    @Test
    void emptyDirectoryReturnsEmptyList() {
        List<String> result = search(new DirectoryNode("empty"), new SearchVisitor(NameCriteria.any(), 0));

        assertTrue(result.isEmpty());
    }

    @Test
    void fileAsRootIsReportedByItsName() {
        List<String> result = search(new FileNode("solo.txt", 9), new SearchVisitor(NameCriteria.any(), 9));

        assertEquals(List.of("solo.txt"), result);
    }

    @Test
    void returnedListCannotBeModified() {
        SampleTree tree = SampleTree.create();
        SearchVisitor visitor = new SearchVisitor(NameCriteria.any(), 0);
        tree.root().accept(visitor);

        assertThrows(UnsupportedOperationException.class, () -> visitor.matches().add("x"));
    }

    @Test
    void rejectsInvalidConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(NameCriteria.any(), -1));
        assertThrows(NullPointerException.class, () -> new SearchVisitor(null, 0));
    }
}
