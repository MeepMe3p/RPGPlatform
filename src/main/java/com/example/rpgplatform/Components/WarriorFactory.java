package com.example.rpgplatform.Components;

import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.multiplayer.NetworkComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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
//                ARIGATOOOOOOO GOZAIMASUUUUU NAIJERUUU KUUUN YOU TASUKETE ME SO MATS
                .with(new ExpireCleanComponent(Duration.seconds(0.3)))
                // TODO: ADD MORE .with(new NetworkComponent()) (?) ASIDE FROM THIS ENTITY
                .with(new NetworkComponent())  // ADDED FOR MULTIPLAYER?
                .build();
    }
    @Spawns("WarriorSkill")
    public Entity newWarriorSkill(SpawnData data){
        return entityBuilder(data)
                .type(WARRIOR_SLASH)
                .viewWithBBox(new Rectangle(45,85,Color.RED))
//                .with(new ProjectileComponent(new Point2D(1,0),400))
//              SHEEEEEHSSS NICE KAAA NAIJERUUU KUUUN
                .with(new ExpireCleanComponent(Duration.seconds(1)))
                // TODO: ADD MORE .with(new NetworkComponent()) (?) ASIDE FROM THIS ENTITY
                .with(new NetworkComponent())  // ADDED FOR MULTIPLAYER?
                .build();
    }
}
