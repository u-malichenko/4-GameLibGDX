package com.geekbrains.rpg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 *корневой класс унаследован от Game
 * содержит только спрайтбатч - один на всю игру
 * наследуем от Game чтоб появилась возможность управлять экранами - getScreen().
 * сам либГДХ знает что у его есть активный экран
 *
 */
public class GeekRpgGame extends Game {
    private SpriteBatch batch; // на весь проект у нас будет один спрайтбатч

    // Домашнее задание:
    // + Разбор кода и пишите какие вопросы возникли
    // + Если здоровье монстра падает до 0, перекидываем его в другую точку
    // + и залечиваем полностью, герою даем монетку (от 3 до 10)
    // - * Если монстр подошел близко к герою, то раз в 0.5 сек он долен
    // наносить герою 1 урона

    /** запуск игры
     * кода запускаем игру инициализируем спрайтбатч
     * иниуиализируем менеджер экранов ScreenManager- пробрасываем ему ссылку на игру и бач
     * говорим что хотим перейти на экран GAME
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        ScreenManager.getInstance().init(this, batch);
        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
    }

    /**
     * getScreen().render(dt) - рисуем активный экран
     * возврашщает активный экран и вызывает у его мтеод рендер рисовать
     * рисуем GeekRpgGame она рисует  - getScreen().render(dt)(активный экран)
     */
    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float dt = Gdx.graphics.getDeltaTime(); // получаем разницу времени
        getScreen().render(dt); //рисуем активный экран
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}