package edu.aitu.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DoubleDispatchTest {

    private static final class RecordingVisitor implements Visitor {

        private final List<String> calls = new ArrayList<>();

        @Override
        public void visit(FileNode file) {
            calls.add("file:" + file.name());
        }

        @Override
        public void visit(DirectoryNode directory) {
            calls.add("directory:" + directory.name());
        }
    }

    @Test
    void fileAcceptReachesFileOverload() {
        RecordingVisitor visitor = new RecordingVisitor();

        new FileNode("report.txt", 5).accept(visitor);

        assertEquals(List.of("file:report.txt"), visitor.calls);
    }

    @Test
    void directoryAcceptReachesDirectoryOverload() {
        RecordingVisitor visitor = new RecordingVisitor();

        new DirectoryNode("docs").accept(visitor);

        assertEquals(List.of("directory:docs"), visitor.calls);
    }

    @Test
    void dispatchFollowsRuntimeTypeBehindElementReference() {
        RecordingVisitor visitor = new RecordingVisitor();
        Element asFile = new FileNode("a.txt", 1);
        Element asDirectory = new DirectoryNode("d");

        asFile.accept(visitor);
        asDirectory.accept(visitor);

        assertEquals(List.of("file:a.txt", "directory:d"), visitor.calls);
    }

    @Test
    void acceptDoesNotTraverseChildren() {
        RecordingVisitor visitor = new RecordingVisitor();
        DirectoryNode docs = new DirectoryNode("docs").add(new FileNode("x.txt", 1));

        docs.accept(visitor);

        assertEquals(List.of("directory:docs"), visitor.calls);
    }
}
