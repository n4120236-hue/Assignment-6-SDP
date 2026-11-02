# Visitor Pattern - Advanced File System

Assignment 06, ShP-2216 Software Design Patterns, Astana IT University.
Author: Yershadi Nurassyl, group SE-2523.

An in-memory hierarchical file system with three independent operations implemented with the Visitor pattern.
Java 17, Maven, JUnit 5.

## Commands

Run the tests from the project root:

    mvn test

Run the deterministic demonstration:

    mvn -q compile exec:java

## Structure

| Type | Role |
|------|------|
| `Element` | Common element interface: `name()`, `parent()`, `accept(Visitor)` |
| `Visitor` | Declares `visit(FileNode)` and `visit(DirectoryNode)` |
| `Node` | Shared name and ownership bookkeeping, package-private mutation |
| `FileNode`, `DirectoryNode` | The two concrete elements |
| `TraversingVisitor` | Owns the traversal, exposes hooks to subclasses |
| `TotalSizeVisitor` | Sum of file sizes in a subtree |
| `SearchVisitor`, `NameCriteria` | Name criterion combined with inclusive minimum size |
| `StatisticsVisitor` | File count, directory count, maximum depth |

## Double dispatch

1. `element.accept(visitor)` is resolved by the runtime type of the element.
2. `accept` calls `visitor.visit(this)`, and the overload is chosen by the static type of `this`
   (`FileNode` or `DirectoryNode`).

No `instanceof`, `getClass()`, reflection or casts are used anywhere.

## Traversal decision

The visitors own the traversal, through the abstract base class `TraversingVisitor`.
`accept` stays a one-line delegation, so elements never change when a visitor is added.
Traversal is depth first, pre-order, and children are kept sorted by name, so every reachable node is visited exactly
once per run and the order is deterministic. Empty and nested directories are visited like any other node.

## How paths and depth are determined

* Depth is the number of active `visit` frames minus one, so the chosen root has depth 0.
* `SearchVisitor` keeps the names of the directories entered below the root. The root name is not part of any path.
  Paths use `/`, and the final list is sorted lexicographically (`String` natural order).
* When the chosen root is a file, its path is its own name.

## State and reuse

Visitor instances are reusable. `startRun()` is called when a traversal begins at a root, before the first node, and
clears all accumulated state. Results describe the latest run only and never depend on the order of earlier runs.

## Tree integrity

| Rule | Mechanism |
|------|-----------|
| Negative file size | `FileNode` constructor throws `IllegalArgumentException` |
| Invalid name (null, blank, contains `/`) | `Node` constructor throws `IllegalArgumentException` |
| Duplicate sibling name | `DirectoryNode.add` throws `IllegalArgumentException` |
| Second parent | `DirectoryNode.add` throws `IllegalStateException` |
| Cycle (self or ancestor) | `DirectoryNode.add` throws `IllegalArgumentException` |

Rejected changes leave the tree unchanged. `children()` returns an immutable snapshot. Ownership changes are
package-private, so the public API cannot break the invariants. `remove(name)` detaches a child so it can be moved.

## Extension boundary

Closed for modification after the initial contracts: `Element`, `Visitor`, `Node`, `FileNode`, `DirectoryNode`,
`TraversingVisitor`. Open for extension: new operations are new subclasses of `TraversingVisitor`.
`StatisticsVisitor` was added in its own commit without touching any of those classes.

Adding a new element type is the opposite case: `Visitor` gets a new overload, so every visitor must be revisited.

## Tests

| Class | Checkpoints |
|-------|-------------|
| `DoubleDispatchTest` | T1 |
| `TotalSizeVisitorTest` | T2, T6 |
| `SearchVisitorTest` | T3, T4, T6 |
| `StatisticsVisitorTest` | T5, T6 |
| `RepeatedExecutionTest` | T7 |
| `TreeIntegrityTest` | T8 |
| `TraversalTest` | R7 |
