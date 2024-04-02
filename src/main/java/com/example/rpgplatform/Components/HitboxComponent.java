package com.example.rpgplatform.Components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.HitBox;

public class HitboxComponent extends Component {
    private final HitBox hitBox;

    public HitboxComponent(HitBox hitBox) {
        this.hitBox = hitBox;
    }
}
