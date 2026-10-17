package pkgDriver;

// When the SlTIMRenderer is created, remove this line and uncomment the next:
//import pkgSlRenderEngine.SlTMRenderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
// <AI PROMPT>
// Added GL30 imports for vertex array object calls required by OpenGL core profile.
import static org.lwjgl.opengl.GL30.*;
// </AI PROMPT>

import static org.lwjgl.system.MemoryUtil.NULL;

public class DriverPoC {
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWFramebufferSizeCallback fbCallback;
    private long glfwWindow;
    private static int WIN_WIDTH = 1800, WIN_HEIGHT = 1200,
            WIN_POS_X = 30, WIN_POX_Y = 90;
    private int projMatLocation = 0, viewMatLocation = 0;

    private float[] vertexArray;
    private int[] indexArray;
    private int vArrayNextIndex = 0, iArrayNextIndex = 0;
    private final int FPV = 9, VPT = 3, IPT = 3;

    public static void main(String[] args) {
        new DriverPoC().render();
        } // public static void main(String[] args)

    private void initVertexIndexArrays(int numTriangles) {
        vertexArray = new float[numTriangles * VPT * FPV];
        indexArray = new int[numTriangles * IPT];
    }  //  private void initVertexIndexArrays(...)

    private void fillVertices(float x0, float y0, float x1, float y1, float x2, float y2,
                              float cR, float cG, float cB, float cA) {
        final float zmin = 0.0f, umin = 0.0f, vmin = 0.0f, umax = 1.0f, vmax = 1.0f;
        float[] tmpArray = new float[] { x0, y0, zmin, umin, vmin, cR, cG, cB, cA,
                x1, y1, zmin, umax, vmin, cR, cG, cB, cA,
                x2, y2, zmin, umin, vmin, cR, cG, cB, cA};
        System.arraycopy(tmpArray, 0, vertexArray, vArrayNextIndex, tmpArray.length);
        int vStart =  vArrayNextIndex/(VPT*IPT);
        vArrayNextIndex += tmpArray.length;
        indexArray[iArrayNextIndex++] = vStart;
        indexArray[iArrayNextIndex++] = vStart + 1;
        indexArray[iArrayNextIndex++] = vStart + 2;
    }  //  private void fillVertices(...)

    private void fillVertexArray() {
        Arrays.fill(vertexArray, 0.0f);
        Arrays.fill(indexArray, 0);

        final float sqLength = 300f;
        float x0 = WIN_WIDTH/2.0f - sqLength, y0 = WIN_HEIGHT/2.0f;

        //notice the counter-clockwise winding order of the vertices:
        // top left square
        fillVertices(x0, y0, x0+sqLength, y0, x0+sqLength, y0+sqLength,
                1.0f, 0.4f, 0.6f, 1.0f);
        fillVertices(x0, y0, x0+sqLength, y0+sqLength, x0, y0+sqLength,
                1.0f, 0.4f, 0.6f, 1.0f);
        // lower right square
        x0 += sqLength; y0 -= sqLength;
        fillVertices(x0, y0, x0+sqLength, y0, x0+sqLength, y0+sqLength,
                1.0f, 0.4f, 0.6f, 1.0f);
        fillVertices(x0, y0, x0+sqLength, y0+sqLength, x0, y0+sqLength,
                1.0f, 0.4f, 0.6f, 1.0f);
    }  //  private void fillVertexArray()

