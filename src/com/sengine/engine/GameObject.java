package com.sengine.engine;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> components;
    public Transform transform;

    public GameObject(String name) {
        this.name = name;
        transform = new Transform();
        components = new ArrayList<>();
    }

    public GameObject(String name, Transform transform) {
        this.name = name;
        this.transform = transform;
        components = new ArrayList<>();
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for(Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting component.";
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeCompoent(Class<T> componetClass) {
        for(int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if(componetClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        components.add(c);
        c.gameObject = this;
    }

    public void update(float dt) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).update(dt);
        }
    }

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }
}
