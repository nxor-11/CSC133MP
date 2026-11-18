package pkgSlUtils;

import java.util.Random;

public class SlPingPongArray extends Sl2DIntArray {

    public static final String DEFAULT_OUTPUT_FILE =
            "SlPingPongArrayOutput.txt";

    private static final int MAX_VALUE = 1;
    private int[][] nextCellArray;
    private static final int MIN_VALUE = 0;
    protected static final int DEFAULT_VALUE = 0;

    public SlPingPongArray(
            int numRows,
            int numCols,
            int randMin,
            int randMax
    ) {
        super(numRows, numCols);
        initArrays(numRows, numCols);

        Random random = new Random();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int value = randMin
                        + random.nextInt(randMax - randMin + 1);

                setCellValue(row, col, value);
            }
        }
    }

    public SlPingPongArray(int numRows, int numCols) {
        super(numRows, numCols);
        initArrays(numRows, numCols);
    }

    private void initArrays(int numRows, int numCols) {
        nextCellArray = new int[numRows][numCols];
    }

    protected void swapLiveAndNext() {
        int[][] oldLiveArray = swapDataArray(nextCellArray);
        nextCellArray = oldLiveArray;
    }

    public int getCellValue(int row, int col) {
        return getData(row, col);
    }

    protected void setCellValue(int row, int col, int newVal) {
        setData(row, col, newVal);
    }

    public void loadFile(String dataFilePath) {
        super.loadFile(dataFilePath);

        int[] dimensions = getNumRowsCols();
        nextCellArray = new int[dimensions[0]][dimensions[1]];
    }

    protected int getN2NeighborsSum(int row, int col) {
        SlRCPair[] neighbors = getN2NeighborsArray(row, col);

        int sum = 0;

        for (SlRCPair neighbor : neighbors) {
            sum += getCellValue(neighbor.row(), neighbor.col());
        }

        return sum;
    }
}