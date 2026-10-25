package pkgSlRenderEngine;

import java.util.Random;

public class SlVertexDataManager {

    private int VPT;
    private float[] vArray;
    private int[] iArray;
    private int IPT;
    private int FPV;
    private Random myRandom;

    public SlVertexDataManager(int numRows, int numCols, int size) {
        myRandom = new Random();
    }

    public int getArrayLength() {
        return 0;
    }

    public float[] getRandomColor() {
        return new float[4];
    }

    public int[] getIArray() {
        return iArray;
    }

    public void generateTileArrayIndices(final int numRows, final int numCols) {
    }

    public float[] getVArray() {
        return vArray;
    }

    public int getVArrayLength() {
        return 0;
    }
    public void generateTileArrayVertices(
            final int numRows,
            final int numCols,
            final float offset,
            final float padding,
            final float sizeH,
            final float sizeV) {
    }

    private void generateTileGeometry(
            final int startIndex,
            final float orgX,
            final float orgY,
            final float sizeH,
            final float sizeV) {
    }

    private void generateTileVertexIndices(
            final int startIndex,
            final int startVertexNum) {
    }
}