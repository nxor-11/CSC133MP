package pkgDriver;

import pkgSlUtils.Sl2DIntArray;

public class DriverMP {

    public static void main(String[] args) {
        printNeighbors();
        FYKDemo();
    }

    private static void printNeighbors() {
        Sl2DIntArray myIA =
                new Sl2DIntArray("src/main/resources/ppa_test1.txt");

        myIA.printArray("ppa_test1.txt");
        System.out.println();

        int[][] rgRowsCols = new int[][] {
                {0, 0},
                {4, 0},
                {4, 6},
                {0, 6},
                {2, 3}
        };

        myIA.printNeighbors(1, rgRowsCols);
        myIA.printNeighbors(2, rgRowsCols);
        myIA.printNeighbors(3, rgRowsCols);
    }

    private static void FYKDemo() {
        Sl2DIntArray myIA =
                new Sl2DIntArray("src/main/resources/ppa_test1.txt");

        final Long randomSeed = 1728L;

        myIA.randomizeViaFisherYatesKnuth(randomSeed);

        myIA.printArray(
                "After Fisher-Yates-Knuth randomization with seed: "
                        + randomSeed
        );
    }
}