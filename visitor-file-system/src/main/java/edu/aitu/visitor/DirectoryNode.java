package edu.aitu.visitor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public final class DirectoryNode extends Node {

    private final Map<String, Node> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public DirectoryNode add(Node child) {
        Objects.requireNonNull(child, "child");
        requireNoCycle(child);
        requireNoOwner(child);
        requireFreeName(child);
        children.put(child.name(), child);
        child.attachTo(this);
        return this;
    }

    public boolean remove(String childName) {
        Node removed = children.remove(childName);
        if (removed == null) {
            return false;
        }
        removed.detach();
        return true;
    }

    public List<Element> children() {
        return List.copyOf(children.values());
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    private void requireNoCycle(Node child) {
        for (DirectoryNode current = this; current != null; current = current.owner()) {
            if (current == child) {
                throw new IllegalArgumentException(
                        "Adding '" + child.name() + "' to '" + name() + "' would create a cycle");
            }
        }
    }

    private void requireNoOwner(Node child) {
        if (child.owner() != null) {
            throw new IllegalStateException(
                    "'" + child.name() + "' already belongs to '" + child.owner().name() + "'");
        }
    }

    private void requireFreeName(Node child) {
        if (children.containsKey(child.name())) {
            throw new IllegalArgumentException(
                    "Directory '" + name() + "' already contains '" + child.name() + "'");
        }
    }
}
