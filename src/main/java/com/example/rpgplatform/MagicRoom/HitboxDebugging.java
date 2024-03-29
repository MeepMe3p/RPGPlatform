package com.example.rpgplatform.MagicRoom;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.collision.shapes.Shape;
import com.almasb.fxgl.physics.box2d.collision.shapes.ShapeType;
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
//import javafx.scene.shape.Shape;


public class HitboxDebugging extends Component {
    private Rectangle hitbox;
    private final Color hitbox_color = Color.RED;

    @Override
    public void onAdded() {
        Entity entity1 = getEntity();
        PhysicsComponent physics = entity1.getComponent(PhysicsComponent.class);
        if(physics!= null){
            for(Fixture fixture: physics.getBody().getFixtures()){
                Shape shape =  fixture.getShape();
                if(shape.getType() == ShapeType.CIRCLE){
//                    Circle circle = (Circle) shape;
                }
            }
        }
//        updateDebuRect();
//
//        entity.getWorld().addGameView()
    }

}
