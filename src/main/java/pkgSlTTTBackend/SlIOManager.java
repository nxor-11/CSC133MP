package pkgSlTTTBackend;

import java.util.Scanner;

public class SlIOManager {

    private Scanner myScanner;

    public SlIOManager() {
        myScanner = new Scanner(System.in);
    }

    protected static void initPrompt() {
        System.out.println("Welcome to your unproductive time of the day!");
        System.out.println("Enter valid row, column numbers separated by a space!\n");
    }

    protected static void rowColPrompt() {
        System.out.print(
                "Enter two numbers (0, 1, or 2) separated by a space, "
                        + "or 'q' to quit: ");
    }

    protected static void invalidEntryMessage() {
        System.out.println("Invalid input! Try again.");
    }

    public static void cellNotFreeMessage(final int row, final int col) {
        System.out.println(
                "cell  [" + row + ", " + col + "] is not available!");
    }

    protected static void machineWinMessage() {
        System.out.println(
                "Try again - this time machine outsmarted you!\n");
    }

    protected static void playerWinMessage() {
        System.out.println(
                "\nCongratulations you have outsmarted the computer!!\n");
    }

    protected static void gameDrawMessage() {
        System.out.println(
                "That round was a draw; let's play another round!\n");
    }

    protected static void playAgainMessage() {
        System.out.println(
                "Beginning another round; type \"q\" to quit!\n");
    }

    protected static void quitGameMessage() {
        System.out.println(
                "Good bye - game over, come again ASAP and waste more time!");
    }

    protected static void errorInPlayMessage() {
        System.out.println("Something went wrong - bailing out!");
    }

    protected int[] readInputData() { //method for reading input, keeps player inputs within boundsby throwing error if out of biunds
        rowColPrompt();

        String input = myScanner.nextLine().trim();

        if (input.equalsIgnoreCase("q")) {
            return new int[]{SlTTTSPOT.GAME_QUIT};
        }

        String[] values = input.split("\\s+");

        if (values.length != 2) {
            invalidEntryMessage();
            return new int[]{-1, -1};
        }

        try {
            int row = Integer.parseInt(values[0]);
            int col = Integer.parseInt(values[1]);

            if (row < 0 || row >= SlTTTSPOT.NUM_ROWS
                    || col < 0 || col >= SlTTTSPOT.NUM_COLS) {
                invalidEntryMessage();
                return new int[]{-1, -1};
            }

            return new int[]{row, col};

        } catch (NumberFormatException error) {
            invalidEntryMessage();
            return new int[]{-1, -1};
        }
    }

    protected static void printBoard(final SlTTTBoard board) { //method for creating game board
        System.out.println();

        for (int row = 0; row < SlTTTSPOT.NUM_ROWS; row++) {
            System.out.println(
                    " " + board.getContent(row, 0)
                            + " | " + board.getContent(row, 1)
                            + " | " + board.getContent(row, 2));

            if (row < SlTTTSPOT.NUM_ROWS - 1) {
                System.out.println("---+---+---");
            }
        }

        System.out.println();
    }
}