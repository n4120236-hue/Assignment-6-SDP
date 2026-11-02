package edu.aitu.visitor;

public final class FileNode extends Node {

    private final long size;

    public FileNode(String name, long size) {
        super(name);
        if (size < 0) {
            throw new IllegalArgumentException("File size must not be negative: " + size);
        }
        this.size = size;
    }

    public long size() {
        return size;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
