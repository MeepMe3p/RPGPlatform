package com.example.rpgplatform;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
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

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1280);
        gameSettings.setHeight(720);
        gameSettings.setTitle("FUCK THIS SHIT");
        gameSettings.setApplicationMode(ApplicationMode.DEVELOPER);
        gameSettings.setDeveloperMenuEnabled(true);
        gameSettings.setSceneFactory(new SceneFactory(){

            @Override
            public LoadingScene newLoadingScene() {
                return new MainLoadingScene();
            }
        });
    }
    public static Entity player, player2;

    Entity a;
    @Override
    protected void initInput(){
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(WarriorComponent.class).left();
//                player.reset()
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(WarriorComponent.class).stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(WarriorComponent.class).right();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(WarriorComponent.class).stop();
            }
        }, KeyCode.D,VirtualButton.RIGHT);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onAction() {
                player.getComponent(WarriorComponent.class).jump();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(WarriorComponent.class).stop();
            }
        }, KeyCode.W);
        getInput().addAction(new UserAction("BasicAttack") {
            @Override
            protected void onAction() {
                //TODO NAA DIRIIII STUDYHON SANI NAKO IDK WTF HOW DIS WORKS PERO FEEL NAKO DI NI MUWORK MAGKADUGAYAN

                player.getComponent(WarriorComponent.class).basicAttack();
                runOnce(()->{
                    System.out.println("spawn");
                    a = spawn("WarriorBasic", player.getX(), player.getY());

                }, Duration.seconds(0.5));

//                runOnce(()->{
//                    System.out.println("despawn");
//                    a.removeFromWorld();
//
//                }, Duration.seconds(2));

            }


            @Override
            protected void onActionEnd() {
                player.getComponent(WarriorComponent.class).stop();
//                a.removeFromWorld();

//                g

            }
        },KeyCode.I);
        getInput().addAction(new UserAction("SkillAttack") {
            @Override
            protected void onAction() {
                player.getComponent(WarriorComponent.class).skill();

                player.getComponent(WarriorComponent.class).basicAttack();
                runOnce(()->{
                    System.out.println("spawn");
                    a = spawn("WarriorSkill", player.getX(), player.getY());

                }, Duration.seconds(1));

            }

            @Override
            protected void onActionEnd() {
                player.getComponent(WarriorComponent.class).stop();
//                a.removeFromWorld();
            }
        }, KeyCode.P);
    }
    public void spawnBasicAttack(){
        System.out.println("spawned");
        spawn("WarriorBasic",player.getX(),50);
    }
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", STARTING_LEVEL);
        vars.put("levelTime",0.0);
        vars.put("score",0);
    }
    @Override
    protected void onPreInit() {
        FXGL.getSettings().setGlobalMusicVolume(0.25);
        loopBGM("BGM_dash_runner.wav");
    }


    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameFactory());
        getGameWorld().addEntityFactory(new WarriorFactory());

        player = null;
        player2 = null;
        nextLevel();
        player2 = spawn("player",550,50);
        player = spawn("player", 50,50);

        set("player",player);
        spawn("background");

        Viewport viewport = getGameScene().getViewport();
        viewport.setBounds(-1500,0,250*7,getAppHeight());
        viewport.bindToEntity(player,getAppWidth()/2,getAppHeight()/2);

        viewport.setLazy(true);
    }
    private void nextLevel() {
        if (geti("level") == MAX_LEVEL) {
            showMessage("You finished the demo!");
            return;
        }

        inc("level", +1);

        setLevel(geti("level"));
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0,760);
        getPhysicsWorld().addCollisionHandler(new PlayerButtonHandler());
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
        if(player.getY()> getAppHeight() ){
            onPlayerDied();
//            setLevel(2);
        }
        if(player2.getY() > getAppHeight()){
            respawnEnemy();
        }

    }
    public void onPlayerDied(){
//        setLevel(geti("level"));
        if(player != null){
            player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50,50));
            player.setZIndex(Integer.MAX_VALUE);
        }
    }
    public void respawnEnemy(){
        if(player2 != null){
            player2.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(550,50));
            player2.setZIndex(Integer.MAX_VALUE);
        }
    }
    private void setLevel(int levelNum){

        if(player != null){
            player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50,50));
            player.setZIndex(Integer.MAX_VALUE);
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
}
