package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.screens.utils.Assets;

/**
 * КАрта
 * созается в GameController
 * может быть байтовым массивом а может быть массивомм энамов
 * private byte[][] data; - байтовая карта 0 это трава 1 это стенка
 * задаем константы размера карты:
 *      public static final int MAP_CELLS_WIDTH = 16;
 *      1280/80 =16
 *     public static final int MAP_CELLS_HEIGHT = 9;
 *     720/80=9
 * задаем регионы текстур травы и стен:
 *      private TextureRegion grassTexture;
 *     private TextureRegion wallTexture;
 *
 */
public class Map {
    public static final int MAP_CELLS_WIDTH = 16;
    public static final int MAP_CELLS_HEIGHT = 9;

    private byte[][] data;
    private TextureRegion grassTexture;
    private TextureRegion wallTexture;

    /**может ли какй-то летяший объект пролететь через эту ячейку?
     * проверка проходимости воздуха, стрелы над водой могут леать а вот сквозь стену не должны
     * если стена то ==1 и значит вернется фолсе = низя пролететь
     * элемент в массиве data[cellX][cellY] = 0 = тру
     * @param cellX
     * @param cellY
     * @return
     */
    public boolean isAirPassable(int cellX, int cellY) {
        return data[cellX][cellY] == 0;
    }

    /**может ли какй-то  объект пройти через эту ячейку?
     * проверка проходимости земли, не должны проходить через стену
     * если стена то ==1 и значит вернется фолсе = низя пройти
     * элемент в массиве data[cellX][cellY] = 0 = тру
     * @param cellX
     * @param cellY
     * @return
     */
    public boolean isGroundPassable(int cellX, int cellY) {
        return data[cellX][cellY] == 0;
    }
    /**может ли какй-то  объект пройти через эту ячейку? ПО ВЕКТОРУ для GameController.collideUnits
     * проверка проходимости земли, не должны проходить через стену
     * если стена то ==1 и значит вернется фолсе = низя пройти
     * элемент в массиве data[(int)(position.x / 80)][(int)(position.y / 80)] = 0 = тру
     * @return
     */
    public boolean isGroundPassable(Vector2 position) {
        return data[(int)(position.x / 80)][(int)(position.y / 80)] == 0;
    }

    /**
     * конструктор карты
     * this.data = new byte[MAP_CELLS_WIDTH][MAP_CELLS_HEIGHT]; - задаем размер мааива карты
     * рисуем стены рандомным циклом(0 равно трава 1 = стена):
     *          for (int i = 0; i < 10; i++) {
     *             data[MathUtils.random(15)][MathUtils.random(8)] = 1;
     *
     * задаем регионы текстур травы и стен:
     *          this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
     *         this.wallTexture = Assets.getInstance().getAtlas().findRegion("wall");

     */
    public Map() {
        this.data = new byte[MAP_CELLS_WIDTH][MAP_CELLS_HEIGHT];
        for (int i = 0; i < 10; i++) {
            data[MathUtils.random(15)][MathUtils.random(8)] = 1;
        }
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.wallTexture = Assets.getInstance().getAtlas().findRegion("wall");
    }

    /**У карты не будет полноценного рендера, мапа не умеет рисовать сама себя
     * будем просто заправшивать рисовать либо землю либо како-то объект в клетке
     *
     * рисуем землю  в какой то клетке используется в WorldRenderer.
     * просто в соответсвующей ячейке рисуем кусочек земли batch.draw(grassTexture, x * 80, y * 80);
     * полноценный цикл для отрисовки земли не используем так как сперва рисуем землю потом персонажа  уже на земле чтоб перекрывало
     * @param batch
     * @param x
     * @param y
     */
    public void renderGround(SpriteBatch batch, int x, int y) {
        batch.draw(grassTexture, x * 80, y * 80);
    }

    /**если у тебя в клетке что-то стоит то рисуй его иначе и ен рисуй ни чего
     * рисуем объект на карте используется в WorldRenderer
     * если в клетке есть 1 то там нужно нарисовать стену
     * batch.draw(wallTexture, x * 80, y * 80);
     *
     * @param batch
     * @param x
     * @param y
     */
    public void renderUpper(SpriteBatch batch, int x, int y) {
        if (data[x][y] == 1) {
            batch.draw(wallTexture, x * 80, y * 80);
        }
    }
}
