package edu.aitu.visitor;

public abstract class TraversingVisitor implements Visitor {

    private int activeFrames;

    @Override
    public final void visit(FileNode file) {
        enterFrame();
        try {
            onFile(file, currentDepth());
        } finally {
            exitFrame();
        }
    }

    @Override
    public final void visit(DirectoryNode directory) {
        enterFrame();
        try {
            onDirectoryEnter(directory, currentDepth());
            for (Element child : directory.children()) {
                child.accept(this);
            }
            onDirectoryExit(directory, currentDepth());
        } finally {
            exitFrame();
        }
    }

    protected void startRun() {
    }

    protected void onFile(FileNode file, int depth) {
    }

    protected void onDirectoryEnter(DirectoryNode directory, int depth) {
    }

    protected void onDirectoryExit(DirectoryNode directory, int depth) {
    }

    private void enterFrame() {
        if (activeFrames == 0) {
            startRun();
        }
        activeFrames++;
    }

    private void exitFrame() {
        activeFrames--;
    }

    private int currentDepth() {
        return activeFrames - 1;
    }
}
