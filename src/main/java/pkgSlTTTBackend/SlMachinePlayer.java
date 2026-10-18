package pkgSlTTTBackend;

public class SlMachinePlayer {

    private SlTTTBoard myT3Board;
    private SlIOManager myIOManager;
    private int gameStatus;

    public int getGameStatus() { //getter for game status
        return gameStatus;
    }

    public SlMachinePlayer() {
        myT3Board = new SlTTTBoard();
        myIOManager = new SlIOManager();
        gameStatus = SlTTTSPOT.NOT_STARTED;
    }

    public void clearBoard() {
        myT3Board.clearBoard();
    }

    public void playAgainMessage() {
        SlIOManager.playAgainMessage();
    }

    public int play() {
        gameStatus = SlTTTSPOT.GAME_INCOMPLETE;

        runFirstRound();

        if (gameStatus == SlTTTSPOT.GAME_QUIT) {
            printGameOverMessage(gameStatus);
            return gameStatus;
        }

        runMidGame();
        printGameOverMessage(gameStatus);

        return gameStatus;
    }

    protected int[] getUserInput() {
        int[] userInput;

        while (true) {
            userInput = myIOManager.readInputData();

            if (userInput.length == 1
                    && userInput[0] == SlTTTSPOT.GAME_QUIT) {
                return userInput;
            }

            if (userInput[0] < 0 || userInput[1] < 0) {
                continue;
            }

            if (myT3Board.getContent(userInput[0], userInput[1])
                    != SlTTTSPOT.DEFAULT_CHAR) {
                SlIOManager.cellNotFreeMessage(
                        userInput[0], userInput[1]);
                continue;
            }

            return userInput;
        }
    }

    protected void runFirstRound() {
        SlIOManager.initPrompt();

        int[] userInput = getUserInput();

        if (userInput.length == 1
                && userInput[0] == SlTTTSPOT.GAME_QUIT) {
            gameStatus = SlTTTSPOT.GAME_QUIT;
            return;
        }

        myT3Board.setContent(
                userInput[0],
                userInput[1],
                SlTTTSPOT.PLAYER_CHAR);

        int machineRow;
        int machineCol;

        if (userInput[0] == 1 && userInput[1] == 1) {
            machineRow = 0;
            machineCol = 0;
        } else {
            machineRow = 1;
            machineCol = 1;
        }

        startMovePlay(machineRow, machineCol);

        SlIOManager.printBoard(myT3Board);

        gameStatus = isGameOver();
    }

