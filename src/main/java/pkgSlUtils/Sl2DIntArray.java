package pkgSlUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
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
        System.out.println("N" + nDistance + "Neighbors:");

        for (int[] rowCol : rgRowsCols) {
            int row = rowCol[0];
            int col = rowCol[1];

            SlRCPair[] neighbors;

            if (nDistance == 1) {
                neighbors = getN1NeighborsArray(row, col);
            } else if (nDistance == 2) {
                neighbors = getN2NeighborsArray(row, col);
            } else if (nDistance == 3) {
                neighbors = getN3NeighborsArray(row, col);
            } else {
                throw new IllegalArgumentException(
                        "Neighbor distance must be 1, 2, or 3."
                );
            }

            System.out.print("[" + row + "][" + col + "]: ");

            for (SlRCPair neighbor : neighbors) {
                System.out.print(
                        "{" + neighbor.row()
                                + ", " + neighbor.col()
                                + "}, "
                );
            }

            System.out.println();
        }

        System.out.println();
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
        int numRows = arrayData.length;
        int numCols = arrayData[0].length;

        int upRow = (orgRow - 1 + numRows) % numRows;
        int downRow = (orgRow + 1) % numRows;
        int leftCol = (orgCol - 1 + numCols) % numCols;
        int rightCol = (orgCol + 1) % numCols;

        return new SlRCPair[] {
                new SlRCPair(upRow, orgCol),
                new SlRCPair(orgRow, leftCol),
                new SlRCPair(downRow, orgCol),
                new SlRCPair(orgRow, rightCol)
        };
    }

    public SlRCPair[] getN2NeighborsArray(int orgRow, int orgCol) {
        int numRows = arrayData.length;
        int numCols = arrayData[0].length;

        int upRow = (orgRow - 1 + numRows) % numRows;
        int downRow = (orgRow + 1) % numRows;
        int leftCol = (orgCol - 1 + numCols) % numCols;
        int rightCol = (orgCol + 1) % numCols;

        return new SlRCPair[] {
                new SlRCPair(upRow, rightCol),
                new SlRCPair(upRow, orgCol),
                new SlRCPair(upRow, leftCol),
                new SlRCPair(orgRow, leftCol),
                new SlRCPair(downRow, leftCol),
                new SlRCPair(downRow, orgCol),
                new SlRCPair(downRow, rightCol),
                new SlRCPair(orgRow, rightCol)
        };
    }

    public SlRCPair[] getN3NeighborsArray(int orgRow, int orgCol) {
        int numRows = arrayData.length;
        int numCols = arrayData[0].length;

        int rowAbove2 = (orgRow - 2 + numRows) % numRows;
        int rowAbove1 = (orgRow - 1 + numRows) % numRows;
        int rowBelow1 = (orgRow + 1) % numRows;
        int rowBelow2 = (orgRow + 2) % numRows;

        int colLeft2 = (orgCol - 2 + numCols) % numCols;
        int colLeft1 = (orgCol - 1 + numCols) % numCols;
        int colRight1 = (orgCol + 1) % numCols;
        int colRight2 = (orgCol + 2) % numCols;

        return new SlRCPair[] {
                new SlRCPair(rowAbove2, colRight2),
                new SlRCPair(rowAbove2, colRight1),
                new SlRCPair(rowAbove2, orgCol),
                new SlRCPair(rowAbove2, colLeft1),
                new SlRCPair(rowAbove2, colLeft2),

                new SlRCPair(rowAbove1, colRight2),
                new SlRCPair(rowAbove1, colLeft2),

                new SlRCPair(orgRow, colRight2),
                new SlRCPair(orgRow, colLeft2),

                new SlRCPair(rowBelow1, colRight2),
                new SlRCPair(rowBelow1, colLeft2),

                new SlRCPair(rowBelow2, colRight2),
                new SlRCPair(rowBelow2, colRight1),
                new SlRCPair(rowBelow2, orgCol),
                new SlRCPair(rowBelow2, colLeft1),
                new SlRCPair(rowBelow2, colLeft2)
        };
    }

    public void randomizeViaFisherYatesKnuth(Long myInteger) {
        randomSeed = myInteger;

        Random random = new Random(myInteger);

        int numRows = arrayData.length;
        int numCols = arrayData[0].length;
        int totalElements = numRows * numCols;

        for (int currentIndex = totalElements - 1;
             currentIndex > 0;
             currentIndex--) {

            int randomIndex = random.nextInt(currentIndex + 1);

            int currentRow = currentIndex / numCols;
            int currentCol = currentIndex % numCols;

            int randomRow = randomIndex / numCols;
            int randomCol = randomIndex % numCols;

            int temporaryValue = arrayData[currentRow][currentCol];

            arrayData[currentRow][currentCol] =
                    arrayData[randomRow][randomCol];

            arrayData[randomRow][randomCol] = temporaryValue;
        }
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
        try (PrintWriter outputFile =
                     new PrintWriter(new FileWriter(outFilePath))) {

            int numRows = arrayData.length;
            int numCols = arrayData[0].length;

            outputFile.println(defaultValue);
            outputFile.println(numRows + " " + numCols);

            for (int row = 0; row < numRows; row++) {
                outputFile.print(row);

                for (int col = 0; col < numCols; col++) {
                    outputFile.print(" " + arrayData[row][col]);
                }

                outputFile.println();
            }

            return true;

        } catch (IOException error) {
            return false;
        }
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