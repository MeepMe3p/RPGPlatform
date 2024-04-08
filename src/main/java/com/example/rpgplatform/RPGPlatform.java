package com.example.rpgplatform;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.multiplayer.ReplicationEvent;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.Server;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.example.rpgplatform.CharactersStuff.WarriorComponent;
import com.example.rpgplatform.Components.WarriorFactory;
import com.example.rpgplatform.MagicRoom.GameFactory;
import com.example.rpgplatform.MagicRoom.PlayerButtonHandler;
import com.example.rpgplatform.Scenes.MainLoadingScene;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.nio.channels.MulticastChannel;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;

public class RPGPlatform extends GameApplication {
    private static final int STARTING_LEVEL = 0;
    private static final int MAX_LEVEL = 3;

    boolean isServer;
    // Connects the Client and the Server
    private Connection<Bundle>connection;
    // ELIJAH CHANGES -------------------------------------------------//
    /*For the Input - for having a separate input handler(?) for the client
    * For the EventBus - in my understanding needed ni so that the client will have a separate way of communicating sa server side... for now wa pakoy idea the main purpose of this
    */

//    private Server<Bundle> server;
    private Input clientInput;
//    private EventBus clientBus;
    // ELIJAH CHANGES -------------------------------------------------//

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1280);
        gameSettings.setHeight(720);

        // To access the Multiplayer Functionalities
        gameSettings.addEngineService(MultiplayerService.class);

        gameSettings.setTitle("Flat Former Game");
        gameSettings.setApplicationMode(ApplicationMode.DEVELOPER);
        gameSettings.setDeveloperMenuEnabled(true);
        gameSettings.setSceneFactory(new SceneFactory(){
            @Override
            public LoadingScene newLoadingScene() {
                return new MainLoadingScene();
            }
        });
    }
    public static Entity player1, player2;

    // ADDED 04/04/2024 @direction && @range
    private int player1_direction;
    private int player2_direction;

    private final int range = 50;

    @Override
    protected void initInput(){
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                try{
                    player1.getComponent(WarriorComponent.class).left();
                    player1_direction = -range;
                } catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }

            @Override
            protected void onActionEnd() {
                try{
                player1.getComponent(WarriorComponent.class).stop();
                } catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                try{
                player1.getComponent(WarriorComponent.class).right();
                player1_direction = range;
                } catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }

            @Override
            protected void onActionEnd() {
                try{
                player1.getComponent(WarriorComponent.class).stop();
                } catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        }, KeyCode.D,VirtualButton.RIGHT);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onAction() {
                try{
                player1.getComponent(WarriorComponent.class).jump();
                } catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }

            @Override
            protected void onActionEnd() {
                try{
                player1.getComponent(WarriorComponent.class).stop();
                } catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        }, KeyCode.W);
            getInput().addAction(new UserAction("BasicAttack") {
                @Override
                protected void onAction() {
                    // ADDED FOR THE CIRCLE TO NOT ALWAYS SPAWN?
                    // TODO: Pull Kazuha @Elijah Sabay
                    try{
                    if(!player1.getComponent(WarriorComponent.class).isBasicAttacking()){
                            player1.getComponent(WarriorComponent.class).basicAttack();
                            runOnce(()->{
                                System.out.println("spawn");
                                spawn("WarriorBasic", player1.getX() + player1_direction, player1.getY());

                            }, Duration.seconds(0.5));
                        }
                    } catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }
                    }
                @Override
                protected void onActionEnd() {
                    try{
                    player1.getComponent(WarriorComponent.class).stop();
                    } catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }
                }
            },KeyCode.I);
        getInput().addAction(new UserAction("SkillAttack") {
            @Override
            protected void onAction() {
//                player1.getComponent(WarriorComponent.class).skill(player1,player1_direction);
                try{
                if(!player1.getComponent(WarriorComponent.class).isSkilling()){
                    player1.getComponent(WarriorComponent.class).skill(player1, player1_direction);
//                    runOnce(()->{
//                        System.out.println("spawn");
//                        a = spawn("WarriorBasic", player1.getX() + player1_direction, player1.getY());
//
//                    }, Duration.seconds(0.5));
                }
                } catch (IllegalArgumentException e){
                    e.printStackTrace();
                }


            }

            @Override
            protected void onActionEnd() {
                try{
                player1.getComponent(WarriorComponent.class).stopSkill();
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            }
            }
        }, KeyCode.P);

        clientInput = new Input();
        clientInput.addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player2.getComponent(WarriorComponent.class).left();
                player2_direction = -range;
            }

            @Override
            protected void onActionEnd() {
                player2.getComponent(WarriorComponent.class).stop();
            }
        }, KeyCode.A);

        clientInput.addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player2.getComponent(WarriorComponent.class).right();
                player2_direction = range;
            }

            @Override
            protected void onActionEnd() {
                player2.getComponent(WarriorComponent.class).stop();
            }
        }, KeyCode.D,VirtualButton.RIGHT);

        clientInput.addAction(new UserAction("Jump") {
            @Override
            protected void onAction() {
                player2.getComponent(WarriorComponent.class).jump();
            }

            @Override
            protected void onActionEnd() {
                player2.getComponent(WarriorComponent.class).stop();
            }
        }, KeyCode.W);
        clientInput.addAction(new UserAction("BasicAttack") {
            @Override
            protected void onAction() {
                if(!player2.getComponent(WarriorComponent.class).isBasicAttacking()){
                    player2.getComponent(WarriorComponent.class).basicAttack();
                    runOnce(()->{
                        System.out.println("spawn");
                        spawn("WarriorBasic", player2.getX() + player2_direction, player2.getY());

                    }, Duration.seconds(0.5));
                }
            }
            @Override
            protected void onActionEnd() {
                player2.getComponent(WarriorComponent.class).stop();
            }
        },KeyCode.I);
        clientInput.addAction(new UserAction("SkillAttack") {
            @Override
            protected void onAction() {
                if(!player2.getComponent(WarriorComponent.class).isSkilling()){
                    player2.getComponent(WarriorComponent.class).skill(player2,player2_direction);
                }
            }

            @Override
            protected void onActionEnd() {
                player2.getComponent(WarriorComponent.class).stopSkill();
            }
        }, KeyCode.P);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", STARTING_LEVEL);
        vars.put("levelTime",0.0);
        vars.put("score",0);
    }
    @Override
    protected void onPreInit() {
        FXGL.getSettings().setGlobalMusicVolume(0.00);
        loopBGM("BGM_dash_runner.wav");
    }


    @Override
    protected void initGame() {
//        clientBus = new EventBus();

        runOnce(()->{
           getDialogService().showConfirmationBox("Are you the host? ", YES -> {
            addEntityFactories();
            setLevelFromMap("tmx/level1.tmx");
            isServer = YES;
               if(isServer){
                   var server = getNetService().newTCPServer(55555);

                   server.setOnConnected(connection -> {
                       this.connection = connection;

                       getExecutor().startAsyncFX(()->{
                           OnServer();
                       });

                   });
                   server.startAsync();
               } else {

                   var client = getNetService().newTCPClient("localhost", 55555);

                   client.setOnConnected(connection -> {
                       this.connection = connection;

                       getExecutor().startAsyncFX(()->{
                           OnClient();

                       });

                   });
                   client.connectAsync();
               }
           });
        }, Duration.seconds(.5));
    }

    /**
     * Registers entity factories into the game world.
     * This method adds instances of entity factories to the game world,
     * allowing for the creation of various types of entities within the game environment.
     * Call this method to initialize entity factories required for creating game objects.
     */
    private void addEntityFactories() {
        // This method should be in both Client and Server
        getGameWorld().addEntityFactory(new GameFactory());
        getGameWorld().addEntityFactory(new WarriorFactory());
        // TODO: Add more .....
    }
    private void OnClient() {
        player1 = new Entity();

        getService(MultiplayerService.class).addEntityReplicationReceiver(connection, getGameWorld());
        getService(MultiplayerService.class).addPropertyReplicationReceiver(connection, getWorldProperties());
        getService(MultiplayerService.class).addInputReplicationSender(connection,getInput());

        // TODO: ADD CONDITION SINCE THIS CAUSES LAG!
        // TODO: MAYBE ADD THREAD?
//        clientBus.addEventHandler(CustomReplicationEvent.CUSTOM_EVENT, event -> {
//            getNotificationService().pushNotification(event.data + ": " + event.value);
//        });
//        getService(MultiplayerService.class).addEventReplicationReceiver(connection, clientBus);
        // TODO: OR DELETE SINCE IT DOES NOTHING!
    }
    private void OnServer() {
        spawnHandler();
        Viewport viewport1 = getGameScene().getViewport();
        viewport1.setBounds(-1500,0,250*7,getAppHeight());
        viewport1.bindToEntity(player1,getAppWidth()/2,getAppHeight()/2);
        viewport1.setLazy(true);

        getService(MultiplayerService.class).addPropertyReplicationSender(connection,getWorldProperties());
        getService(MultiplayerService.class).addInputReplicationReceiver(connection,clientInput);

        // TODO: ADD CONDITION SINCE THIS CAUSES LAG!
        // TODO: MAYBE ADD THREAD?
//        getService(MultiplayerService.class).addEventReplicationSender(connection,clientBus);
    }

    /** For every "Spawn", client should also have it, provided that @GameFactory the entity contains a ".with(new NetworkComponent())" **/
    private void spawnHandler() {
        setLevel();
        player2 = spawn("player",550,50);
        getService(MultiplayerService.class).spawn(connection, player2, "player");
        player1 = spawn("player", 50,50);
        getService(MultiplayerService.class).spawn(connection, player1, "player");
        var background = spawn("background");
        getService(MultiplayerService.class).spawn(connection, background, "background");
        // TODO: Add more .....
    }
    /** Collisions should be handled by the server **/
    private void collisionHandler() {
        getPhysicsWorld().setGravity(0,760);
        getPhysicsWorld().addCollisionHandler(new PlayerButtonHandler());
    }
    @Override
    protected void initPhysics() {
        collisionHandler();
    }
    @Override
    protected void initUI() {
        if(isMobile()){
            var dpadView = getInput().createVirtualDpadView();
            var buttonsView = getInput().createXboxVirtualControllerView();

            addUINode(dpadView,0,getAppHeight()-290);
            addUINode(buttonsView,getAppWidth()-280,getAppHeight()-290);
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        if (isServer || clientInput != null) {
            clientInput.update(tpf);
        }
        if(player1 != null && player2 != null){
            if(player1.getY()> getAppHeight() ){
                onPlayerDied();
            }
            if(player2.getY() > getAppHeight()){
                respawnEnemy();
            }
        }
    }
    public void onPlayerDied(){
        if(player1 != null){
            player1.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50,50));
            player1.setZIndex(Integer.MAX_VALUE);
        }
    }
    public void respawnEnemy(){
        if(player2 != null){
            player2.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(550,50));
            player2.setZIndex(Integer.MAX_VALUE);
        }
    }
    private void setLevel(){

        if(player1 != null && player2 != null){
            player1.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50,50));
            player1.setZIndex(Integer.MAX_VALUE);

            player2.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50,50));
            player2.setZIndex(Integer.MAX_VALUE);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Entity getPlayer2() {
        return player2;
    }

    public static void setPlayer2(Entity player2) {
        RPGPlatform.player2 = player2;
    }

    public static Entity getPlayer1() {
        return player1;
    }
//    // ELIJAH CHANGES -------------------------------------------------//
//    public static class CustomReplicationEvent extends ReplicationEvent{
//        public static final EventType<CustomReplicationEvent> CUSTOM_EVENT = new EventType<>(ReplicationEvent.ANY,"CUSTOM_EVENT");
//        public String data;
//        public double value;
//
//        public CustomReplicationEvent(String data, double value) {
//            super(CUSTOM_EVENT);
//            this.data = data;
//            this.value = value;
//        }
//    }
//    // ELIJAH CHANGES -------------------------------------------------//

}
