package com.example.rpgplatform.MagicRoom;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.example.rpgplatform.Components.AttackType;
import com.example.rpgplatform.Components.EntityType;

public class SkillToPlayerHandler extends CollisionHandler {
    public SkillToPlayerHandler() {
        super(AttackType.WARRIOR_BASIC,EntityType.PLAYER);
    }



    @Override
    protected void onCollision(Entity a, Entity b) {
        super.onCollision(a, b);
    }
}
