package pkgSlRenderEngine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SlTMRenderer {
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWFramebufferSizeCallback fbCallback;
    private long glfwWindow;

    private static int WIN_WIDTH = 1800, WIN_HEIGHT = 1200,
            WIN_POS_X = 30, WIN_POX_Y = 90;
    private int projMatLocation = 0, viewMatLocation = 0;

    private final TriangleMesh originalBunny;
    private final TriangleMesh scaledBunny;
    private TriangleMesh activeMesh;
    private boolean meshUploadNeeded = true;

    private int vbo = 0, ibo = 0, vao = 0;

    private static final int FPV = 9;
    private static final int VPT = 3;

    // <AI PROMPT>
    // Added renderer-owned resource names so DriverTM uses both bunny data files
    // after they were moved into src/main/resources.
    private static final String ORIGINAL_BUNNY_RESOURCE = "bunnyDataHouseFormat.txt";
    private static final String SCALED_BUNNY_RESOURCE = "bunnyDataHouseFormatScaled.txt";
    // </AI PROMPT>

    public SlTMRenderer(int trianglesToRender) {
        // <AI PROMPT>
        // Load both assignment data files up front. The scaled bunny is the
        // default because its coordinates fit the existing orthographic window
        // better, and SPACE toggles between the two meshes at runtime.
        originalBunny = loadTriangleMesh(ORIGINAL_BUNNY_RESOURCE, trianglesToRender);
        scaledBunny = loadTriangleMesh(SCALED_BUNNY_RESOURCE, trianglesToRender);
        activeMesh = scaledBunny;
        // </AI PROMPT>
    }

    public void render() {
        try {
            initGLFWindow();
            renderLoop();
            glfwDestroyWindow(glfwWindow);
            keyCallback.free();
            fbCallback.free();
        } finally {
            glfwTerminate();
            Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        }
    }

    private void initGLFWindow() {
        glfwSetErrorCallback(errorCallback =
                GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, 8);

        // <AI PROMPT>
        // Kept the macOS-compatible OpenGL context settings from the working
        // DriverPoC renderer so DriverTM can launch on this machine too.
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        // </AI PROMPT>

        glfwWindow = glfwCreateWindow(WIN_WIDTH, WIN_HEIGHT, "CSC 133 Bunny TM", NULL, NULL);
        if (glfwWindow == NULL)
            throw new RuntimeException("CSC133 Error: Failed to create the GLFW window");
        glfwSetKeyCallback(glfwWindow, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                }
                // <AI PROMPT>
                // Added a simple way to demonstrate that both bunny data files
                // are wired into the assignment: press SPACE to toggle them.
                if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) {
                    activeMesh = activeMesh == scaledBunny ? originalBunny : scaledBunny;
                    meshUploadNeeded = true;
                }
                // </AI PROMPT>
            }
        });
        glfwSetFramebufferSizeCallback(glfwWindow, fbCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                if (w > 0 && h > 0) {
                    WIN_WIDTH = w;
                    WIN_HEIGHT = h;
                }
            }
        });
        glfwSetWindowPos(glfwWindow, WIN_POS_X, WIN_POX_Y);
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
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        int shaderProgram = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);

        // <AI PROMPT>
        // Added the same GLSL 410 core shaders used in the fixed proof of
        // concept, preserving color data parsed from the bunny text files.
        final String stringVS = "#version 410 core\n" +
                "layout (location=0) in vec3 aPos;" +
                "layout (location=1) in vec2 aTexCoords;" +
                "layout (location=2) in vec4 aColor;" +
                "uniform mat4 uProjMatrix;" +
                "uniform mat4 uViewMatrix;" +
                "out vec2 fTexCoords;" +
                "out vec4 fColor;" +
                "void main(void) {" +
                "   fTexCoords = aTexCoords;" +
                "   fColor = aColor;" +
                "   gl_Position = uProjMatrix * uViewMatrix * vec4(aPos, 1.0);" +
                "}";
        // </AI PROMPT>
        glShaderSource(vs, stringVS);
        glCompileShader(vs);
        glAttachShader(shaderProgram, vs);

        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        final String stringPS = "#version 410 core\n" +
                "in vec4 fColor;" +
                "out vec4 outColor;" +
                "void main(void) {" +
                "   outColor = fColor;" +
                "}";
        glShaderSource(fs, stringPS);
        glCompileShader(fs);
        glAttachShader(shaderProgram, fs);
        glLinkProgram(shaderProgram);
        glUseProgram(shaderProgram);

        projMatLocation = glGetUniformLocation(shaderProgram, "uProjMatrix");
        viewMatLocation = glGetUniformLocation(shaderProgram, "uViewMatrix");
    }

    private void renderScene() {
        vbo = glGenBuffers();
        ibo = glGenBuffers();
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

        final int posStride = 3, texStride = 2, colStride = 4;
        final int vertexStride = posStride + texStride + colStride;
        glVertexAttribPointer(0, posStride, GL_FLOAT, false, vertexStride * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, texStride, GL_FLOAT, false, vertexStride * Float.BYTES,
                (long)(posStride * Float.BYTES));
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, colStride, GL_FLOAT, false, vertexStride * Float.BYTES,
                (long)(posStride + texStride) * Float.BYTES);
        glEnableVertexAttribArray(2);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            // <AI PROMPT>
            // Moved glViewport out of the framebuffer callback because GLFW can
            // fire that callback before GL.createCapabilities() has created the
            // current thread's OpenGL bindings, which caused DriverTM to abort.
            glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
            // </AI PROMPT>

            if (meshUploadNeeded) {
                uploadActiveMesh();
                meshUploadNeeded = false;
            }

            updateMatrices();
            glDrawElements(GL_TRIANGLES, activeMesh.indexArray.length, GL_UNSIGNED_INT, 0L);
            glfwSwapBuffers(glfwWindow);
        }
    }

    private void uploadActiveMesh() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils
                .createFloatBuffer(activeMesh.vertexArray.length)
                .put(activeMesh.vertexArray)
                .flip(), GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils
                .createIntBuffer(activeMesh.indexArray.length)
                .put(activeMesh.indexArray)
                .flip(), GL_STATIC_DRAW);
    }

    private void updateMatrices() {
        Matrix4f uProjMatrix = new Matrix4f();
        Matrix4f uViewMatrix = new Matrix4f();
        uProjMatrix.setOrtho(0.0f, WIN_WIDTH, 0.0f, WIN_HEIGHT, 0, 100.0f);

        final int oglMatrixSize = 16;
        FloatBuffer myFloatBuffer = BufferUtils.createFloatBuffer(oglMatrixSize);
        glUniformMatrix4fv(projMatLocation, false, uProjMatrix.get(myFloatBuffer));

        final Vector3f defaultLookFrom = new Vector3f(0.0f, 0.0f, 0.0f);
        final Vector3f defaultLookAt = new Vector3f(0.0f, 0.0f, -1.0f);
        final Vector3f defaultUpVector = new Vector3f(0.0f, 1.0f, 0.0f);
        uViewMatrix.identity();
        uViewMatrix.lookAt(defaultLookFrom, defaultLookAt.add(defaultLookFrom), defaultUpVector);
        glUniformMatrix4fv(viewMatLocation, false, uViewMatrix.get(myFloatBuffer));
    }

    private TriangleMesh loadTriangleMesh(String resourceName, int maxTriangles) {
        // <AI PROMPT>
        // Added parser for the assignment's comma-separated triangle data:
        // x0,y0,x1,y1,x2,y2,r,g,b,a per row.
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int triangleCount = 0;

        try (InputStream inputStream = SlTMRenderer.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing resource: " + resourceName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null && triangleCount < maxTriangles) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.isEmpty()) {
                        continue;
                    }
                    String[] parts = trimmedLine.split("\\s*,\\s*");
                    if (parts.length > 0 && parts[parts.length - 1].isEmpty()) {
                        parts = java.util.Arrays.copyOf(parts, parts.length - 1);
                    }
                    if (parts.length != 10) {
                        throw new IllegalStateException("Expected 10 values in " + resourceName + ": " + trimmedLine);
                    }

                    float x0 = Float.parseFloat(parts[0]);
                    float y0 = Float.parseFloat(parts[1]);
                    float x1 = Float.parseFloat(parts[2]);
                    float y1 = Float.parseFloat(parts[3]);
                    float x2 = Float.parseFloat(parts[4]);
                    float y2 = Float.parseFloat(parts[5]);
                    float r = Float.parseFloat(parts[6]);
                    float g = Float.parseFloat(parts[7]);
                    float b = Float.parseFloat(parts[8]);
                    float a = Float.parseFloat(parts[9]);

                    int vertexStart = triangleCount * VPT;
                    addVertex(vertices, x0, y0, r, g, b, a);
                    addVertex(vertices, x1, y1, r, g, b, a);
                    addVertex(vertices, x2, y2, r, g, b, a);
                    indices.add(vertexStart);
                    indices.add(vertexStart + 1);
                    indices.add(vertexStart + 2);
                    triangleCount++;
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read resource: " + resourceName, ex);
        }

        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }
        int[] indexArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indexArray[i] = indices.get(i);
        }
        return new TriangleMesh(vertexArray, indexArray);
        // </AI PROMPT>
    }

    private void addVertex(List<Float> vertices, float x, float y, float r, float g, float b, float a) {
        final float z = 0.0f, u = 0.0f, v = 0.0f;
        vertices.add(x);
        vertices.add(y);
        vertices.add(z);
        vertices.add(u);
        vertices.add(v);
        vertices.add(r);
        vertices.add(g);
        vertices.add(b);
        vertices.add(a);
    }

    private static class TriangleMesh {
        private final float[] vertexArray;
        private final int[] indexArray;

        private TriangleMesh(float[] vertexArray, int[] indexArray) {
            this.vertexArray = vertexArray;
            this.indexArray = indexArray;
        }
    }
}
