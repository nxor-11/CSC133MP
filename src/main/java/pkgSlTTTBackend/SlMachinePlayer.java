package pkgSlTTTBackend;

public class SlMachinePlayer {

    private SlTTTBoard myT3Board;
    private SlIOManager myIOManager;
    private int gameStatus;

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

        myT3Board.setContent(
                machineRow,
                machineCol,
                SlTTTSPOT.MACHINE_CHAR);

        SlIOManager.printBoard(myT3Board);
    }
}