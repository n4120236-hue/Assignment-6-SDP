package edu.aitu.visitor;

public final class TotalSizeVisitor extends TraversingVisitor {

    private long totalSize;

    public long totalSize() {
        return totalSize;
    }

    @Override
    protected void startRun() {
        totalSize = 0;
    }

    @Override
    protected void onFile(FileNode file, int depth) {
        totalSize = Math.addExact(totalSize, file.size());
    }
}
