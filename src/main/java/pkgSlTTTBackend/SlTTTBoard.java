package pkgSlTTTBackend;

public class SlTTTBoard {

    private char[][] tttBoard;

    public SlTTTBoard() {
        tttBoard = new char[SlTTTSPOT.NUM_ROWS][SlTTTSPOT.NUM_COLS];
        clearBoard();
    }

    public char getContent(final int row, final int col) {
        return tttBoard[row][col];
    }

    public char[][] getBoard() {
        char[][] boardCopy =
                new char[SlTTTSPOT.NUM_ROWS][SlTTTSPOT.NUM_COLS];

        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
                boardCopy[row][col] = tttBoard[row][col];
            }
        }

        return boardCopy;
    }

    public void clearBoard() {
        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
                tttBoard[row][col] = SlTTTSPOT.DEFAULT_CHAR;
            }
        }
    }

    public boolean setContent(
            final int row,
            final int col,
            final char content) {

        if (row < 0 || row >= SlTTTSPOT.NUM_ROWS
                || col < 0 || col >= SlTTTSPOT.NUM_COLS) {
            return false;
        }

        if (tttBoard[row][col] != SlTTTSPOT.DEFAULT_CHAR) {
            return false;
        }

        if (content != SlTTTSPOT.PLAYER_CHAR
                && content != SlTTTSPOT.MACHINE_CHAR) {
            return false;
        }

        tttBoard[row][col] = content;
        return true;
    }
}