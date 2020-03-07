package com.geekbrains.rpg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** отвечает за ИГРУ
 * так как мы его наследовали от AbstractScreen: а там мы- создаем базовый экран, так как во многих экранах
 * не хотим пока реализовывать многие методы типа паузы ресайз
 * у которого есть бач и есть пустые реализации этих методов
 *  *    теперь можно геймскрин унаследовать от абстрактскрин тем самым сделав его ИМПЛЕМЕНТС скрином
 *  *    и методы эти не писать там потому что они уже реализованны в родительстком классе AbstractScreen, пусть и пустые они пока
 *  *    это такое сокращение кода - мы реализуем только то что нам инетресно
 */
public class GameScreen extends AbstractScreen {
    private BitmapFont font32;
    private TextureRegion textureGrass;
    private ProjectilesController projectilesController; //контроллер снарядов
    private Hero hero; //герой
    private Monster monster; //монстр

    /**
     * гетер на героя для того тчоб монстр видел куда бежать
     * @return
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * геттер для экрана игры
     * @return
     */
    public ProjectilesController getProjectilesController() {
        return projectilesController;
    }

    /**
     * конструктор экрана игры
     * получает бач от ScreenManager (который получает его от GeekRpgGame)
     * пробрасываем бач к родителю
     * @param batch
     */
    public GameScreen(SpriteBatch batch) {
        super(batch);
    }

    /**
     *  - метод срабатывает каждый раз когда вы попадаете на этот экран, чтото вроде мтеода криейт
     *  создание прожектиль контроллера, героя, монстра и фонта
     *   атласа не нужен мы его грузим с помощью менеджера ресурсов:
     *         this.textureGrass = Assets.getInstance().getAtlas().findRegion("grass");
     *         используем синглтон менеджер ресурсов мы хотим получить к тебе доступ (Assets.getInstance().)
     *         у тебя был атлас .getAtlas() и в этом атласе есть регион с именем гресс .findRegion("grass")
     *         Assets.getInstance().getAtlas() - обращение к текущему активному атласу
     * все что касается ресурсов должно происходить через менеджер ресурсов
     *         this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf");
     *         из ассетс достаем любой фонт
     */
    @Override
    public void show() {
        this.projectilesController = new ProjectilesController();
        this.hero = new Hero(this); // тут отдаем ссылку на экран игры. передаем ему ссылку-хаб - этот геймскрин
        this.monster = new Monster(this); //создаем новый монстр передаем ему ссылку-хаб - этот геймскрин
        this.textureGrass = Assets.getInstance().getAtlas().findRegion("grass");
        //используем синглтон менеджер ресурсов мы хотим получить к тебе доступ (Assets.getInstance().)
        // у тебя был атлас .getAtlas() и в этом атласе есть регион с именем гресс .findRegion("grass")
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf");
    }

    /**
     * 60 раз в секунду рендерим этот экран
     * запускаем обновление update(delta);
     * отчищаем экран
     * рисуем траву в цикле заполняем экран
     * рисуем героя
     * рисуем монстра
     * рисуем прожектили
     * рисуем гуи
     *
     * @param delta - дельта тайм приходит из GeekRpgGame - getScreen().render(dt)
     */
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                batch.draw(textureGrass, i * 80, j * 80);
            }
        }
        hero.render(batch); //рендер героя
        monster.render(batch); //рендерим монстра
        projectilesController.render(batch);
        hero.renderGUI(batch, font32);
        batch.end();
    }

    /**
     * сами его придумываем, так как в либГДХ его нет
     * обновляем героя
     * обновляем монстра
     * проверяем колизии попадания срелой
     * обновляем прожектили
     *
     * @param dt
     */
    public void update(float dt) {
        hero.update(dt);
        monster.update(dt);

        checkCollisions();

        projectilesController.update(dt); //там мы циклом апдейтим активные стрелы в цикле
    }

    /**МТЕОД ПРОВЕРКИ СТОЛКНОВЕНИЙ  попаданий стрелой
     * перебиаем все прожектили что у нас есть активные - for (int i = 0; i < projectilesController.getActiveList().size(); i++)
     * получаем итый прожектиль из списка - Projectile p = projectilesController.getActiveList().get(i); создается ссылка а не объект
     * если расстояние от продектиля до монстра  меньше 24 пикселей - if (p.getPosition().dst(monster.getPosition()) < 24) { то
     * прожектиль деактивируем - p.deactivate();
     * монстру наносим урон 1 - monster.takeDamage(1);
     */
    public void checkCollisions() {
        for (int i = 0; i < projectilesController.getActiveList().size(); i++) {
            Projectile p = projectilesController.getActiveList().get(i); //создается ссылка а не объект
            if (p.getPosition().dst(monster.getPosition()) < 24) {
                p.deactivate();
                if (monster.takeDamage(1)) {
                    getHero().addCoin();
                }
            }
        }
    }
}