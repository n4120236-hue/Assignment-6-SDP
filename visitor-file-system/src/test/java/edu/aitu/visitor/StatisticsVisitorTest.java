package edu.aitu.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StatisticsVisitorTest {

    private static StatisticsVisitor collect(Element root) {
        StatisticsVisitor visitor = new StatisticsVisitor();
        root.accept(visitor);
        return visitor;
    }

    @Test
    void countsFilesDirectoriesAndDepthForNestedTree() {
        StatisticsVisitor stats = collect(SampleTree.create().root());

        assertEquals(6, stats.fileCount());
        assertEquals(5, stats.directoryCount());
        assertEquals(3, stats.maxDepth());
    }

    @Test
    void rootDirectoryIsCountedAndHasDepthZero() {
        StatisticsVisitor stats = collect(new DirectoryNode("empty"));

        assertEquals(0, stats.fileCount());
        assertEquals(1, stats.directoryCount());
        assertEquals(0, stats.maxDepth());
    }

    @Test
    void subtreeStatisticsMeasureDepthFromChosenRoot() {
        StatisticsVisitor stats = collect(SampleTree.create().docs());

        assertEquals(4, stats.fileCount());
        assertEquals(3, stats.directoryCount());
        assertEquals(2, stats.maxDepth());
    }

    @Test
    void fileAsRootHasDepthZeroAndNoDirectories() {
        StatisticsVisitor stats = collect(new FileNode("solo", 1));

        assertEquals(1, stats.fileCount());
        assertEquals(0, stats.directoryCount());
        assertEquals(0, stats.maxDepth());
    }

    @Test
    void deepestEmptyDirectoryDeterminesMaxDepth() {
        DirectoryNode root = new DirectoryNode("r")
                .add(new FileNode("shallow.txt", 1))
                .add(new DirectoryNode("a").add(new DirectoryNode("b").add(new DirectoryNode("c"))));

        StatisticsVisitor stats = collect(root);

        assertEquals(1, stats.fileCount());
        assertEquals(4, stats.directoryCount());
        assertEquals(3, stats.maxDepth());
    }

    @Test
    void deepestFileDeterminesMaxDepth() {
        DirectoryNode root = new DirectoryNode("r")
                .add(new DirectoryNode("a").add(new FileNode("leaf.txt", 1)));

        assertEquals(2, collect(root).maxDepth());
    }

    @Test
    void siblingBranchesDoNotAddUpDepth() {
        DirectoryNode root = new DirectoryNode("r")
                .add(new DirectoryNode("a").add(new FileNode("x", 1)))
                .add(new DirectoryNode("b").add(new FileNode("y", 1)))
                .add(new DirectoryNode("c").add(new FileNode("z", 1)));

        StatisticsVisitor stats = collect(root);

        assertEquals(3, stats.fileCount());
        assertEquals(4, stats.directoryCount());
        assertEquals(2, stats.maxDepth());
    }
}
