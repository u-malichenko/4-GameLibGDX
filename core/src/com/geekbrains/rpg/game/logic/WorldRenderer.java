package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.screens.ScreenManager;
import com.geekbrains.rpg.game.screens.utils.Assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorldRenderer {
    private GameController gc;
    private SpriteBatch batch;
    private BitmapFont font10;
    private List<MapElement>[] drawables;
    private Comparator<MapElement> yComparator;
    private Vector2 pov;

    private FrameBuffer frameBuffer; // буфер где ве отрисовываем
    private TextureRegion frameBufferRegion;
    private ShaderProgram shaderProgram; //подпрограмка для затемнения

    public WorldRenderer(GameController gameController, SpriteBatch batch) {
        this.gc = gameController;
        this.font10 = Assets.getInstance().getAssetManager().get("fonts/font10.ttf");
        this.batch = batch;
        this.pov = new Vector2(0, 0);
        this.drawables = new ArrayList[Map.MAP_CELLS_HEIGHT];
        for (int i = 0; i < drawables.length; i++) {
            drawables[i] = new ArrayList<>();
        }
        yComparator = new Comparator<MapElement>() {
            @Override
            public int compare(MapElement o1, MapElement o2) {
                return (int) (o2.getY() - o1.getY());//чем персонаж выше тем раньше будет отрисован
            }
        };
        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, ScreenManager.WORLD_WIDTH, ScreenManager.WORLD_HEIGHT, false); //создаем буфер
        this.frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());//назначаем регион
        this.frameBufferRegion.flip(false, true);// оказывается перевернут и поэтому сами делаем флип
        this.shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/vertex.glsl").readString(), Gdx.files.internal("shaders/fragment.glsl").readString());
        //создали шейдерную программу из двух шейдеров - вершинный и фрагментный указали пути к ним
        if (!shaderProgram.isCompiled()) { //выполняем компиляцию шейдера если ошибка то выбрасываем исключение:
            throw new IllegalArgumentException("Error compiling shader " + shaderProgram.getLog());
        }
    }

    /**
     * запускаем цикл и говорим что у нас много АКТИВНЫХ монстров:
     * for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {
     * Monster m = gc.getMonstersController().getActiveList().get(i); //это ссылка! локальная в стеке
     * drawables[m.getCellY()].add(m);
     */
    public void render() {
        //чтоб камера не вылезала за края карты
        pov.set(gc.getHero().getPosition());//прицеливаемся в игрока
        if (pov.x < ScreenManager.HALF_WORLD_WIDTH) { //если меньше половины экрана
            pov.x = ScreenManager.HALF_WORLD_WIDTH; //не моет заглядывать за границы экрана
        }
        if (pov.y < ScreenManager.HALF_WORLD_HEIGHT) {
            pov.y = ScreenManager.HALF_WORLD_HEIGHT;
        }
        if (pov.x > gc.getMap().getWidthLimit() - ScreenManager.HALF_WORLD_WIDTH) { //если меньше половины экрана
            pov.x = gc.getMap().getWidthLimit() - ScreenManager.HALF_WORLD_WIDTH; //не моет заглядывать за границы экрана
        }
        if (pov.y > gc.getMap().getHeightLimit() - ScreenManager.HALF_WORLD_HEIGHT) {
            pov.y = gc.getMap().getHeightLimit() - ScreenManager.HALF_WORLD_HEIGHT;
        }
        ScreenManager.getInstance().pointCameraTo(pov); //нацеливаем камеру на tmp = hero if size screen not /2 переместили камеру на игрока
        //рисуем МИР в буфер

        //чистим объекты отрисовки:
        for (int i = 0; i < drawables.length; i++) {
            drawables[i].clear();
        }
        //рассчитываем что в каком порядке нужно рисовать:
        //отрисовка героя:
        drawables[gc.getHero().getCellY()].add(gc.getHero());
        //отрисовка монстров:
        for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {
            Monster m = gc.getMonstersController().getActiveList().get(i);
            drawables[m.getCellY()].add(m);
        }
        //отрисовка снарядов:
        for (int i = 0; i < gc.getProjectilesController().getActiveList().size(); i++) {
            Projectile p = gc.getProjectilesController().getActiveList().get(i);
            drawables[p.getCellY()].add(p);
        }
        //отрисовка оружия:
        for (int i = 0; i < gc.getWeaponsController().getActiveList().size(); i++) {
            Weapon w = gc.getWeaponsController().getActiveList().get(i);
            drawables[w.getCellY()].add(w);
        }
        //отрисовка поверапсов:
        for (int i = 0; i < gc.getPowerUpController().getActiveList().size(); i++) {
            PowerUp p = gc.getPowerUpController().getActiveList().get(i);
            drawables[p.getCellY()].add(p);
        }
        //отрисовка бонусов:
        for (int i = 0; i < gc.getBonusController().getActiveList().size(); i++) {
            Bonus bonus = gc.getBonusController().getActiveList().get(i);
            drawables[bonus.getCellY()].add(bonus);
        }
        //сортировка по приближению
        for (int i = 0; i < drawables.length; i++) {
            Collections.sort(drawables[i], yComparator);
        }

        frameBuffer.begin();    //вся отрисовка идет в буфер для начала
        //чистим экран:
        Gdx.gl.glClearColor(0, 0, 0, 1);
        //все что кассается мира нарисовали во фрейм буфер:
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
                gc.getMap().renderGround(batch, x, y);
            }
        }
        for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {
            for (int i = 0; i < drawables[y].size(); i++) {
                drawables[y].get(i).render(batch, font10);
            }
            for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
                gc.getMap().renderUpper(batch, x, y);
            }
        }
        gc.getSpecialEffectsController().render(batch); //отрисовка анамации ударов, поверх всего
        batch.end();
        frameBuffer.end();//закончили с буфером отрисокой МИРА

        //рисуем на экран:
        // сбросить камеру и сказать чтоб она смотрела в центр экрана:
        ScreenManager.getInstance().resetCamera(); //сбрасываем камеру

        //после всего, рисуем весь 1! кадр:
        batch.begin(); //вывод на экран с использованием шейдера
        batch.setShader(shaderProgram); //устонавливаем при отрисовке шейдкрную программу - активировать шецйдер
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("time"), gc.getWorldTime()); //пробрасываем переменные в шейдер
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("px"), pov.x, 1280.0f);
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("py"), pov.y, 720.0f);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(frameBufferRegion, 0, 0);//нарисовали фрейм буфер
        batch.end();
        batch.setShader(null); //деактивируем шейдер

        batch.begin();//поверх рисуем гуи игрока
        gc.getHero().renderGUI(batch, font10);
        batch.end();

        ScreenManager.getInstance().pointCameraTo(pov); //возвращаем камеру к герою чтоб мышка счтала координаты относительно героя
    }
}
