package com.geekbrains.rpg.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.geekbrains.rpg.game.screens.ScreenManager;

public class GeekRpgGame extends Game {
    private SpriteBatch batch;

    // Домашнее задание:
    // - Разбор кода и пишите какие вопросы возникли
    // - На экране игры добавьте Stage и сделайте две кнопки:
    // пауза и выход в меню
    // - Сделайте полезные выпадающие вещи (бутыль со здоровьем,
    // монеты)

    /**
     * при запуске игры перейти на экран...ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        ScreenManager.getInstance().init(this, batch);
        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float dt = Gdx.graphics.getDeltaTime();
        getScreen().render(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}