package com.geekbrains.rpg.game.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.geekbrains.rpg.game.logic.GameController;
import com.geekbrains.rpg.game.logic.WorldRenderer;

/**
 * не связан ни с чем, ни с героями ни с мапами посему находится в пакедж скрин
 * GameController gc; создаем геймконтроллер - там все персонажи
 * WorldRenderer worldRenderer; - отрисовщик мира
 */
public class GameScreen extends AbstractScreen {
    private GameController gc;
    private WorldRenderer worldRenderer;

    /**
     * конструктор принимает бач и пробрасывает его родителю в AbstractScreen
     * @param batch
     */
    public GameScreen(SpriteBatch batch) {
        super(batch);
    }

    /**
     * когда создается экран
     * создаем геймконтроллер и отрисовщик мира(пробрасываем ссылку на геймконтролер и бач,
     * птому что отрисовщик будет запрашивать информацию обо всех наших объектах и рисовать их
     */
    @Override
    public void show() {
        gc = new GameController();
        worldRenderer = new WorldRenderer(gc, batch);
    }

    /**
     * запускаем обновление геймконтроллера и отрисовщика мира
     * @param delta
     */
    @Override
    public void render(float delta) {
        gc.update(delta);
        worldRenderer.render();
    }
}