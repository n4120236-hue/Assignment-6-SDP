package edu.aitu.visitor;

record SampleTree(
        DirectoryNode root,
        DirectoryNode docs,
        DirectoryNode deep,
        DirectoryNode voidDir,
        DirectoryNode emptyDir,
        FileNode rootFile) {

    static SampleTree create() {
        FileNode rootFile = new FileNode("a.txt", 10);
        DirectoryNode voidDir = new DirectoryNode("void");
        DirectoryNode deep = new DirectoryNode("deep")
                .add(new FileNode("a.txt", 70))
                .add(new FileNode("data.log", 400))
                .add(voidDir);
        DirectoryNode docs = new DirectoryNode("docs")
                .add(new FileNode("guide.txt", 30))
                .add(new FileNode("spec.txt", 500))
                .add(deep);
        DirectoryNode emptyDir = new DirectoryNode("empty");
        DirectoryNode root = new DirectoryNode("root")
                .add(rootFile)
                .add(new FileNode("notes.log", 250))
                .add(docs)
                .add(emptyDir);
        return new SampleTree(root, docs, deep, voidDir, emptyDir, rootFile);
    }
}
