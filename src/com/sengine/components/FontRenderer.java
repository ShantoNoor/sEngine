package com.sengine.components;

import com.sengine.engine.Component;

public class FontRenderer extends Component {
    @Override
    public void start() {
        System.out.println("Starting Font Renderer. ");
        if(gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("found sp ss");
        } else {
            System.out.println("not found sp ss");
        }
    }

    @Override
    public void update(float dt) {

    }
}
