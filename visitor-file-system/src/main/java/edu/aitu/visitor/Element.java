package edu.aitu.visitor;

import java.util.Optional;

public interface Element {

    String name();

    Optional<DirectoryNode> parent();

    void accept(Visitor visitor);
}
