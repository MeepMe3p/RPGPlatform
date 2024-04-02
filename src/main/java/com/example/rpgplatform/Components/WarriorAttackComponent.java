package com.example.rpgplatform.Components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;

public class WarriorAttackComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel basicAttack;
    private boolean isBasic;

    public WarriorAttackComponent(){
//        TODO ANIMATION STUFF

    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16,21));
        entity.getViewComponent().addChild(texture);

    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
    }


}
