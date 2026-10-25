package pkgDriver;
public class SPOT {
    public final static int NUM_ROWS = 11, NUM_COLS = 7, SIZE_H = 50,
            SIZE_V = 40, OFFSET = 20, PADDING = 10;
    public final static int WIN_WIDTH = (SIZE_H + PADDING) * NUM_COLS + 2 * OFFSET
            - PADDING;
    public final static int WIN_HEIGHT = (SIZE_V + PADDING) * NUM_ROWS + 2 * OFFSET
            - PADDING;
    public final static int WIN_ORG_X = 50, WIN_ORG_Y = 80;
    public static final int VIEW_NEAR = 0, VIEW_FAR = 10;
}
