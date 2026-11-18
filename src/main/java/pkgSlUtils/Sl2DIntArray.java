package pkgSlUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Sl2DIntArray {

    private Long randomSeed;
    private int[][] arrayData;

    public Sl2DIntArray(int[][] rawData) {
        arrayData = rawData;
    }

    public Sl2DIntArray(String filePath) {
        loadFile(filePath);
    }

    public Sl2DIntArray(int numRows, int numCols) {
        arrayData = new int[numRows][numCols];
    }

    public void printNeighbors(int nDistance, int[][] rgRowsCols) {
    }

    protected void setDataArray(int[][] newData) {
        arrayData = newData;
    }

    protected int[][] swapDataArray(int[][] newArray) {
        int[][] oldArray = arrayData;
        arrayData = newArray;
        return oldArray;
    }

    protected void setData(int row, int col, int newData) {
        arrayData[row][col] = newData;
    }

    public int getData(int row, int col) {
        return arrayData[row][col];
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
        try {
            Scanner inputFile = new Scanner(new File(dataFilePath));

            int defaultValue = inputFile.nextInt();
            int numRows = inputFile.nextInt();
            int numCols = inputFile.nextInt();

            arrayData = new int[numRows][numCols];

            for (int row = 0; row < numRows; row++) {
                Arrays.fill(arrayData[row], defaultValue);
            }

            for (int row = 0; row < numRows; row++) {
                int fileRow = inputFile.nextInt();

                for (int col = 0; col < numCols; col++) {
                    arrayData[fileRow][col] = inputFile.nextInt();
                }
            }

            inputFile.close();

        } catch (FileNotFoundException error) {
            throw new IllegalArgumentException(
                    "Could not find data file: " + dataFilePath,
                    error
            );
        }
    }

    public int[] getNumRowsCols() {
        return new int[] {
                arrayData.length,
                arrayData[0].length
        };
    }

    public boolean saveToFile(String outFilePath, int defaultValue) {
        return false;
    }

    public void printArray(String title) {
        System.out.println(title);

        for (int row = 0; row < arrayData.length; row++) {
            for (int col = 0; col < arrayData[row].length; col++) {
                System.out.printf("%3d", arrayData[row][col]);
            }

            System.out.println();
        }
    }

    protected int[][] getDataArray() {
        return arrayData;
    }

    public int[][] getDataClone() {
        int[][] clonedArray = new int[arrayData.length][arrayData[0].length];

        for (int row = 0; row < arrayData.length; row++) {
            for (int col = 0; col < arrayData[row].length; col++) {
                clonedArray[row][col] = arrayData[row][col];
            }
        }

        return clonedArray;
    }
}