package com.geekbrains.rpg.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.geekbrains.rpg.game.GeekRpgGame;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class ScreenManager {
    public enum ScreenType {
        MENU, GAME
    }

    public static final int WORLD_WIDTH = 1280;
    public static final int HALF_WORLD_WIDTH = WORLD_WIDTH / 2;
    public static final int WORLD_HEIGHT = 720;
    public static final int HALF_WORLD_HEIGHT = WORLD_HEIGHT / 2;

    private GeekRpgGame game;
    private SpriteBatch batch;
    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private Screen targetScreen;
//    private Viewport viewport;
//    private Camera camera;

    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

//    public Viewport getViewport() {
//        return viewport;
//    }
//
//    public Camera getCamera() {
//        return camera;
//    }

    public static int getWorldWidth() {
        return WORLD_WIDTH;
    }

    public static int getWorldHeight() {
        return WORLD_HEIGHT;
    }

    private ScreenManager() {
    }

    public void init(GeekRpgGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
//        this.camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
//        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.gameScreen = new GameScreen(batch);
        this.loadingScreen = new LoadingScreen(batch);
    }

//    public void resize(int width, int height) {
//        viewport.update(width, height);
//        viewport.apply();
//    }
//
//    public void resetCamera() {
//        camera.position.set(HALF_WORLD_WIDTH, HALF_WORLD_HEIGHT, 0);
//        camera.update();
//        batch.setProjectionMatrix(camera.combined);
//    }

    public void changeScreen(ScreenType type) {
        Screen screen = game.getScreen();
        Assets.getInstance().clear();
        if (screen != null) {
            screen.dispose();
        }
//        resetCamera();
        game.setScreen(loadingScreen);
        switch (type) {
            case GAME:
//                game.setScreen(gameScreen);
                targetScreen = gameScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME);
                break;
        }
    }

    public void goToTarget() {
        game.setScreen(targetScreen);
    }
}
