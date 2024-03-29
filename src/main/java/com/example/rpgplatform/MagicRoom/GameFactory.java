package com.example.rpgplatform.MagicRoom;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.example.rpgplatform.Components.PlayerComponent;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static com.example.rpgplatform.Components.EntityType.*;
import static com.example.rpgplatform.Config.WARRIOR_HP;

public class GameFactory implements EntityFactory {
    @Spawns("background")
    public Entity newBackground(SpawnData data){
        return entityBuilder()
                .view(new ScrollingBackgroundView(texture("background/windrise-background.png").getImage(), getAppWidth(),getAppHeight()))
                .zIndex(-1)
                .with(new IrremovableComponent())
                .build();
    }
    @Spawns("platform")
    public Entity newPlatform(SpawnData data){
        return entityBuilder(data)
                .type(PLATFORM)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data){
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR",new Point2D(16,38), BoundingShape.box(6,17)));
        physics.setFixtureDef(new FixtureDef().friction(0.1f));

        return entityBuilder(data)
                .type(PLAYER)
                .bbox(new HitBox(new Point2D(5,5), BoundingShape.circle(20)))
                .bbox(new HitBox(new Point2D(10,25),BoundingShape.box(10,17)))
                .with(physics)
                .with(new CollidableComponent())
                .with(new IrremovableComponent())
                .with(new PlayerComponent())
                .with(new HealthIntComponent(WARRIOR_HP))
                .build();
    }

    @Spawns("keyCode")
    public Entity newKeyCode(SpawnData data) {
        String key = data.get("key");

        KeyCode keyCode = KeyCode.getKeyCode(key);

        var lift = new LiftComponent();
        lift.setGoingUp(true);
        lift.yAxisDistanceDuration(6, Duration.seconds(0.76));

        var view = new KeyView(keyCode, Color.YELLOW, 24);
        view.setCache(true);
        view.setCacheHint(CacheHint.SCALE);

        return entityBuilder(data)
                .view(view)
                .with(lift)
                .zIndex(100)
                .build();
    }
    @Spawns("keyPrompt")
    public Entity newPrompt(SpawnData data) {
        return entityBuilder(data)
                .type(KEY_PROMPT)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }
    @Spawns("exitSign")
    public Entity newExit(SpawnData data) {
        return entityBuilder(data)
                .type(EXIT_SIGN)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }


//    @Spawns("button")
//    public Entity newButton(SpawnData data){
//        var keyEntity = getGameWorld().create("keyCode", new SpawnData(data.getX(), data.getY()-50).put("key","E"));
//        keyEntity.getViewComponent().setOpacity(0);
//
//        return entityBuilder(data)
//                .type(BUTTON)
//                .viewWithBBox(texture("button.png",20,10))
//                .with(new CollidableComponent(true))
//                .with("keyEntity",keyEntity)
//                .build();
//    }

}