    protected int isGameOver() {
        if (hasWinningLine(SlTTTSPOT.PLAYER_CHAR)) {
            return SlTTTSPOT.GAME_PLAYER;
        }

        if (hasWinningLine(SlTTTSPOT.MACHINE_CHAR)) {
            return SlTTTSPOT.GAME_MACHINE;
        }

        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
                if (myT3Board.getContent(row, col)
                        == SlTTTSPOT.DEFAULT_CHAR) {
                    return SlTTTSPOT.GAME_INCOMPLETE;
                }
            }
        }

        return SlTTTSPOT.GAME_DRAW;
    }

    private boolean hasWinningLine(final char mark) {
        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            if (myT3Board.getContent(row, 0) == mark
                    && myT3Board.getContent(row, 1) == mark
                    && myT3Board.getContent(row, 2) == mark) {
                return true;
            }
        }

        for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
            if (myT3Board.getContent(0, col) == mark
                    && myT3Board.getContent(1, col) == mark
                    && myT3Board.getContent(2, col) == mark) {
                return true;
            }
        }

        if (myT3Board.getContent(0, 0) == mark
                && myT3Board.getContent(1, 1) == mark
                && myT3Board.getContent(2, 2) == mark) {
            return true;
        }

        return myT3Board.getContent(0, 2) == mark
                && myT3Board.getContent(1, 1) == mark
                && myT3Board.getContent(2, 0) == mark;
    }

    protected void printGameOverMessage(final int status) {
        switch (status) {
            case SlTTTSPOT.GAME_PLAYER:
                SlIOManager.playerWinMessage();
                break;

            case SlTTTSPOT.GAME_MACHINE:
                SlIOManager.machineWinMessage();
                break;

            case SlTTTSPOT.GAME_DRAW:
                SlIOManager.gameDrawMessage();
                break;

            case SlTTTSPOT.GAME_QUIT:
                SlIOManager.quitGameMessage();
                break;

            default:
                SlIOManager.errorInPlayMessage();
                break;
        }
    }

    protected boolean playToWin() {
        int[] winningMove =
                findImmediateMove(SlTTTSPOT.MACHINE_CHAR);

        if (winningMove == null) {
            return false;
        }

        myT3Board.setContent(
                winningMove[0],
                winningMove[1],
                SlTTTSPOT.MACHINE_CHAR);

        return true;
    }

    protected boolean preventWin() {
        int[] blockingMove =
                findImmediateMove(SlTTTSPOT.PLAYER_CHAR);

        if (blockingMove == null) {
            return false;
        }

        myT3Board.setContent(
                blockingMove[0],
                blockingMove[1],
                SlTTTSPOT.MACHINE_CHAR);

        return true;
    }

    private int[] findImmediateMove(final char mark) {
        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
                if (myT3Board.getContent(row, col)
                        == SlTTTSPOT.DEFAULT_CHAR
                        && wouldWin(row, col, mark)) {
                    return new int[]{row, col};
                }
            }
        }

        return null;
    }

    private boolean wouldWin(
            final int row,
            final int col,
            final char mark) {

        int rowMatches = 0;
        int colMatches = 0;

        for (int index = 0; index < SlTTTSPOT.NUM_COLS; index++) {
            char rowContent = index == col
                    ? mark
                    : myT3Board.getContent(row, index);

            char colContent = index == row
                    ? mark
                    : myT3Board.getContent(index, col);

            if (rowContent == mark) {
                rowMatches++;
            }

            if (colContent == mark) {
                colMatches++;
            }
        }

        if (rowMatches == SlTTTSPOT.NUM_COLS
                || colMatches == SlTTTSPOT.NUM_ROWS) {
            return true;
        }

        char topLeft = row == 0 && col == 0
                ? mark : myT3Board.getContent(0, 0);
        char center = row == 1 && col == 1
                ? mark : myT3Board.getContent(1, 1);
        char bottomRight = row == 2 && col == 2
                ? mark : myT3Board.getContent(2, 2);

        if (topLeft == mark && center == mark && bottomRight == mark) {
            return true;
        }

        char topRight = row == 0 && col == 2
                ? mark : myT3Board.getContent(0, 2);
        char bottomLeft = row == 2 && col == 0
                ? mark : myT3Board.getContent(2, 0);

        return topRight == mark && center == mark && bottomLeft == mark;
    }

    protected boolean takeCenter() {
        if (myT3Board.getContent(1, 1)
                == SlTTTSPOT.DEFAULT_CHAR) {
            myT3Board.setContent(
                    1,
                    1,
                    SlTTTSPOT.MACHINE_CHAR);
            return true;
        }

        return false;
    }

    protected boolean takeAnyCorner() {
        int[][] corners = {
                {0, 0},
                {0, 2},
                {2, 0},
                {2, 2}
        };

        for (int[] corner : corners) {
            if (myT3Board.getContent(corner[0], corner[1])
                    == SlTTTSPOT.DEFAULT_CHAR) {
                myT3Board.setContent(
                        corner[0],
                        corner[1],
                        SlTTTSPOT.MACHINE_CHAR);
                return true;
            }
        }

        return false;
    }

    protected boolean takeAnySide() {
        int[][] sides = {
                {0, 1},
                {1, 0},
                {1, 2},
                {2, 1}
        };

        for (int[] side : sides) {
            if (myT3Board.getContent(side[0], side[1])
                    == SlTTTSPOT.DEFAULT_CHAR) {
                myT3Board.setContent(
                        side[0],
                        side[1],
                        SlTTTSPOT.MACHINE_CHAR);
                return true;
            }
        }

        return false;
    }

    protected void midGamePlay() {
        while (gameStatus == SlTTTSPOT.GAME_INCOMPLETE) {
            int[] userInput = getUserInput();

            if (userInput.length == 1
                    && userInput[0] == SlTTTSPOT.GAME_QUIT) {
                gameStatus = SlTTTSPOT.GAME_QUIT;
                return;
            }

            myT3Board.setContent(
                    userInput[0],
                    userInput[1],
                    SlTTTSPOT.PLAYER_CHAR);

            SlIOManager.printBoard(myT3Board);

            gameStatus = isGameOver();

            if (gameStatus != SlTTTSPOT.GAME_INCOMPLETE) {
                return;
            }

            if (!playToWin()
                    && !preventWin()
                    && !createFork()) {
                blockFork();
            }

            SlIOManager.printBoard(myT3Board);
            gameStatus = isGameOver();
        }
    }

    protected boolean blockFork() {
        int[] bestMove = findBestMove();

        if (bestMove == null) {
            return false;
        }

        myT3Board.setContent(
                bestMove[0],
                bestMove[1],
                SlTTTSPOT.MACHINE_CHAR);

        return true;
    }

    private int[] findBestMove() {
        char[][] board = myT3Board.getBoard();
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
                if (board[row][col] == SlTTTSPOT.DEFAULT_CHAR) {
                    board[row][col] = SlTTTSPOT.MACHINE_CHAR;

                    int score = minimax(board, false);

                    board[row][col] = SlTTTSPOT.DEFAULT_CHAR;

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{row, col};
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(
            final char[][] board,
            final boolean machineTurn) {

        if (hasWinningLine(
                board,
                SlTTTSPOT.MACHINE_CHAR)) {
            return 1;
        }

        if (hasWinningLine(
                board,
                SlTTTSPOT.PLAYER_CHAR)) {
            return -1;
        }

        if (isFull(board)) {
            return 0;
        }

        if (machineTurn) {
            int bestScore = Integer.MIN_VALUE;

            for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
                for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
                    if (board[row][col]
                            == SlTTTSPOT.DEFAULT_CHAR) {

                        board[row][col] =
                                SlTTTSPOT.MACHINE_CHAR;

                        int score = minimax(board, false);

                        board[row][col] =
                                SlTTTSPOT.DEFAULT_CHAR;

                        bestScore = Math.max(bestScore, score);
                    }
                }
            }

            return bestScore;

        } else {
            int bestScore = Integer.MAX_VALUE;

            for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
                for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
                    if (board[row][col]
                            == SlTTTSPOT.DEFAULT_CHAR) {

                        board[row][col] =
                                SlTTTSPOT.PLAYER_CHAR;

                        int score = minimax(board, true);

                        board[row][col] =
                                SlTTTSPOT.DEFAULT_CHAR;

                        bestScore = Math.min(bestScore, score);
                    }
                }
            }

            return bestScore;
        }
    }

    private boolean isFull(final char[][] board) {
        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
                if (board[row][col]
                        == SlTTTSPOT.DEFAULT_CHAR) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasWinningLine(
            final char[][] board,
            final char mark) {

        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            if (board[row][0] == mark
                    && board[row][1] == mark
                    && board[row][2] == mark) {
                return true;
            }
        }

        for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
            if (board[0][col] == mark
                    && board[1][col] == mark
                    && board[2][col] == mark) {
                return true;
            }
        }

        return (board[0][0] == mark
                && board[1][1] == mark
                && board[2][2] == mark)
                || (board[0][2] == mark
                && board[1][1] == mark
                && board[2][0] == mark);
    }
    //helper methods for createforak and stuff
    private int countWinningMoves(
            final char[][] board,
            final char mark) {

        int count = 0;

        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
                if (board[row][col]
                        == SlTTTSPOT.DEFAULT_CHAR
                        && wouldWinOnBoard(board, row, col, mark)) {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean wouldWinOnBoard(
            final char[][] board,
            final int row,
            final int col,
            final char mark) {

        board[row][col] = mark;
        boolean result = hasWinningLine(board, mark);
        board[row][col] = SlTTTSPOT.DEFAULT_CHAR;

        return result;
    }

    protected boolean createFork() {
        char[][] board = myT3Board.getBoard();

        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
                if (board[row][col]
                        == SlTTTSPOT.DEFAULT_CHAR) {

                    board[row][col] =
                            SlTTTSPOT.MACHINE_CHAR;

                    int winningMoves =
                            countWinningMoves(
                                    board,
                                    SlTTTSPOT.MACHINE_CHAR);

                    board[row][col] =
                            SlTTTSPOT.DEFAULT_CHAR;

                    if (winningMoves >= 2) {
                        myT3Board.setContent(
                                row,
                                col,
                                SlTTTSPOT.MACHINE_CHAR);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected void runMidGame() {
        midGamePlay();
    }

    protected boolean isRowBlank(final int row) {
        for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
            if (myT3Board.getContent(row, col)
                    != SlTTTSPOT.DEFAULT_CHAR) {
                return false;
            }
        }

        return true;
    }

    protected boolean playTheRow(final int row) {
        for (int col = 0; col < SlTTTSPOT.NUM_COLS; col++) {
            if (myT3Board.getContent(row, col)
                    == SlTTTSPOT.DEFAULT_CHAR) {
                myT3Board.setContent(
                        row,
                        col,
                        SlTTTSPOT.MACHINE_CHAR);
                return true;
            }
        }

        return false;
    }

    protected boolean playTheCol(final int col) {
        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            if (myT3Board.getContent(row, col)
                    == SlTTTSPOT.DEFAULT_CHAR) {
                myT3Board.setContent(
                        row,
                        col,
                        SlTTTSPOT.MACHINE_CHAR);
                return true;
            }
        }

        return false;
    }

    protected boolean takeOtherCorner() {
        int[][] cornerPairs = {
                {0, 0, 2, 2},
                {2, 2, 0, 0},
                {0, 2, 2, 0},
                {2, 0, 0, 2}
        };

        for (int[] pair : cornerPairs) {
            if (myT3Board.getContent(pair[0], pair[1])
                    == SlTTTSPOT.PLAYER_CHAR
                    && myT3Board.getContent(pair[2], pair[3])
                    == SlTTTSPOT.DEFAULT_CHAR) {

                myT3Board.setContent(
                        pair[2],
                        pair[3],
                        SlTTTSPOT.MACHINE_CHAR);
                return true;
            }
        }

        return false;
    }

    protected void startMovePlay(
            final int row,
            final int col) {

        if (row >= 0
                && row < SlTTTSPOT.NUM_ROWS
                && col >= 0
                && col < SlTTTSPOT.NUM_COLS
                && myT3Board.getContent(row, col)
                == SlTTTSPOT.DEFAULT_CHAR) {

            myT3Board.setContent(
                    row,
                    col,
                    SlTTTSPOT.MACHINE_CHAR);
        }
    }
}