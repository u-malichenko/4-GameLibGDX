package com.geekbrains.rpg.game.logic.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *интерфейс который говорит что у всех элементов рисуемых на карте есть 3 метода
 * рендер для отрисовки
 * и получение клетки по Х и получение клетки по У
 * реализуем этот интефейс у героя монстра и прожектилей
 */
public interface MapElement {
    void render(SpriteBatch batch, BitmapFont font);
    int getCellX();
    int getCellY();
}
