package com.example.rpgplatform.MagicRoom;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.example.rpgplatform.Components.EntityType;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;

public class PlayerButtonHandler extends CollisionHandler {
    public PlayerButtonHandler() {
        super(EntityType.PLAYER, EntityType.BUTTON);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity btn) {
        Entity keyEntity = btn.getObject("keyEntity");

        if(!keyEntity.isActive()){
            keyEntity.setProperty("activated",false);
            getGameWorld().addEntity(keyEntity);
        }

        keyEntity.setOpacity(1); //SET ME TO 1
    }

    @Override
    protected void onCollisionEnd(Entity player, Entity btn) {
        Entity keyEntity = btn.getObject("keyEntity");
        if(!keyEntity.getBoolean("activated")){
            keyEntity.setOpacity(0);
        }
    }
}
