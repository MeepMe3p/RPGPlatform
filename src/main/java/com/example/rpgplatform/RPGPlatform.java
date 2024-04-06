package com.example.rpgplatform;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.example.rpgplatform.CharactersStuff.WarriorComponent;
import com.example.rpgplatform.Components.WarriorFactory;
import com.example.rpgplatform.MagicRoom.GameFactory;
import com.example.rpgplatform.MagicRoom.PlayerButtonHandler;
import com.example.rpgplatform.Scenes.MainLoadingScene;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;

public class RPGPlatform extends GameApplication {
    private static final int STARTING_LEVEL = 0;
    private static final int MAX_LEVEL = 3;

    // Connects the Client and the Server
    private Connection<Bundle>connection;

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
    private int direction;
    private final int range = 50;

    Entity a;
    @Override
    protected void initInput(){
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player1.getComponent(WarriorComponent.class).left();
                direction = -range;
//                player.setX(player.getX()+50);
//                player.reset()
            }

            @Override
            protected void onActionEnd() {
                player1.getComponent(WarriorComponent.class).stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player1.getComponent(WarriorComponent.class).right();
                direction = range;
            }

            @Override
            protected void onActionEnd() {
                player1.getComponent(WarriorComponent.class).stop();
            }
        }, KeyCode.D,VirtualButton.RIGHT);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onAction() {
                player1.getComponent(WarriorComponent.class).jump();
            }

            @Override
            protected void onActionEnd() {
                player1.getComponent(WarriorComponent.class).stop();
            }
        }, KeyCode.W);
            getInput().addAction(new UserAction("BasicAttack") {
                @Override
                protected void onAction() {
                    // ADDED FOR THE CIRCLE TO NOT ALWAYS SPAWN?
                    // TODO: Pull Kazuha @Elijah Sabay
                    if(!player1.getComponent(WarriorComponent.class).isBasicAttacking()){
                            player1.getComponent(WarriorComponent.class).basicAttack();
                            runOnce(()->{
                                System.out.println("spawn");
                                a = spawn("WarriorBasic", player1.getX() + direction, player1.getY());

                            }, Duration.seconds(0.5));
                        }
                    }
                @Override
                protected void onActionEnd() {
                    player1.getComponent(WarriorComponent.class).stop();
                }
            },KeyCode.I);
        getInput().addAction(new UserAction("SkillAttack") {
            @Override
            protected void onAction() {
                player1.getComponent(WarriorComponent.class).skill(direction);


            }

            @Override
            protected void onActionEnd() {
                player1.getComponent(WarriorComponent.class).stopSkill();
            }
        }, KeyCode.P);
    }
    public void spawnBasicAttack(){
        System.out.println("spawned");
        spawn("WarriorBasic", player1.getX(),50);
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
        runOnce(()->{
           getDialogService().showConfirmationBox("Are you the host? ", YES -> {
            addEntityFactories();
               if(YES){
                   var server = getNetService().newUDPServer(55555);
                   server.setOnConnected(connection -> {
                       this.connection = connection;

                       getExecutor().startAsyncFX(()->{
                           OnServer();
                       });

                   });
                   server.startAsync();
               } else {
                   var client = getNetService().newUDPClient("localhost", 55555);
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
        getService(MultiplayerService.class).addEntityReplicationReceiver(connection, getGameWorld());
        getService(MultiplayerService.class).addPropertyReplicationReceiver(connection, getWorldProperties());

        if(player2 != null){
            Viewport viewport2 = getGameScene().getViewport();
            viewport2.setBounds(-1500,0,250*7,getAppHeight());
            viewport2.bindToEntity(player2,getAppWidth()/2,getAppHeight()/2);
            viewport2.setLazy(true);
        }
    }
    private void OnServer() {
        // Setting the UPD Server with port of 55555
        getNetService().newUDPServer(55555);

        spawnHandler();

        Viewport viewport1 = getGameScene().getViewport();
        viewport1.setBounds(-1500,0,250*7,getAppHeight());
        viewport1.bindToEntity(player1,getAppWidth()/2,getAppHeight()/2);
        viewport1.setLazy(true);

        collisionHandler();
    }

    /** For every "Spawn", client should also have it, provided that @GameFactory the entity contains a ".with(new NetworkComponent())" **/
    private void spawnHandler() {
        player1 = null;
        player2 = null;
        nextLevel();
        player2 = spawn("player",550,50);
        // Spawns the "player" to the client
        getService(MultiplayerService.class).spawn(connection, player2, "player");

        player1 = spawn("player", 50,50);
        // Spawns the "player" to the client
        getService(MultiplayerService.class).spawn(connection, player1, "player");

        set("player", player1);
        set("player", player2);

        var background = spawn("background");
        // Spawns the "background" to the client
        getService(MultiplayerService.class).spawn(connection, background, "background");
        // TODO: Add more .....
    }
    /** Collisions should be handled by the server **/
    private void collisionHandler() {
        getPhysicsWorld().setGravity(0,760);
        getPhysicsWorld().addCollisionHandler(new PlayerButtonHandler());
    }

    private void nextLevel() {
//        if (geti("level") == MAX_LEVEL) {
//            showMessage("You finished the demo!");
//            return;
//        }
//
//        inc("level", +1);

        setLevel(geti("level"));
    }

    @Override
    protected void initPhysics() {
//        Transferred to the @OnServer() method
//        getPhysicsWorld().setGravity(0,760);
//        getPhysicsWorld().addCollisionHandler(new PlayerButtonHandler());
//        onCollisionBegin(PLAYER,);
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
        inc("levelTime",tpf);
        if(player1 != null && player2 != null){
            if(player1.getY()> getAppHeight() ){
                onPlayerDied();
    //            setLevel(2);
            }
            if(player2.getY() > getAppHeight()){
                respawnEnemy();
            }
        }
    }
    public void onPlayerDied(){
//        setLevel(geti("level"));
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
    private void setLevel(int levelNum){

        if(player1 != null && player2 != null){
            player1.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50,50));
            player1.setZIndex(Integer.MAX_VALUE);

            player2.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50,50));
            player2.setZIndex(Integer.MAX_VALUE);
        }


        set("levelTime", 0.0);
//        Level level = setLevelFromMap("/assets/levels/tmx"+levelNum+".tmx");
        Level level = setLevelFromMap("tmx/level1.tmx");


        var shortestTime = level.getProperties().getDouble("star1time");
//        var levelTimeData = new LevelEndScene.LevelTimeData(shortestTime*2.4,shortestTime*1.3,shortestTime);
//        set("levelTimeData",levelTimeData);
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

    public static void setPlayer1(Entity player1) {
        RPGPlatform.player1 = player1;
    }
}
