package com.sengine.engine;

import org.lwjgl.BufferUtils;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


public class LevelEditorScene extends Scene{
    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;

//    private String vertexShaderSrc = "#version 330\n" +
//            "layout (location = 0) in vec3 aPos;\n" +
//            "layout (location = 1) in vec4 aColor;\n" +
//            "\n" +
//            "out vec4 fColor;\n" +
//            "\n" +
//            "void main()\n" +
//            "{\n" +
//            "    fColor = aColor;\n" +
//            "    gl_Position = vec4(aPos, 1.0f);\n" +
//            "}";
//    private String fragmentShaderSrc = "#version 330\n" +
//            "in vec4 fColor;\n" +
//            "\n" +
//            "out vec4 color;\n" +
//            "\n" +
//            "void main()\n" +
//            "{\n" +
//            "    color = fColor;\n" +
//            "}";

    private String vertexShaderSrc = "#version 330 \n" +
            "layout (location = 0) in vec3 pos; \n" +
            "out vec3 position; \n" +
            "void main() \n" +
            "{ \n" +
            "    gl_Position = vec4(pos, 1.0); \n" +
            "    position = pos; \n" +
            "} ";
    private String fragmentShaderSrc = "#version 330\n" +
            "out vec4 colour;\n" +
            "in vec3 position;\n" +
            "void main()\n" +
            "{\n" +
            "    float a = position.x * 2;\n" +
            "    float b = position.y * 2;\n" +
            "    float n = 0.0f;\n" +
            "\n" +
            "    float aa = (a * a) - (b * b) + a;\n" +
            "    float bb = 2 * (a * b) + b;\n" +
            "\n" +
            "    float cc = 0.0f;\n" +
            "    float dd = 0.0f;\n" +
            "\n" +
            "    while(abs(aa * aa + bb * bb) < 4) {\n" +
            "        n += 0.05;\n" +
            "        if(n > 1.0f) break;\n" +
            "\n" +
            "        cc = aa;\n" +
            "        dd = bb;\n" +
            "        aa = cc * cc - dd * dd + a;\n" +
            "        bb = 2 * cc * dd + b;\n" +
            "    }\n" +
            "\n" +
            "    colour = vec4(0.0f, n, n, n);\n" +
            "    \n" +
            "    \n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;


    float val = 1.0f;
    private float[] vertexArray = {
            // position          // color
            val, -val, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f,
            -val, val, 0.0f,   0.0f, 1.0f, 0.0f, 1.0f,
            val, val, 0.0f,   0.0f, 0.0f, 1.0f, 1.0f,
            -val, -val, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f
    };

    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        System.out.println("Level Editor Scene.");
    }

    @Override
    public void init() {
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("vertex Shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("fragment Shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("shader program linking failed.");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vaoID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        int positionSize = 3;
        int colorSize = 4;
        int vertexSize = (positionSize + colorSize) * 4;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSize, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSize, 3 * 4);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        glUseProgram(shaderProgram);
        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        glUseProgram(0);
    }
}
