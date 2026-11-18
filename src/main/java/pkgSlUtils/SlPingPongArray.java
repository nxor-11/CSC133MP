package pkgSlUtils;

public class SlPingPongArray extends Sl2DIntArray {

    public static final String DEFAULT_OUTPUT_FILE = "SlPingPongArrayOutput.txt";

    private static final int MAX_VALUE = 1;
    private int[][] nextCellArray;
    private static final int MIN_VALUE = 0;
    protected static final int DEFAULT_VALUE = 0;

    public SlPingPongArray(int numRows, int numCols, int randMin, int randMax) {
        super(numRows, numCols);
        initArrays(numRows, numCols);
    }

    public SlPingPongArray(int numRows, int numCols) {
        super(numRows, numCols);
        initArrays(numRows, numCols);
    }

    private void initArrays(int numRows, int numCols) {
    }

    protected void swapLiveAndNext() {
    }

    public int getCellValue(int row, int col) {
        return 0;
    }

    protected void setCellValue(int row, int col, int newVal) {
    }

    public void loadFile(String dataFilePath) {
    }

    protected int getN2NeighborsSum(int row, int col) {
        return 0;
    }
}