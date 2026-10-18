package pkgDriver;

import pkgSlTTTBackend.SlMachinePlayer;

import static pkgSlTTTBackend.SlTTTSPOT.*;

public class DriverTTT {

    public static void main(String[] args) {
        SlMachinePlayer myPlayer = new SlMachinePlayer();

        int retVal = GAME_INCOMPLETE;

        while (retVal == GAME_INCOMPLETE) {
            retVal = myPlayer.play();

            if (retVal != GAME_QUIT) {
                retVal = GAME_INCOMPLETE;
                myPlayer.clearBoard();
                myPlayer.playAgainMessage();
            }
        }
    }
}
