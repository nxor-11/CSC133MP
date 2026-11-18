package pkgDriver;

import pkgSlUtils.Sl2DIntArray;
import pkgSlUtils.SlPingPongArray;
import pkgSlUtils.SlRCPair;

public class DriverMP {

    public static void main(String[] args) {
        printNeighbors();
        FYKDemo();
        saveFileDemo();
        pingPongDemo();
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

    private static void saveFileDemo() {
        Sl2DIntArray originalArray =
                new Sl2DIntArray("src/main/resources/ppa_test1.txt");

        boolean saveWorked =
                originalArray.saveToFile("build/ppa_saved.txt", 1);

        System.out.println();
        System.out.println("saveToFile returned: " + saveWorked);

        Sl2DIntArray reloadedArray =
                new Sl2DIntArray("build/ppa_saved.txt");

        reloadedArray.printArray("Reloaded saved array");
    }

    private static void pingPongDemo() {
        TestPingPongArray pingPongArray =
                new TestPingPongArray(3, 3);

        pingPongArray.testSetCellValue(1, 1, 5);

        System.out.println();
        System.out.println("PingPongArray test:");
        System.out.println(
                "Before swap, center cell = "
                        + pingPongArray.getCellValue(1, 1)
        );

        pingPongArray.testSwapLiveAndNext();

        System.out.println(
                "After swap, center cell = "
                        + pingPongArray.getCellValue(1, 1)
        );
    }
    private static class TestPingPongArray extends SlPingPongArray {

        public TestPingPongArray(int numRows, int numCols) {
            super(numRows, numCols);
        }

        public void testSetCellValue(int row, int col, int value) {
            setCellValue(row, col, value);
        }

        public int testGetN2NeighborsSum(int row, int col) {
            return getN2NeighborsSum(row, col);
        }

        public void testSwapLiveAndNext() {
            swapLiveAndNext();
        }
    }
}