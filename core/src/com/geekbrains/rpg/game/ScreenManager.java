package com.geekbrains.rpg.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** управляющий глобальный класс работы со всеми экранами
 * энам с всеми экранами
 * прописаны констаннты нашего мира и их половинные размеры
 * есть ссылка на имеет дотуп к корневой игре
 * есть ссылка на бач
 * инициализирует сразу все экраны
 * экран загрузки
 * экран игры
 *
 */
public class ScreenManager {
    /**
     * энам говорит нам какие экраны есть в игре
     * на какие экраны вы потенциально можете перейти
     */
    public enum ScreenType {
        MENU, GAME
    }

    public static final int WORLD_WIDTH = 1280;
    public static final int HALF_WORLD_WIDTH = WORLD_WIDTH / 2;
    public static final int WORLD_HEIGHT = 720;
    public static final int HALF_WORLD_HEIGHT = WORLD_HEIGHT / 2;

    private GeekRpgGame game; //имеет дотуп к корневой игре
    private SpriteBatch batch;
    private LoadingScreen loadingScreen; //экран загрузки
    private GameScreen gameScreen;
    private Screen targetScreen; //скрин куда мы хотеле перейти запомнили его перед загрузкой экрана загрузки
//    private Viewport viewport;
//    private Camera camera;
    /** ЧТОБ МЫ МОГЛИ ПОЛУЧАТЬ К ЕМУ ДОСТУП ИЗ ЛЮБОЙ ТОЧКИ ПРОГРАМЫ
     * сам скрин менеджер является СИНГЛТОНОМ - глобальный объект,
     * мы можем получить доступ к нему из любой част нашей программы
     * если вы хотите обратится к ScreenManager - выполните статический метод getInstance
     * идея сингл тона - нужно хранить состояние объекта и оно должно быть одно на все приложение -глобальный объект
     * реализация - создается обычный классджава
     * - создается приватное статическое поле private static ScreenManager ourInstance = new ScreenManager(); экземпляр меня, создается экземпляр этого класса
     * чтоб этот объект достать создается статический геттер public static ScreenManager getInstance() {return ourInstance;}
     * этот getInstance возвращает ссылку на этот объект
     * такой объект может быть только один вообще во всей программе, потому что
     * у данного класса конструктор приватный private ScreenManager() { }
     * при всем желении объект этого класса низя создать снаружи
     * когда мы запрашиваем public static ScreenManager getInstance()
     * срабатывает статическаое поле инициализации - private static ScreenManager ourInstance = new ScreenManager();
     * объект создается и нам возвращается на его ссылка,
     * каждый следующий раз мы будем получать ссылку на этот объект(уже готовый)
     *
     */
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

    /**
     * конструктор ScreenManager
     * приватный так как обеъект является синглтоном
     * снаружи такой конструктор создать низя
     */
    private ScreenManager() {
    }

    /** ИНИЦИАЛИЗАЦИЯ ScreenManager
     * получает игру и бач от GeekRpgGame (прокинута ссылка на игру и доступ к бачу)
     * когда мы создаем скринманагер он созадет сразу все экраны(это не страшно все скрины оч легковесны)
     */
    public void init(GeekRpgGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
//        this.camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
//        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.gameScreen = new GameScreen(batch); //передаем бач от инициализатора игрц в конструктор экрана геймскрин
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

    /**СМЕНИТЬ ЭКРАН
     * получаем ссылку на экран куда хотим перейти
     * а на каком экране мы находились? Screen screen = game.getScreen(); получаем ссылку на текущий экран из главного модуля
     * раз уж мы переходим на другой экран текушщий мы счищаем Assets.getInstance().clear(); чистим экран
     * усли мы только что запустились if (screen != null) (экрана не было) вернее если мы не только запустились а уже
     * были на не нулевом экране то делаем screen.dispose(); у еткущего экрана, чистим объекты, чистим за собой все ресурсы чтоб ни какого мусора не валялось
     * всегда между сменой экранов мы переходим на экране загрузки - game.setScreen(loadingScreen); экран с полоской загрузки
     * кейс по экранам  с хитростью
     * запоминаем что нам нужно было перейти на игровой экран - targetScreen = gameScreen; запоминаем куда мы хотели перейти
     * начинаем грузить ресурсы для игры - Assets.getInstance().loadAssets(ScreenType.GAME); метеод загрузки ресурсов куда мы передаем тип экрана куда мы хотим попасть
     *
     * @param type - имя экрана на который мы хотим сменить экран
     */
    public void changeScreen(ScreenType type) {
        Screen screen = game.getScreen();
        Assets.getInstance().clear();
        if (screen != null) {
            screen.dispose();
        }
//        resetCamera();
        game.setScreen(loadingScreen); // перейти на экран лоадингскриин всегда грузим лоадингскрин и продолжаем идем в кейс
        switch (type) {
            case GAME:
//                game.setScreen(gameScreen); // в качестве основного экрана используй - gameScreen(там срабатывает мтеод show()
                targetScreen = gameScreen; //запоминаем куда мы хотели перейти
                Assets.getInstance().loadAssets(ScreenType.GAME); // грузим ресурсы для геймскрина
                break;
        }
    }

    /**метод переключает игру в целевой экран(запомненый) туда куда мы хотели
     * метод для перехода на следующий экран(запомненый в кейсе в ветках)
     */
    public void goToTarget() {
        game.setScreen(targetScreen);
    }
}
