package edu.aitu.visitor;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        DirectoryNode project = buildProject();
        printTotalSize(project);
        printSearches(project);
        printStatistics(project);
        printRepeatedRun(project);
        printRejectedChanges(project);
    }

    private static DirectoryNode buildProject() {
        DirectoryNode util = new DirectoryNode("util")
                .add(new FileNode("Numbers.java", 1_100))
                .add(new FileNode("Strings.java", 900));
        DirectoryNode main = new DirectoryNode("main")
                .add(new FileNode("App.java", 5_200))
                .add(util);
        DirectoryNode test = new DirectoryNode("test")
                .add(new FileNode("AppTest.java", 2_100));
        DirectoryNode src = new DirectoryNode("src").add(main).add(test);
        DirectoryNode images = new DirectoryNode("images")
                .add(new FileNode("logo.png", 15_000))
                .add(new DirectoryNode("diagrams"));
        DirectoryNode docs = new DirectoryNode("docs")
                .add(new FileNode("guide.md", 4_300))
                .add(images);
        return new DirectoryNode("project")
                .add(new FileNode("README.md", 1_200))
                .add(new FileNode("build.log", 88_000))
                .add(docs)
                .add(src)
                .add(new DirectoryNode("tmp"));
    }

    private static void printTotalSize(DirectoryNode project) {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        project.accept(visitor);
        System.out.println("== TotalSizeVisitor ==");
        System.out.println("project total bytes: " + visitor.totalSize());
        project.children().stream()
                .filter(child -> child.name().equals("src"))
                .forEach(child -> {
                    child.accept(visitor);
                    System.out.println("src total bytes: " + visitor.totalSize());
                });
    }

    private static void printSearches(DirectoryNode project) {
        System.out.println("== SearchVisitor ==");
        printSearch(project, "*.java, size >= 1000", NameCriteria.endsWith(".java"), 1_000);
        printSearch(project, "*.md, size >= 0", NameCriteria.endsWith(".md"), 0);
        printSearch(project, "any name, size >= 10000", NameCriteria.any(), 10_000);
        printSearch(project, "contains 'zip', size >= 0", NameCriteria.contains("zip"), 0);
    }

    private static void printSearch(
            DirectoryNode root,
            String description,
            java.util.function.Predicate<String> criterion,
            long minimumSize) {
        SearchVisitor visitor = new SearchVisitor(criterion, minimumSize);
        root.accept(visitor);
        System.out.println("[" + description + "] -> " + visitor.matches());
    }

    private static void printStatistics(DirectoryNode project) {
        StatisticsVisitor visitor = new StatisticsVisitor();
        project.accept(visitor);
        System.out.println("== StatisticsVisitor ==");
        System.out.println("files: " + visitor.fileCount()
                + ", directories: " + visitor.directoryCount()
                + ", max depth: " + visitor.maxDepth());
    }

    private static void printRepeatedRun(DirectoryNode project) {
        StatisticsVisitor visitor = new StatisticsVisitor();
        project.accept(visitor);
        project.accept(visitor);
        System.out.println("== Repeated run on the same visitor ==");
        System.out.println("files after two runs: " + visitor.fileCount());
    }

    private static void printRejectedChanges(DirectoryNode project) {
        System.out.println("== Rejected changes ==");
        reject("negative size", () -> new FileNode("bad.bin", -5));
        reject("duplicate name", () -> project.add(new FileNode("README.md", 1)));
        FileNode owned = new FileNode("owned.txt", 1);
        new DirectoryNode("first").add(owned);
        reject("second parent", () -> project.add(owned));
        reject("cycle", () -> project.add(project));
    }

    private static void reject(String label, Runnable change) {
        try {
            change.run();
            System.out.println(label + ": ACCEPTED (unexpected)");
        } catch (RuntimeException e) {
            System.out.println(label + ": rejected by " + e);
        }
    }
}
