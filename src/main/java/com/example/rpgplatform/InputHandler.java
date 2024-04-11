package com.example.rpgplatform;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.example.rpgplatform.CharactersStuff.WarriorComponent;
import com.example.rpgplatform.Network.GameClient;
import com.example.rpgplatform.Network.packets.Packet02Move;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.runOnce;
import static com.almasb.fxgl.dsl.FXGL.spawn;

public class InputHandler implements Runnable{
    Input input;
    Entity player;
    int playerDirection;
    private final int range = 50;
    boolean isMultiplayer;
    GameClient socketClient;
    WarriorComponent warriorComponent;

    public InputHandler(Entity player, Input input,
                        boolean isMultiplayer,
                        GameClient socketClient,
                        WarriorComponent warriorComponent) {
        this.input = input;
        this.player = player;
        this.isMultiplayer = isMultiplayer;
        this.socketClient = socketClient;
        this.warriorComponent = warriorComponent;
    }

    public Input getInput() {
        return input;
    }

    @Override
    public void run() {
        input.addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                try{
                    player.getComponent(WarriorComponent.class).left();
                    playerDirection = -range;
                    if (isMultiplayer) {
                        // TODO: UNDERSTAND THE STATE!
//                        Packet02Move packet = new Packet02Move(warriorComponent.getUsername(),
//                                warriorComponent.getPhysics().getVelocityX(), warriorComponent.getPhysics().getVelocityY(),
//                                warriorComponent.getState().getValue(),
//                                player.getX(), player.getY());
//                        packet.writeData(socketClient);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            protected void onActionEnd() {
                try{
                    player.getComponent(WarriorComponent.class).stop();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, KeyCode.A);
        input.addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                try {
                    player.getComponent(WarriorComponent.class).right();
                    playerDirection = range;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            protected void onActionEnd() {
                try{
                    player.getComponent(WarriorComponent.class).stop();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, KeyCode.D, VirtualButton.RIGHT);
        input.addAction(new UserAction("Jump") {
            @Override
            protected void onAction() {
                try{
                    player.getComponent(WarriorComponent.class).jump();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            protected void onActionEnd() {
                try{
                    player.getComponent(WarriorComponent.class).stop();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, KeyCode.W);
        input.addAction(new UserAction("BasicAttack") {
            @Override
            protected void onAction() {
                try{
                    if(!player.getComponent(WarriorComponent.class).isBasicAttacking()){
                        player.getComponent(WarriorComponent.class).basicAttack();
                        runOnce(()->{
                            System.out.println("spawn");
                            spawn("WarriorBasic", player.getX() + playerDirection, player.getY());

                        }, Duration.seconds(0.5));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            protected void onActionEnd() {
                try{
                    player.getComponent(WarriorComponent.class).stop();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        },KeyCode.I);
        input.addAction(new UserAction("SkillAttack") {
            @Override
            protected void onAction() {
                try{
                    if(!player.getComponent(WarriorComponent.class).isSkilling()){
                        player.getComponent(WarriorComponent.class).skill(player,playerDirection);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            protected void onActionEnd() {
                try{
                    player.getComponent(WarriorComponent.class).stopSkill();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, KeyCode.P);
    }
}
