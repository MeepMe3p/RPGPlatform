package com.example.rpgplatform.CharactersStuff;

import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.Timer;
//import com.example.rpgplatform.Components.HitboxComponent;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.image;

public class WarriorComponent extends Component {
    private final PhysicsComponent physics;
    private Entity warriorSkill;
    private AnimatedTexture texture;
    private AnimationChannel idle, walk,jump,attack, skill_atk;
    private boolean isJumping, isBasicAttacking, isSkilling;

    private int jumps = 2;
    private int skill = 2;
    String username;

    public WarriorComponent(String username){
        this();
        this.username = username; // TODO: USERNAME USAGE
    }
    public WarriorComponent(){
        physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR",new Point2D(16,100), BoundingShape.box(6,17)));
        physics.setFixtureDef(new FixtureDef().friction(0.1f));

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
        entity.addComponent(physics);

        physics.onGroundProperty().addListener((obs, old, isOnGround)->{
            if(isOnGround){
                jumps = 2;
                skill = 2;
            }
        });
    }
    private Timer t = new Timer();

    private double now = t.getNow();

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
        now++;
        if (now == 500) {
            numSkill = 0;
            now = 0;
        }

        if (isBasicAttacking) {
            if (texture.getAnimationChannel() != attack) {
                texture.loopAnimationChannel(attack);
                // ADDED: Fixed when holding the attack button it always "resets"
                getGameTimer().runOnceAfter(() -> {
                    setBasicAttacking(false);
                }, Duration.seconds(2));
            }
        } else if (isSkilling) {
            if (texture.getAnimationChannel() != skill_atk) {
                texture.loopAnimationChannel(skill_atk);
                getGameTimer().runOnceAfter(() -> {
                    setSkilling(false);
                }, Duration.seconds(5));
            }
        } else if (isJumping) {
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
    private int numSkill = 0;
    private final int maxSkill = 1;
    public void skill(Entity player, int direction){
        runOnce(() -> {
            physics.setVelocityY(-200);
            isSkilling = true;
            warriorSkill = spawn("WarriorSkill", getEntity().getX() + direction, getEntity().getY());
            warriorSkill.addComponent(new ProjectileComponent(new Point2D(1+direction,0),400));
        }, Duration.seconds(1));
        numSkill++;
    }
    public void stopSkill(){
        isSkilling = false;

    }
    //FOR ATTACK

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
//        System.out.println(getEntity().getAnchoredPosition());


        physics.setVelocityX(170);

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

    public void setBasicAttacking(boolean basicAttacking) {
        isBasicAttacking = basicAttacking;
    }

    public boolean isBasicAttacking() {
        return isBasicAttacking;
    }
    public boolean isSkilling(){
        return isSkilling;
    }
    public void setSkilling(boolean skilling){
        isSkilling = skilling;
    }

    public String getUsername() {
        return username;
    }

    public PhysicsComponent getPhysics() {
        return physics;
    }
}
