package edu.aitu.visitor;

public final class StatisticsVisitor extends TraversingVisitor {

    private int fileCount;
    private int directoryCount;
    private int maxDepth;

    public int fileCount() {
        return fileCount;
    }

    public int directoryCount() {
        return directoryCount;
    }

    public int maxDepth() {
        return maxDepth;
    }

    @Override
    protected void startRun() {
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
    }

    @Override
    protected void onFile(FileNode file, int depth) {
        fileCount++;
        trackDepth(depth);
    }

    @Override
    protected void onDirectoryEnter(DirectoryNode directory, int depth) {
        directoryCount++;
        trackDepth(depth);
    }

    private void trackDepth(int depth) {
        maxDepth = Math.max(maxDepth, depth);
    }
}
