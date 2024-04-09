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
    InputHandler clientInput;
    InputHandler serverInput;
//    private EventBus clientBus;
    // ELIJAH CHANGES -------------------------------------------------//
    public static Entity player1, player2;

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
        // TODO: ADD EVENTBUS();
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
//                getInput().setProcessInput(false);
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
//        player1 = new Entity();

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
        initInputPlayer();
        getService(MultiplayerService.class).addPropertyReplicationSender(connection,getWorldProperties());
        getService(MultiplayerService.class).addInputReplicationReceiver(connection, clientInput.getInput());
        Viewport viewport1 = getGameScene().getViewport();
        viewport1.setBounds(-1500,0,250*7,getAppHeight());
        viewport1.bindToEntity(player1,getAppWidth()/2,getAppHeight()/2);
        viewport1.setLazy(true);


        // TODO: ADD CONDITION SINCE THIS CAUSES LAG!
        // TODO: MAYBE ADD THREAD?
//        getService(MultiplayerService.class).addEventReplicationSender(connection,clientBus);
    }

    private void initInputPlayer() {
        getExecutor().startAsyncFX(()->{
            clientInput = new InputHandler(player2, new Input());
            Thread clientInputThread = new Thread(clientInput);
            clientInputThread.start();
            serverInput = new InputHandler(player1, getInput());
            Thread serverInputThread = new Thread(serverInput);
            serverInputThread.start();
        });
    }

    /** For every "Spawn", client should also have it, provided that @GameFactory the entity contains a ".with(new NetworkComponent())" **/
    private void spawnHandler() {
//        player1 = null;
//        player2 = null;
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
        if (isServer && clientInput != null && clientInput.getInput() != null) {
            try{
                clientInput.getInput().update(tpf);
            } catch (Exception e){
                e.printStackTrace();
            }
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


    // TODO: ADD EVENTBUS();
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
