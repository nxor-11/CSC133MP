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
}