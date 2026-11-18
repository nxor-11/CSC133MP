package pkgDriver;

import pkgSlUtils.Sl2DIntArray;
import pkgSlUtils.SlPingPongArray;
import pkgSlUtils.SlRCPair;

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

        int myNDistance = 1;
        myIA.printNeighbors(myNDistance, rgRowsCols);

        myNDistance = 2;
        myIA.printNeighbors(myNDistance, rgRowsCols);

        myNDistance = 3;
        myIA.printNeighbors(myNDistance, rgRowsCols);
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