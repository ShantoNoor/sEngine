package com.sengine.engine;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    public Scene() { }
    public abstract void update(float dt);
    public void init() { }

    public void start() {
        for(GameObject go : gameObjects) {
            go.start();
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        gameObjects.add(go);
        if(isRunning) {
            go.start();
        }
    }
}
