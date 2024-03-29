package com.example.rpgplatform.Components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.image;

public class PlayerComponent extends Component {
    private PhysicsComponent physics;
    private AnimatedTexture texture;
    private AnimationChannel idle, walk,jump,attack;
    private boolean isJumping, isBasicAttacking;

    private int jumps = 2;

    public PlayerComponent(){
        Image idle_image = image("WarriorIdleAnim.png");
        Image jump_anim = image("WarriorJump.png");
        Image walk_anim = image("WalkingAnimation.png");
        Image atk_anim = image("WarriorBasicAtk.png");
        idle = new AnimationChannel(idle_image,8,64,64, Duration.seconds(1),1,7);
        walk = new AnimationChannel(walk_anim,6,56,56, Duration.seconds(0.66),1,5);
        jump = new AnimationChannel(jump_anim,12,74,74, Duration.seconds(1),1,11);
        attack = new AnimationChannel(atk_anim,5,84,84,Duration.seconds(1),1,4);

        texture = new AnimatedTexture(idle);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16,21));
        entity.getViewComponent().addChild(texture);

        physics.onGroundProperty().addListener((obs, old, isOnGround)->{
            if(isOnGround){
                jumps = 2;
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if(isBasicAttacking){
            if(texture.getAnimationChannel() != attack){
//                System.out.println("attal");
                texture.loopAnimationChannel(attack);

            }
        }
        else if (isJumping) {
//            System.out.println("sadsad");
            if (texture.getAnimationChannel() != jump) {
                texture.loopAnimationChannel(jump);
            }
        } else {
            if (physics.isMovingX()) {
                if (texture.getAnimationChannel() != walk) {
                    texture.loopAnimationChannel(walk);
                }
            } else {
                if (texture.getAnimationChannel() != idle) {
                    texture.loopAnimationChannel(idle);
                }
            }
        }
    }
    //FOR ATTACK
    public void basicAttack(){
        isBasicAttacking = true;
    }
    //FOR MOVEMENT
    public void left(){
        getEntity().setScaleX(1);
        physics.setVelocityX(170);

    }
    public void right(){
        getEntity().setScaleX(-1);
        physics.setVelocityX(-170);

    }
    public void stop(){
        physics.setVelocityX(0);
        isJumping = false;
        isBasicAttacking = false;

    }
    public void jump(){
        if(jumps == 0){
            return;
        }
        physics.setVelocityY(-500);
        jumps--;
        isJumping = true;
    }
}
