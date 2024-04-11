package com.example.rpgplatform;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.example.rpgplatform.CharactersStuff.WarriorComponent;
import com.example.rpgplatform.Components.WarriorFactory;
import com.example.rpgplatform.Constants.Constant;
import com.example.rpgplatform.MagicRoom.GameFactory;
import com.example.rpgplatform.MagicRoom.PlayerButtonHandler;
import com.example.rpgplatform.Network.GameClient;
import com.example.rpgplatform.Network.GameServer;
import com.example.rpgplatform.Network.PlayerMP;
import com.example.rpgplatform.Network.packets.Packet00Login;
import com.example.rpgplatform.Scenes.GameMenu;
import com.example.rpgplatform.Scenes.MainMenu;
import javafx.geometry.Point2D;
import javafx.scene.control.TextInputDialog;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;

public class RPGPlatform extends GameApplication {
    private static final String STARTING_LEVEL = "Oi Pangkita-a ko";
    private static RPGPlatform instance;
    // Multiplayer
    private boolean isMultiplayer;
    private GameServer socketServer;
    private GameClient socketClient;
    private PlayerMP playerMP; // TODO: FIND USAGE @REFERENCE BOMBERMAN

    InputHandler gameInput;
    // Entities related
    private Entity player;
    private WarriorComponent warriorComponent;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        instance = this;
        isMultiplayer = false;
        gameSettings.setWidth(1080);
        gameSettings.setHeight(720);
        gameSettings.setTitle(Constant.GAME_TITLE);
        gameSettings.setApplicationMode(ApplicationMode.DEVELOPER);
        gameSettings.setDeveloperMenuEnabled(true);
        gameSettings.setIntroEnabled(false);
        gameSettings.setGameMenuEnabled(true);
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setSceneFactory(new SceneFactory(){
//            @Override
//            public LoadingScene newLoadingScene() {
//                return new MainLoadingScene();
//            }
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }

            @Override
            public FXGLMenu newGameMenu() {
                return new GameMenu();
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
        addEntityFactories();
        if (isMultiplayer) {
            socketClient = new GameClient(this, "localhost");
            socketClient.start();

            playerMP = new PlayerMP(50,50, // to set player's Position
                    new TextInputDialog("Please enter a username").showAndWait().orElse(null),
                    null,-1);
            player = playerMP.getEntity();
            warriorComponent = player.getComponent(WarriorComponent.class);
            setLevelFromMap("tmx/platformer_tiled.tmx");
            getGameWorld().addEntity(player);
            Packet00Login loginPacket = new Packet00Login(playerMP.getUsername(), player.getX(), player.getY());
            if (socketServer != null) {
                socketServer.addConnection(playerMP, loginPacket);
            }
            loginPacket.writeData(socketClient);
        } else {
            setLevelFromMap("tmx/level1.tmx");
            spawnHandler();
            Viewport viewport1 = getGameScene().getViewport();
            viewport1.setBounds(-1500,0,250*7,getAppHeight());
            viewport1.bindToEntity(player,getAppWidth()/2,getAppHeight()/2);
            viewport1.setLazy(true);
        }
        initInputPlayer();
    }

    /**
     * Registers entity factories into the game world.
     * This method adds instances of entity factories to the game world,
     * allowing for the creation of various types of entities within the game environment.
     * Call this method to initialize entity factories required for creating game objects.
     */
    protected void initInputPlayer() {
//        isMultiplayer
//        socketClient
//        warriorComponent
        getExecutor().startAsync(()->{
            // TODO: LATER BUILDER PATTERN!
            gameInput = new InputHandler(player, getInput(), isMultiplayer, socketClient, warriorComponent);
            gameInput.run();
        });
    }
    private void addEntityFactories() {
        getGameWorld().addEntityFactory(new GameFactory());
        getGameWorld().addEntityFactory(new WarriorFactory());
        // TODO: Add more .....
    }
    /** For every "Spawn", client should also have it, provided that @GameFactory the entity contains a ".with(new NetworkComponent())" **/
    private void spawnHandler() {
        setLevel();
        player = spawn("player");
        var background = spawn("background");
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
        if(player != null){
            if(player.getY()> getAppHeight() ){
                onPlayerDied();
            }
//            if(player2.getY() > getAppHeight()){
//                respawnEnemy();
//            }
        }
    }
    public void onPlayerDied(){
        if(player != null){
            player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50,50));
            player.setZIndex(Integer.MAX_VALUE);
        }
    }
//    public void respawnEnemy(){
//        if(player2 != null){
//            player2.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(550,50));
//            player2.setZIndex(Integer.MAX_VALUE);
//        }
//    }
    private void setLevel(){
        if(player != null){
            player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50,50));
            player.setZIndex(Integer.MAX_VALUE);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    public void setNewSocketServer() {
        this.socketServer = new GameServer(this);
        socketServer.start();
    }

    public void setMultiplayer(boolean multiplayer) {
        isMultiplayer = multiplayer;
    }

    public static RPGPlatform getInstance() {
        return instance;
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

//    public void addPlayerMP(Entity p) {
//        Platform.runLater(() -> {
//            getGameWorld().addEntity(p);
////            enemies.add(p);
//            System.out.println("Spawn enemy!");
//            if (p.getComponent(WarriorComponent.class).getUsername().compareTo(playerComponent.getUsername()) > 0) {
//                p.getComponent(WarriorComponent.class).setPos(0, 0, 18*48, 11*48);
//            } else {
//                playerComponent.setPos(0,0,18*48, 11*48);
//                set("defaultX", 18*48);
//                set("defaultY", 11*48);
//            }
//        });
//    }
}
