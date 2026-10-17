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
        gameStatus = SlTTTSPOT.GAME_QUIT;
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
}