package com.sengine.renderer;

import com.sengine.components.SpriteRenderer;
import com.sengine.engine.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    // Vertex
    // ======
    // Pos               Color
    // float, float,     float, float, float, float

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private final int AQuadHas4Vertices = 4;
    private final int AQuadHas6Indices = 6;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize) {
        shader = new Shader("assets/shaders/default.glsl");
        shader.compile();
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * AQuadHas4Vertices * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
    }

    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // create and upload ebo
        int eboID = glGenBuffers();
        int indices[] = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // enable the buffer attrib pointer
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

    }

    public void render() {
        // For now, rebuffer all data every frame
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, numSprites * AQuadHas6Indices, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }

    public void loadVertexProperties(int index) {
        SpriteRenderer sprite = sprites[index];
        int offset = index * AQuadHas4Vertices * VERTEX_SIZE;
        Vector4f color = sprite.getColor();

//        Add vertices with appropriate properties
        // 1,1 - 1,0 - 0,0 - 0,1
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for(int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }

            // Load position
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            offset += VERTEX_SIZE;
        }
    }

    public void addSprite(SpriteRenderer spr) {
        // get index and add renderObject
        int index = numSprites;
        sprites[index] = spr;
        numSprites++;

        // add properties to local vertices array
        loadVertexProperties(index);
        if(numSprites >= maxBatchSize) {
            hasRoom = false;
        }
    }

    private int[] generateIndices() {
        int[] elementArray = new int[AQuadHas6Indices * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elementArray, i);
        }
        return elementArray;
    }

    private void loadElementIndices(int[] elementArray, int index) {
//                 ignore comments
//                    v3---v0
//                     |  /|
//                     | / |
//                     |/  |
//                    v2---v1
//                           triangle1-v      triangle2-v
//    elementArrayShoudBe = [(3 2 0 0 2 1) , (7 6 4 4 6 5)]

        int offsetArrayIndex = AQuadHas6Indices * index;
        int offsetVertexNum = AQuadHas4Vertices * index;

        // triangle 1
        elementArray[offsetArrayIndex + 0] = offsetVertexNum + 3;
        elementArray[offsetArrayIndex + 1] = offsetVertexNum + 2;
        elementArray[offsetArrayIndex + 2] = offsetVertexNum + 0;

        // triangle 2
        elementArray[offsetArrayIndex + 3] = offsetVertexNum + 0;
        elementArray[offsetArrayIndex + 4] = offsetVertexNum + 2;
        elementArray[offsetArrayIndex + 5] = offsetVertexNum + 1;
    }

    public boolean hasRoom() {
        return hasRoom;
    }
}
