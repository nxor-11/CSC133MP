package pkgSlRenderEngine;

import java.util.Random;

public class SlVertexDataManager {

    private final int VPT = 4; // vertices per tile
    private final int IPT = 6; // indices per tile
    private final int FPV = 7; // x, y, z, r, g, b, a

    private float[] vArray;
    private int[] iArray;
    private Random myRandom;

    public SlVertexDataManager(
            final int numRows,
            final int numCols,
            final int floatsPerVertex) {

        myRandom = new Random();

        vArray = new float[numRows * numCols * VPT * floatsPerVertex];
        iArray = new int[numRows * numCols * IPT];
    }

    public int getArrayLength() {
        return iArray.length;
    }

    public float[] getRandomColor() {
        return new float[]{
                myRandom.nextFloat(),
                myRandom.nextFloat(),
                myRandom.nextFloat(),
                1.0f
        };
    }

    public int[] getIArray() {
        return iArray;
    }

    public void generateTileArrayIndices(
            final int numRows,
            final int numCols) {

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int tileNumber = row * numCols + col;
                int startIndex = tileNumber * IPT;
                int startVertexNum = tileNumber * VPT;

                generateTileVertexIndices(startIndex, startVertexNum);
            }
        }
    }

    public float[] getVArray() {
        return vArray;
    }

    public int getVArrayLength() {
        return vArray.length;
    }

    public void generateTileArrayVertices(
            final int numRows,
            final int numCols,
            final float offset,
            final float padding,
            final float sizeH,
            final float sizeV) {

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int tileNumber = row * numCols + col;
                int startIndex = tileNumber * VPT * FPV;

                float orgX = offset + col * (sizeH + padding);
                float orgY = offset + row * (sizeV + padding);

                generateTileGeometry(
                        startIndex,
                        orgX,
                        orgY,
                        sizeH,
                        sizeV);
            }
        }
    }

    private void generateTileGeometry(
            final int startIndex,
            final float orgX,
            final float orgY,
            final float sizeH,
            final float sizeV) {

        float[] color = getRandomColor();

        addVertex(startIndex, orgX, orgY, color);
        addVertex(startIndex + FPV, orgX + sizeH, orgY, color);
        addVertex(startIndex + 2 * FPV,
                orgX + sizeH,
                orgY + sizeV,
                color);
        addVertex(startIndex + 3 * FPV,
                orgX,
                orgY + sizeV,
                color);
    }

    private void generateTileVertexIndices(
            final int startIndex,
            final int startVertexNum) {

        iArray[startIndex] = startVertexNum;
        iArray[startIndex + 1] = startVertexNum + 1;
        iArray[startIndex + 2] = startVertexNum + 2;

        iArray[startIndex + 3] = startVertexNum + 2;
        iArray[startIndex + 4] = startVertexNum + 3;
        iArray[startIndex + 5] = startVertexNum;
    }

    private void addVertex(
            final int startIndex,
            final float x,
            final float y,
            final float[] color) {

        vArray[startIndex] = x;
        vArray[startIndex + 1] = y;
        vArray[startIndex + 2] = 0.0f;
        vArray[startIndex + 3] = color[0];
        vArray[startIndex + 4] = color[1];
        vArray[startIndex + 5] = color[2];
        vArray[startIndex + 6] = color[3];
    }
}