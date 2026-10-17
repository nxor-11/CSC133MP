package pkgDriver;

import pkgSlRenderEngine.SlTMRenderer;

public class DriverTM {
        public static void main(String[] args) {

            // <AI PROMPT>
            // Updated DriverTM to render all 30 triangle rows from the copied
            // bunny data resources instead of only the first 20 triangles.
            final int trianglesToRender = 30;
            // </AI PROMPT>
            SlTMRenderer currentScene = new SlTMRenderer(trianglesToRender);
            currentScene.render();
        } // public static void main(String[] args)
}  //  public class DriverTM

