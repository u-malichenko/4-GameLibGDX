package com.geekbrains.rpg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.geekbrains.rpg.game.GeekRpgGame;
import com.geekbrains.rpg.game.screens.utils.Assets;

/**
 * полная цепока создания скрина
 * -создали класс MenuScreen
 * -создали поле private GameScreen gameScreen;
 * - добавили в энам еще один элемент   MENU,
 * - при запуске создаем этот экран init - this.menuScreen new MenuScreen(batch);
 * - добавляем ветку в кейс changeScreen
 */
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
    private MenuScreen menuScreen;
    private Screen targetScreen;
    private Viewport viewport;
    private Camera camera;

    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Camera getCamera() {
        return camera;
    }

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
        this.menuScreen = new MenuScreen(batch);
        this.camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);//фит вьюпорт - вписать в окно с сохранением пропорций
        this.gameScreen = new GameScreen(batch);
        this.loadingScreen = new LoadingScreen(batch);
    }
/**
 * пересчет мира на новый размер при масштабаровании во вьюпорт отдаем новые размеры мира
 */
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    /**
     * сбрасывает камеру - при переходе в меню
     */
    public void resetCamera() {
        camera.position.set(HALF_WORLD_WIDTH, HALF_WORLD_HEIGHT, 0);
        camera.update();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
    }

    /**
     * выставляет камеру в нужную точку
     * @param position
     */
    public void pointCameraTo(Vector2 position){
        camera.position.set(position, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    /**
     * сбрасываем ввод: нужно для должен реагировать на стейт меню  нужно сбросить, иначе он будет видеть те кнопки которых на геймскрине нету обрабатывает только клики мышкой и нажатия клавиатуры
     *         Gdx.input.setInputProcessor(null); - при проходе между окнами идет сброс проуцесоора встандартное состояние
     * @param type
     */
    public void changeScreen(ScreenType type) {
        Screen screen = game.getScreen();
        Assets.getInstance().clear();
        //сбрасываем ввод:
        Gdx.input.setInputProcessor(null);
        if (screen != null) {
            screen.dispose();
        }
        resetCamera(); //сбрасываем камеру каждый раз при переходе на новый экран
        game.setScreen(loadingScreen);
        switch (type) {
            case MENU:
//                game.setScreen(gameScreen);
                targetScreen = menuScreen; // целевой экран, нужен для отображения лоадингскрина
                Assets.getInstance().loadAssets(ScreenType.MENU);// грузим ассексы для экрана меню
                break;

            case GAME:
            targetScreen = gameScreen;
            Assets.getInstance().loadAssets(ScreenType.GAME);
            break;
        }
    }

    public void goToTarget() {
        game.setScreen(targetScreen);
    }
}
