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
}