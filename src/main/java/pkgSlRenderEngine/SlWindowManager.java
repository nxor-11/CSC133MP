package pkgSlRenderEngine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SlWindowManager {
    private GLFWFramebufferSizeCallback fbCallback;
    private long windowHandle;
    private static int winHeight;
    private static int winWidth;
    private static SlWindowManager myWindow;
    private GLFWKeyCallback keyCallback;
    private GLFWFramebufferSizeCallback resizeWindow;
    private GLFWErrorCallback errorCallback;

    private SlWindowManager() {
    }

    public static SlWindowManager get(int width, int height) {
        if (myWindow == null) {
            winWidth = width;
            winHeight = height;
            myWindow = new SlWindowManager();
            initGlfwWindow();
        }

        return myWindow;
    }

    public boolean isGlfwWindowClosed() {
        return glfwWindowShouldClose(windowHandle);
    }

    private static SlWindowManager get() {
        return myWindow;
    }

    public int[] getWindowSize() {
        return new int[] {winWidth, winHeight};
    }

    private static void initGlfwWindow() {
        myWindow.errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(myWindow.errorCallback);
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, 8);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

        myWindow.windowHandle = glfwCreateWindow(winWidth, winHeight, "CSC 133", NULL, NULL);
        if (myWindow.windowHandle == NULL) {
            throw new RuntimeException("CSC133 Error: Failed to create the GLFW window");
        }

        myWindow.enableSetKeyCallback();
        myWindow.enableResizeWindowCallback();
        myWindow.setWindowPosition(30, 90);
        myWindow.updateContextToThis();
        glfwSwapInterval(1);
        glfwShowWindow(myWindow.windowHandle);
    }

    public void setWinWidth(int width, int height) {
        winWidth = width;
        winHeight = height;
    }

    public void enableResizeWindowCallback() {
        resizeWindow = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                if (width > 0 && height > 0) {
                    winWidth = width;
                    winHeight = height;
                }
            }
        };
        fbCallback = resizeWindow;
        glfwSetFramebufferSizeCallback(windowHandle, fbCallback);
    }

    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
    }

    public void destroyGlfwWindow() {
        glfwDestroyWindow(windowHandle);
        keyCallback.free();
        fbCallback.free();
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void setWindowPosition(int x, int y) {
        glfwSetWindowPos(windowHandle, x, y);
    }

    public void updateContextToThis() {
        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();
        glViewportToWindow();
    }

    private void enableSetKeyCallback() {
        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
        };
        glfwSetKeyCallback(windowHandle, keyCallback);
    }

    private void glViewportToWindow() {
        org.lwjgl.opengl.GL11.glViewport(0, 0, winWidth, winHeight);
    }
}
