package com.sengine.components;

import com.sengine.engine.Component;

public class SpriteRenderer extends Component {
    private boolean firstTime = false;

    @Override
    public void start() {
        System.out.println("SP started.");
    }

    @Override
    public void update(float dt) {
        if(!firstTime) {
            System.out.println("SP updated");
            firstTime = true;
        }
    }
}
