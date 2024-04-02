package com.example.rpgplatform.Components;

import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
import static com.example.rpgplatform.Components.AttackType.WARRIOR_BASIC;
import static com.example.rpgplatform.Components.AttackType.WARRIOR_SLASH;

public class WarriorFactory implements EntityFactory {
    @Spawns("WarriorBasic")
    public Entity newWarriorBasic(SpawnData data){
        return entityBuilder(data)
                .type(WARRIOR_BASIC)
                .bbox(new HitBox(new Point2D(45,85), BoundingShape.circle(30)))
                .viewWithBBox(new Circle(45,85,30,Color.RED))
                .with(new CollidableComponent(true))

                .build();
    }
    @Spawns("WarriorSkill")
    public Entity newWarriorSkill(SpawnData data){
        return entityBuilder(data)
                .type(WARRIOR_SLASH)
                .viewWithBBox(new Rectangle(45,85,Color.RED))
                .with(new ProjectileComponent(new Point2D(1,0),400))
                .build();
    }
}
