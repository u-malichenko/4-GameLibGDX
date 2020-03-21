package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.screens.utils.Assets;

import java.util.ArrayList;
import java.util.List;

public class WorldRenderer {
    private GameController gc;
    private SpriteBatch batch;
    private BitmapFont font10;
    private List<MapElement>[] drawables;

    public WorldRenderer(GameController gameController, SpriteBatch batch) {
        this.gc = gameController;
        this.font10 = Assets.getInstance().getAssetManager().get("fonts/font10.ttf");
        this.batch = batch;
        this.drawables = new ArrayList[Map.MAP_CELLS_HEIGHT];
        for (int i = 0; i < drawables.length; i++) {
            drawables[i] = new ArrayList<>();
        }
    }

    /**
     *
     *
     * запускаем цикл и говорим что у нас много АКТИВНЫХ монстров:
     *         for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {
     *             Monster m = gc.getMonstersController().getActiveList().get(i); //это ссылка! локальная в стеке
     *             drawables[m.getCellY()].add(m);
     */
    public void render() {
        for (int i = 0; i < drawables.length; i++) {
            drawables[i].clear();
        }
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
        //отрисовка бонусов:
        for (int i = 0; i < gc.getBonusController().getActiveList().size(); i++) {
            Bonus bonus = gc.getBonusController().getActiveList().get(i);
            drawables[bonus.getCellY()].add(bonus);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
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
        gc.getHero().renderGUI(batch, font10);
        batch.end();
    }
}
