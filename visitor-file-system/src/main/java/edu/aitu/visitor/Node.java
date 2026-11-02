package edu.aitu.visitor;

import java.util.Optional;

public abstract class Node implements Element {

    private final String name;
    private DirectoryNode owner;

    Node(String name) {
        this.name = requireValidName(name);
    }

    private static String requireValidName(String name) {
        if (name == null || name.isBlank() || name.contains("/")) {
            throw new IllegalArgumentException("Invalid node name: " + name);
        }
        return name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Optional<DirectoryNode> parent() {
        return Optional.ofNullable(owner);
    }

    DirectoryNode owner() {
        return owner;
    }

    void attachTo(DirectoryNode newOwner) {
        owner = newOwner;
    }

    void detach() {
        owner = null;
    }
}
