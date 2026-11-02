package edu.aitu.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class RepeatedExecutionTest {

    @Test
    void totalSizeVisitorDoesNotAccumulateAcrossRuns() {
        SampleTree tree = SampleTree.create();
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        tree.root().accept(visitor);
        tree.root().accept(visitor);
        tree.root().accept(visitor);

        assertEquals(1260, visitor.totalSize());
    }

    @Test
    void searchVisitorDoesNotDuplicateMatchesAcrossRuns() {
        SampleTree tree = SampleTree.create();
        SearchVisitor visitor = new SearchVisitor(NameCriteria.endsWith(".log"), 0);

        tree.root().accept(visitor);
        tree.root().accept(visitor);

        assertEquals(List.of("docs/deep/data.log", "notes.log"), visitor.matches());
    }

    @Test
    void statisticsVisitorDoesNotMultiplyCountsAcrossRuns() {
        SampleTree tree = SampleTree.create();
        StatisticsVisitor visitor = new StatisticsVisitor();

        tree.root().accept(visitor);
        tree.root().accept(visitor);

        assertEquals(6, visitor.fileCount());
        assertEquals(5, visitor.directoryCount());
        assertEquals(3, visitor.maxDepth());
    }

    @Test
    void secondRunOnSmallerSubtreeReplacesEarlierResult() {
        SampleTree tree = SampleTree.create();
        StatisticsVisitor stats = new StatisticsVisitor();
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(NameCriteria.any(), 0);

        tree.root().accept(stats);
        tree.root().accept(size);
        tree.root().accept(search);
        tree.deep().accept(stats);
        tree.deep().accept(size);
        tree.deep().accept(search);

        assertEquals(2, stats.fileCount());
        assertEquals(2, stats.directoryCount());
        assertEquals(1, stats.maxDepth());
        assertEquals(470, size.totalSize());
        assertEquals(List.of("a.txt", "data.log"), search.matches());
    }

    @Test
    void runAfterTreeMutationReflectsCurrentTree() {
        SampleTree tree = SampleTree.create();
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        tree.root().accept(visitor);

        tree.emptyDir().add(new FileNode("late.bin", 40));
        tree.root().accept(visitor);

        assertEquals(1300, visitor.totalSize());
    }

    @Test
    void resultsDoNotDependOnVisitorRunOrder() {
        SampleTree firstTree = SampleTree.create();
        TotalSizeVisitor sizeFirst = new TotalSizeVisitor();
        StatisticsVisitor statsFirst = new StatisticsVisitor();
        SearchVisitor searchFirst = new SearchVisitor(NameCriteria.endsWith(".txt"), 50);
        firstTree.root().accept(sizeFirst);
        firstTree.root().accept(statsFirst);
        firstTree.root().accept(searchFirst);

        SampleTree secondTree = SampleTree.create();
        TotalSizeVisitor sizeSecond = new TotalSizeVisitor();
        StatisticsVisitor statsSecond = new StatisticsVisitor();
        SearchVisitor searchSecond = new SearchVisitor(NameCriteria.endsWith(".txt"), 50);
        secondTree.root().accept(searchSecond);
        secondTree.root().accept(statsSecond);
        secondTree.root().accept(sizeSecond);

        assertEquals(sizeFirst.totalSize(), sizeSecond.totalSize());
        assertEquals(statsFirst.fileCount(), statsSecond.fileCount());
        assertEquals(statsFirst.directoryCount(), statsSecond.directoryCount());
        assertEquals(statsFirst.maxDepth(), statsSecond.maxDepth());
        assertEquals(searchFirst.matches(), searchSecond.matches());
    }

    @Test
    void emptyRunAfterNonEmptyRunClearsMatches() {
        SampleTree tree = SampleTree.create();
        SearchVisitor visitor = new SearchVisitor(NameCriteria.any(), 0);

        tree.deep().accept(visitor);
        new DirectoryNode("empty").accept(visitor);

        assertEquals(List.of(), visitor.matches());
    }
}
