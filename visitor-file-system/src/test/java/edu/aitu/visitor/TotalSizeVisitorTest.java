package edu.aitu.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TotalSizeVisitorTest {

    @Test
    void sumsFilesAcrossAllNestingLevels() {
        SampleTree tree = SampleTree.create();
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        tree.root().accept(visitor);

        assertEquals(1260, visitor.totalSize());
    }

    @Test
    void subtreeSumIgnoresFilesOutsideChosenRoot() {
        SampleTree tree = SampleTree.create();
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        tree.docs().accept(visitor);

        assertEquals(1000, visitor.totalSize());
    }

    @Test
    void eachFileCountsExactlyOnceEvenWithEqualNames() {
        DirectoryNode root = new DirectoryNode("root")
                .add(new FileNode("same.txt", 7))
                .add(new DirectoryNode("inner").add(new FileNode("same.txt", 7)));
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        root.accept(visitor);

        assertEquals(14, visitor.totalSize());
    }

    @Test
    void emptyDirectoryHasZeroSize() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        new DirectoryNode("empty").accept(visitor);

        assertEquals(0, visitor.totalSize());
    }

    @Test
    void emptyNestedDirectoriesContributeNothing() {
        SampleTree tree = SampleTree.create();
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        tree.emptyDir().accept(visitor);
        assertEquals(0, visitor.totalSize());

        tree.voidDir().accept(visitor);
        assertEquals(0, visitor.totalSize());
    }

    @Test
    void singleFileAsRootReturnsItsOwnSize() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        new FileNode("solo.bin", 42).accept(visitor);

        assertEquals(42, visitor.totalSize());
    }

    @Test
    void zeroByteFilesAreAllowed() {
        DirectoryNode root = new DirectoryNode("root").add(new FileNode("zero", 0));
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        root.accept(visitor);

        assertEquals(0, visitor.totalSize());
    }
}