    public void render() {
        try {
            // allocate for the vertices - if the total number of vertices change,
            // you will have to call initVertexIndexArrays(...) again with the new
            // number for the triangles
            final int TOTAL_TRIS = 4;
            initVertexIndexArrays(TOTAL_TRIS);

            initGLFWindow();
            renderLoop();
            glfwDestroyWindow(glfwWindow);
            keyCallback.free();
            fbCallback.free();
        } finally {
            glfwTerminate();
            Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        }
    } // void render()
    private void initGLFWindow() {
        glfwSetErrorCallback(errorCallback =
                GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, 8);

        // <AI PROMPT>
        // macOS supports OpenGL only up to 4.1 and requires a forward-compatible
        // core profile context. The previous 4.6/compatibility setup cannot create
        // a valid window on macOS.
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        // </AI PROMPT>
        // When we create the GLFW Window here, the OpenGL context is created
        // implicitly; but the context needs to be explicitly made current
        glfwWindow = glfwCreateWindow(WIN_WIDTH, WIN_HEIGHT, "CSC 133", NULL, NULL);
        if (glfwWindow == NULL)
            throw new RuntimeException("CSC133 Error: Failed to create the GLFW window");
        glfwSetKeyCallback(glfwWindow, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int
                    mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, true);
            }
        });
        glfwSetFramebufferSizeCallback(glfwWindow, fbCallback = new
                GLFWFramebufferSizeCallback() {
                    @Override
                    public void invoke(long window, int w, int h) {
                        if (w > 0 && h > 0) {
                            WIN_WIDTH = w;
                            WIN_HEIGHT = h;
                        }
                    }
                });
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(glfwWindow, WIN_POS_X, WIN_POX_Y);
        glfwMakeContextCurrent(glfwWindow);
        int VSYNC_INTERVAL = 1;
        glfwSwapInterval(VSYNC_INTERVAL);
        glfwShowWindow(glfwWindow);
    } // private void initGLFWindow()
    void renderLoop() {
        initOpenGL();
        renderScene();
    } // void renderLoop()
    void initOpenGL() {
        GL.createCapabilities();
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        int shader_program = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);

        if (glGetError() != GL_NO_ERROR) {
            String infoLog = glGetShaderInfoLog(vs, 500);
            System.err.println(infoLog);
        }  //  if (glError != GL_NO_ERROR)

        // <AI PROMPT>
        // Changed shaders from GLSL 460 compatibility to GLSL 410 core for macOS.
        // Core profile also requires using the declared vertex attribute instead
        // of the removed compatibility variable gl_Vertex.
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
        glAttachShader(shader_program, vs);
        int fs = glCreateShader(GL_FRAGMENT_SHADER);

        // <AI PROMPT>
        // Matched the fragment shader version to the macOS-supported 4.1 core
        // profile requested above.
        final String stringPS = "#version 410 core\n" +
                "uniform sampler2D TEX_SAMPLER;" +
                "uniform vec3 color;" +
                "in vec4 fColor;" +
                "out vec4 outColor;" +
                "void main(void) {" +
                "   outColor = fColor;" +
                "}";
        // </AI PROMPT>
        glShaderSource(fs, stringPS);
        glCompileShader(fs);
        glAttachShader(shader_program, fs);
        glLinkProgram(shader_program);
        glUseProgram(shader_program);
        projMatLocation = glGetUniformLocation(shader_program, "uProjMatrix");
        viewMatLocation = glGetUniformLocation(shader_program, "uViewMatrix");
        return;
    } // void initOpenGL()

    void renderScene() {

        int vbo = glGenBuffers();
        int ibo = glGenBuffers();
        // <AI PROMPT>
        // Added a VAO because OpenGL core profile requires one before configuring
        // vertex attributes. Compatibility calls like glEnableClientState are not
        // available in the macOS core profile path.
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        // </AI PROMPT>
        fillVertexArray();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.
                createFloatBuffer(vertexArray.length).
                put(vertexArray).flip(), GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.
                createIntBuffer(indexArray.length).
                put(indexArray).flip(), GL_STATIC_DRAW);

        Matrix4f uProjMatrix = new Matrix4f();
        Matrix4f uViewMatrix = new Matrix4f();
        uProjMatrix.setOrtho(0.0f, WIN_WIDTH, 0.0f, WIN_HEIGHT, 0, 100.0f);

        final int OGL_MATRIX_SIZE = 16;
        FloatBuffer myFloatBuffer = BufferUtils.createFloatBuffer(OGL_MATRIX_SIZE);
        glUniformMatrix4fv(projMatLocation, false,
                uProjMatrix.get(myFloatBuffer));

        final Vector3f defaultLookFrom = new Vector3f(0.0f, 0.0f, 00.0f);
        final Vector3f defaultLookAt = new Vector3f(0.0f, 0.0f, -1.0f);
        final Vector3f defaultUpVector = new Vector3f(0.0f, 1.0f, 0.0f);
        uViewMatrix.identity();
        uViewMatrix.lookAt(defaultLookFrom, defaultLookAt.add(defaultLookFrom), defaultUpVector);
        glUniformMatrix4fv(viewMatLocation, false,
                uViewMatrix.get(myFloatBuffer));

        // <AI PROMPT>
        // Use FRONT_AND_BACK so fill mode applies consistently in core profile.
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        // </AI PROMPT>
        final int posStride = 3, texStride = 2, colStride = 4,
                vertexStride = posStride + texStride + colStride;
        final int vpoIndex = 0, vtoIndex = 1, vcoIndex = 2;

        glVertexAttribPointer(vpoIndex, posStride, GL_FLOAT, false, vertexStride * Float.BYTES, 0);
        glEnableVertexAttribArray(vpoIndex);
        glVertexAttribPointer(vtoIndex, texStride, GL_FLOAT, false, vertexStride * Float.BYTES,
                (long)(posStride * Float.BYTES));
        glEnableVertexAttribArray(vtoIndex);
        glVertexAttribPointer(vcoIndex, colStride, GL_FLOAT, false, vertexStride * Float.BYTES,
                (long)(posStride + texStride)*Float.BYTES) ;
        glEnableVertexAttribArray(vcoIndex);

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // <AI PROMPT>
            // Draw exactly the number of indices uploaded to the element buffer.
            // The previous count was derived from float-array position data and
            // could ask OpenGL to read past the valid index list.
            glDrawElements(GL_TRIANGLES, indexArray.length, GL_UNSIGNED_INT, 0L);
            // </AI PROMPT>
            glfwSwapBuffers(glfwWindow);
        }
    } // renderScene(...)

}  //  public class DriverPoC
