package com.example.rpgplatform.CharactersStuff;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.example.rpgplatform.Components.HitboxComponent;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.image;
import static com.example.rpgplatform.RPGPlatform.player;

public class WarriorComponent extends Component {
    private PhysicsComponent physics;
    private AnimatedTexture texture;
    private AnimationChannel idle, walk,jump,attack, skill_atk;
    private boolean isJumping, isBasicAttacking, isSkill;

    private int jumps = 2;
    private int skill = 2;
//    Entity player;



    public WarriorComponent(/*Entity player*/){
//        TODO: YOU ARE MY SUNSHINE MY ONLY SUNSHINE change ett so all animations have the same height and width per frame
        Image idle_image = image("WarriorIdle.png");
        Image jump_anim = image("WarriorHigh_Jump.png");
        Image walk_anim = image("WarriorWalk.png");
        Image atk_anim = image("WarriorAttack.png");
        Image skill_anim = image("WarriorSpecialAttack.png");

        idle = new AnimationChannel(idle_image,12,128,128, Duration.seconds(1),1,11);
        jump = new AnimationChannel(jump_anim,12,128,128, Duration.seconds(1),1,11);
        walk = new AnimationChannel(walk_anim,6,128,128, Duration.seconds(1),1,5);
        attack = new AnimationChannel(atk_anim,5,128,128,Duration.seconds(1),1,4);
        skill_atk = new AnimationChannel(skill_anim, 8,128,128,Duration.seconds(1),1,7);
        texture = new AnimatedTexture(idle);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(48,64));

        entity.getViewComponent().addChild(texture);

        physics.onGroundProperty().addListener((obs, old, isOnGround)->{
            if(isOnGround){
                jumps = 2;
                skill = 2;
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if(isBasicAttacking){
            if(texture.getAnimationChannel() != attack){
                texture.loopAnimationChannel(attack);
                // ADDED: Fixed when holding the attack button it always "resets"
                // TODO: Find a better way or @Elijah Pull Kazuha?
                getGameTimer().runOnceAfter(() -> {
                    setBasicAttacking(false);
                }, Duration.seconds(2));
            }
        }
        else if(isSkill){
            if(texture.getAnimationChannel() != skill_atk){
                texture.loopAnimationChannel(skill_atk);
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
    public void skill(){
        if(skill == 0){
            return;
        }
        physics.setVelocityY(-500);

        skill--;
        isSkill = true;
    }
    //FOR ATTACK
    HitBox attackHitbox;
    Entity e;
    public void basicAttack(){
        isBasicAttacking = true;
    }
    //FOR MOVEMENT
    public void left(){


        getEntity().setScaleX(-1);
//        direction = false;
        physics.setVelocityX(-170);

    }
    public void right(){
        getEntity().setScaleX(1);
//        direction = true;
        System.out.println(getEntity().getAnchoredPosition());


        physics.setVelocityX(170);

    }
    public void stop(){
        physics.setVelocityX(0);
        isJumping = false;
        isBasicAttacking = false;
        isSkill = false;


    }
    public void jump(){
        if(jumps == 0){
            return;
        }
        physics.setVelocityY(-500);
        jumps--;
        isJumping = true;
    }

    public void setBasicAttacking(boolean basicAttacking) {
        isBasicAttacking = basicAttacking;
    }

    public boolean isBasicAttacking() {
        return isBasicAttacking;
    }
}
