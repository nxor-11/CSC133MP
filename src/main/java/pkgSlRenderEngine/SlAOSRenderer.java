package pkgSlRenderEngine;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import static pkgDriver.SPOT.*;

public class SlAOSRenderer {

    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWFramebufferSizeCallback fbCallback;

    private long glfwWindow;

    private int windowWidth = WIN_WIDTH;
    private int windowHeight = WIN_HEIGHT;

    private int vbo = 0;
    private int ibo = 0;
    private int vao = 0;

    private int projMatLocation;
    private int viewMatLocation;

    private SlVertexDataManager myVertexDataManager;

    public SlAOSRenderer() {
    }

    public void render() {
        try {
            initGLFWindow();
            renderLoop();
        } finally {
            glfwDestroyWindow(glfwWindow);

            if (keyCallback != null) {
                keyCallback.free();
            }

            if (fbCallback != null) {
                fbCallback.free();
            }

            glfwTerminate();
            Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        }
    }

    private void initGLFWindow() {
        glfwSetErrorCallback(errorCallback =
                GLFWErrorCallback.createPrint(System.err));

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

        glfwWindow = glfwCreateWindow(
                windowWidth,
                windowHeight,
                "CSC133",
                NULL,
                NULL);

        if (glfwWindow == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        glfwSetKeyCallback(glfwWindow, keyCallback =
                new GLFWKeyCallback() {
                    @Override
                    public void invoke(
                            long window,
                            int key,
                            int scancode,
                            int action,
                            int mods) {

                        if (key == GLFW_KEY_ESCAPE &&
                                action == GLFW_RELEASE) {
                            glfwSetWindowShouldClose(window, true);
                        }
                    }
                });

        glfwSetFramebufferSizeCallback(glfwWindow, fbCallback =
                new GLFWFramebufferSizeCallback() {
                    @Override
                    public void invoke(long window, int width, int height) {
                        if (width > 0 && height > 0) {
                            windowWidth = width;
                            windowHeight = height;
                        }
                    }
                });

        glfwSetWindowPos(glfwWindow, WIN_ORG_X, WIN_ORG_Y);
        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);
    }

    private void renderLoop() {
        initOpenGL();
        renderScene();
    }

    private void initOpenGL() {
        GL.createCapabilities();

        glViewport(0, 0, windowWidth, windowHeight);
        glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        int shaderProgram = glCreateProgram();

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);

        String vertexShaderSource =
                "#version 410 core\n" +
                        "layout (location = 0) in vec3 aPos;\n" +
                        "layout (location = 1) in vec4 aColor;\n" +
                        "uniform mat4 uProjMatrix;\n" +
                        "uniform mat4 uViewMatrix;\n" +
                        "out vec4 fColor;\n" +
                        "void main() {\n" +
                        "    fColor = aColor;\n" +
                        "    gl_Position = uProjMatrix * " +
                        "uViewMatrix * vec4(aPos, 1.0);\n" +
                        "}";

        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        glAttachShader(shaderProgram, vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        String fragmentShaderSource =
                "#version 410 core\n" +
                        "in vec4 fColor;\n" +
                        "out vec4 outColor;\n" +
                        "void main() {\n" +
                        "    outColor = fColor;\n" +
                        "}";

        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        glAttachShader(shaderProgram, fragmentShader);

        glLinkProgram(shaderProgram);
        glUseProgram(shaderProgram);

        projMatLocation =
                glGetUniformLocation(shaderProgram, "uProjMatrix");

        viewMatLocation =
                glGetUniformLocation(shaderProgram, "uViewMatrix");
    }

    private void renderScene() {
        myVertexDataManager =
                new SlVertexDataManager(NUM_ROWS, NUM_COLS, 7);

        myVertexDataManager.generateTileArrayVertices(
                NUM_ROWS,
                NUM_COLS,
                OFFSET,
                PADDING,
                SIZE_H,
                SIZE_V);

        myVertexDataManager.generateTileArrayIndices(
                NUM_ROWS,
                NUM_COLS);

        vbo = glGenBuffers();
        ibo = glGenBuffers();
        vao = glGenVertexArrays();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

        float[] vertices = myVertexDataManager.getVArray();
        int[] indices = myVertexDataManager.getIArray();

        FloatBuffer vertexBuffer =
                BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        IntBuffer indexBuffer =
                BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();

        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        int vertexStride = 7 * Float.BYTES;

        glVertexAttribPointer(
                0,
                3,
                GL_FLOAT,
                false,
                vertexStride,
                0);

        glEnableVertexAttribArray(0);

        glVertexAttribPointer(
                1,
                4,
                GL_FLOAT,
                false,
                vertexStride,
                3L * Float.BYTES);

        glEnableVertexAttribArray(1);

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glViewport(0, 0, windowWidth, windowHeight);

            updateMatrices();

            glDrawElements(
                    GL_TRIANGLES,
                    myVertexDataManager.getArrayLength(),
                    GL_UNSIGNED_INT,
                    0L);

            glfwSwapBuffers(glfwWindow);
        }

        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
        glDeleteVertexArrays(vao);
    }

    private void updateMatrices() {
        Matrix4f projection = new Matrix4f();
        Matrix4f view = new Matrix4f();

        projection.setOrtho(
                0.0f,
                windowWidth,
                0.0f,
                windowHeight,
                VIEW_NEAR,
                VIEW_FAR);

        view.identity();

        FloatBuffer matrixBuffer =
                BufferUtils.createFloatBuffer(16);

        glUniformMatrix4fv(
                projMatLocation,
                false,
                projection.get(matrixBuffer));

        matrixBuffer.clear();

        glUniformMatrix4fv(
                viewMatLocation,
                false,
                view.get(matrixBuffer));
    }
}