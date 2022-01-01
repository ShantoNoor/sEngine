package com.sengine.engine;

import com.sengine.components.FontRenderer;
import com.sengine.components.SpriteRenderer;
import com.sengine.renderer.Shader;
import com.sengine.renderer.Texture;
import com.sengine.util.Time;
import org.joml.Vector2f;
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

    private Shader defaultShader;
    private Texture testTexture;
    private GameObject testObject;

    private float[] vertexArray = {
            // position               // color                 // UV Coordinates
            100.5f,    0.5f, 0.0f,    1.0f, 0.0f, 0.0f, 1.0f,  1, 0, // Bottom right 0
              0.5f,  100.5f, 0.0f,    0.0f, 1.0f, 0.0f, 1.0f,  0, 1, // Top left     1
            100.5f,  100.5f, 0.0f,    1.0f, 0.0f, 1.0f, 1.0f,  1, 1, // Top right    2
              0.5f,    0.5f, 0.0f,    1.0f, 1.0f, 0.0f, 1.0f,  0, 0  // Bottom left  3
    };

    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        System.out.println("Level Editor Scene.");
    }

    private boolean testbool = true;

    @Override
    public void init() {
        testObject = new GameObject("Test Object");
        testObject.addComponent(new SpriteRenderer());
        testObject.addComponent(new FontRenderer());
        addGameObjectToScene(testObject);

        camera = new Camera(new Vector2f(-200, -300));
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        testTexture = new Texture("C:\\Users\\Shanto\\Desktop\\1.png");

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
        int uvSize = 2;
        int vertexSizeInBytes = (positionSize + colorSize + uvSize) * Float.BYTES;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeInBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, colorSize, GL_FLOAT, false, vertexSizeInBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {

        if(testbool) {
            testbool = false;
            System.out.println("C G 2");
            GameObject g = new GameObject("n");
            g.addComponent(new SpriteRenderer());
            g.addComponent(new FontRenderer());
            addGameObjectToScene(g);
        }

        defaultShader.use();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        defaultShader.detach();

        for(GameObject go : gameObjects) {
            go.update(dt);
        }
    }
}
