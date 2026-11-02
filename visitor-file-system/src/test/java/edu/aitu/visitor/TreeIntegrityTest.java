package edu.aitu.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TreeIntegrityTest {

    @Test
    void rejectsNegativeFileSize() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -1));
    }

    @Test
    void acceptsZeroFileSize() {
        assertEquals(0, new FileNode("empty.txt", 0).size());
    }

    @Test
    void rejectsInvalidNames() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new FileNode("  ", 1));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(""));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode("a/b"));
    }

    @Test
    void rejectsDuplicateSiblingNamesAndKeepsOriginalChild() {
        FileNode original = new FileNode("x.txt", 1);
        DirectoryNode dir = new DirectoryNode("dir").add(original);

        assertThrows(IllegalArgumentException.class, () -> dir.add(new FileNode("x.txt", 2)));
        assertThrows(IllegalArgumentException.class, () -> dir.add(new DirectoryNode("x.txt")));

        assertEquals(1, dir.children().size());
        assertSame(original, dir.children().get(0));
    }

    @Test
    void allowsEqualNamesInDifferentDirectories() {
        DirectoryNode left = new DirectoryNode("left").add(new FileNode("same", 1));
        DirectoryNode right = new DirectoryNode("right").add(new FileNode("same", 1));

        assertEquals(1, left.children().size());
        assertEquals(1, right.children().size());
    }

    @Test
    void rejectsSecondParentAndKeepsFirstOwnership() {
        FileNode file = new FileNode("shared.txt", 1);
        DirectoryNode first = new DirectoryNode("first").add(file);
        DirectoryNode second = new DirectoryNode("second");

        assertThrows(IllegalStateException.class, () -> second.add(file));

        assertTrue(second.isEmpty());
        assertSame(first, file.parent().orElseThrow());
    }

    @Test
    void rejectsDirectoryAddedToItself() {
        DirectoryNode dir = new DirectoryNode("dir");

        assertThrows(IllegalArgumentException.class, () -> dir.add(dir));

        assertTrue(dir.isEmpty());
    }

    @Test
    void rejectsCycleThroughDescendants() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode middle = new DirectoryNode("middle");
        DirectoryNode leaf = new DirectoryNode("leaf");
        root.add(middle);
        middle.add(leaf);

        assertThrows(IllegalArgumentException.class, () -> leaf.add(root));

        assertTrue(leaf.isEmpty());
        assertTrue(root.parent().isEmpty());
    }

    @Test
    void rejectsCycleThroughAttachedAncestor() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode middle = new DirectoryNode("middle");
        DirectoryNode leaf = new DirectoryNode("leaf");
        root.add(middle);
        middle.add(leaf);

        assertThrows(IllegalArgumentException.class, () -> leaf.add(middle));

        assertTrue(leaf.isEmpty());
    }

    @Test
    void rejectsNullChild() {
        DirectoryNode dir = new DirectoryNode("dir");

        assertThrows(NullPointerException.class, () -> dir.add(null));
    }

    @Test
    void supportsDeepValidNesting() {
        DirectoryNode root = new DirectoryNode("level0");
        DirectoryNode current = root;
        for (int level = 1; level <= 50; level++) {
            DirectoryNode next = new DirectoryNode("level" + level);
            current.add(next);
            current = next;
        }
        current.add(new FileNode("bottom.txt", 3));

        TotalSizeVisitor size = new TotalSizeVisitor();
        root.accept(size);

        assertEquals(3, size.totalSize());
    }

    @Test
    void removedNodeCanBeMovedToAnotherDirectory() {
        FileNode file = new FileNode("move.txt", 1);
        DirectoryNode source = new DirectoryNode("source").add(file);
        DirectoryNode target = new DirectoryNode("target");

        assertTrue(source.remove("move.txt"));
        assertTrue(file.parent().isEmpty());
        target.add(file);

        assertTrue(source.isEmpty());
        assertSame(target, file.parent().orElseThrow());
    }

    @Test
    void removingUnknownNameReportsFalse() {
        assertFalse(new DirectoryNode("dir").remove("ghost"));
    }

    @Test
    void childrenViewIsImmutableSnapshot() {
        DirectoryNode dir = new DirectoryNode("dir").add(new FileNode("a", 1));
        List<Element> snapshot = dir.children();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new FileNode("b", 1)));

        dir.add(new FileNode("b", 1));
        assertEquals(1, snapshot.size());
        assertEquals(2, dir.children().size());
    }

    @Test
    void childrenAreOrderedByName() {
        DirectoryNode dir = new DirectoryNode("dir")
                .add(new FileNode("c", 1))
                .add(new FileNode("a", 1))
                .add(new FileNode("b", 1));

        List<String> names = dir.children().stream().map(Element::name).toList();

        assertEquals(List.of("a", "b", "c"), names);
    }
}
