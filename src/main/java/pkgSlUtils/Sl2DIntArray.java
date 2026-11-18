package pkgSlUtils;

public class Sl2DIntArray {

    private Long randomSeed;
    private int[][] arrayData;

    public Sl2DIntArray(int[][] rawData) {
    }

    public Sl2DIntArray(String filePath) {
    }

    public Sl2DIntArray(int numRows, int numCols) {
    }

    public void printNeighbors(int nDistance, int[][] rgRowsCols) {
    }

    protected void setDataArray(int[][] newData) {
    }

    protected int[][] swapDataArray(int[][] newArray) {
        return null;
    }

    protected void setData(int row, int col, int newData) {
    }

    public int getData(int row, int col) {
        return 0;
    }

    protected SlRCPair[] getN1NeighborsArray(int orgRow, int orgCol) {
        return null;
    }

    public SlRCPair[] getN2NeighborsArray(int orgRow, int orgCol) {
        return null;
    }

    public SlRCPair[] getN3NeighborsArray(int orgRow, int orgCol) {
        return null;
    }

    public void randomizeViaFisherYatesKnuth(Long myInteger) {
    }

    public void loadFile(String dataFilePath) {
    }

    public int[] getNumRowsCols() {
        return null;
    }

    public boolean saveToFile(String outFilePath, int defaultValue) {
        return false;
    }

    public void printArray(String title) {
    }

    protected int[][] getDataArray() {
        return null;
    }

    public int[][] getDataClone() {
        return null;
    }
}