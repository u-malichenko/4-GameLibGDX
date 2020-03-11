package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.screens.utils.Assets;

import java.util.ArrayList;
import java.util.List;

/**ОТРИСОВЩИК
 * тут все свзанное только с графикой
 * объекты нужно отсортировать по У и рисовать их в соотвествующем порядке
 * private GameController gc; - ссылка на гейм контроллер, там живут все персонажи карта  - получаем его из инициализатор
 *  SpriteBatch batch; - бач - получаем его из инициализатора
 * font32; - фонты чтоб выводить текст
 * private List<MapElement>[] drawables; - список рисуемых вещей - массив листов - колличество строк
 *      в каждом листе лежат свои элементы- персонажи и стены, только из этой конкретной строки
 *      чем меньше индекс листа тем дальше этот лист от зрителя - имеют разный вес
 *      будем рисовать сперва траву потом всех персонажей на этой линии и так в цикле все линии с верху в низ
 *      так как первые будт нарисованы раньше то следующие элементы перекрывают предыдущие
 */
public class WorldRenderer {
    private GameController gc;
    private SpriteBatch batch;
    private BitmapFont font32;
    private List<MapElement>[] drawables; //список объектов на определнной линнии карты

    /**
     * конструктор
     * оздаем массив листов карты для построчного вывода ближних и дальних элементов:
     *          this.drawables = new ArrayList[Map.MAP_CELLS_HEIGHT]; //размер карты по высоте
     *          длинна массива = высоте карты = 9(пропорции 16*9)
     *
     * заполняем массив листов
     *         for (int i = 0; i < drawables.length; i++) {
     *             drawables[i] = new ArrayList<>();
     *
     * @param gameController
     * @param batch
     */
    public WorldRenderer(GameController gameController, SpriteBatch batch) {
        this.gc = gameController;
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf");
        this.batch = batch;
        this.drawables = new ArrayList[Map.MAP_CELLS_HEIGHT]; //размер карты по высоте
        for (int i = 0; i < drawables.length; i++) { //инициализируем лист будущих элементов
            drawables[i] = new ArrayList<>();
        }
    }

    /**
     * отрисовка
     * первым делом отчищаем спиок наших объектов на карте в листах массива:
     *          for (int i = 0; i < drawables.length; i++) { drawables[i].clear();}
     *          чистим массив листов
     * должны понять в какую клетку закинуть персонажа,
     * добавляем двух персонажей:
     *          drawables[...] = строка на карте
     *          в клетку по высторе ... = gc.getHero().getCellY(). - это метод возвращает return (int) (position.y - 20) / 80;
     *          - берем координаты по У-20 делим на размер клетки получаем индекс ячейки из листов в которую нужно положить персонажа
     *          добавляем героя = add(gc.getHero()
     *         drawables[gc.getHero().getCellY()].add(gc.getHero());
     *         drawables[gc.getHero().getCellY()].add(gc.getMonster());
     *         когда убдет контроллер для монстров то они тоже будут раскладываться в цикле!
     *         пока они одни делаем тупо двумя строками
     * добавляем прожектили аналогично персонажам:
     *              проходимся по всем прожектилям спрашиваем на какой полосе они находятсчя и добавляем их в лист
     *             for (int i = 0; i < gc.getProjectilesController().getActiveList().size(); i++) {
     *             Projectile p = gc.getProjectilesController().getActiveList().get(i);
     *             drawables[p.getCellY()].add(p);
     * отчищаем экран:
     *         Gdx.gl.glClearColor(0, 0, 0, 1);
     *         Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
     *
     *  начинаем рисовать- batch.begin();
     * полноценный цикл для отрисовки земли не используем так как сперва рисуем землю потом персонажа  уже на земле чтоб перекрывало
     *
     *
     */
    public void render() {
        for (int i = 0; i < drawables.length; i++) { //отчистить лист элементоа
            drawables[i].clear();
        }
        //раскладываем персонажей по карте(в листах массива)
        drawables[gc.getHero().getCellY()].add(gc.getHero()); //добавить героя на нужную линию, где он находится

        for (int i = 0; i < gc.getMonsterController().getActiveList().size(); i++) {
            Monster monster = gc.getMonsterController().getActiveList().get(i);
            drawables[monster.getCellY()].add(monster);
        }

        for (int i = 0; i < gc.getProjectilesController().getActiveList().size(); i++) {
            Projectile p = gc.getProjectilesController().getActiveList().get(i);
            drawables[p.getCellY()].add(p);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
/**
 * земля ни кого не может перекрывать ее рисуем всю целиком
 * рисуем всю всю землю,  начинаем отрисовку с самой верхней ячейки Map.MAP_CELLS_HEIGHT - 1 и бежим до 0
 *      for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {
 * во втором цикле бежим от 0 до Map.MAP_CELLS_WIDTH длинны карты
 *      for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
 * - перебираем все клетки карты.
 * запускаем метод из карты для рисования клетки земли
 *      gc.getMap().renderGround(batch, x, y);
 */
        for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
                gc.getMap().renderGround(batch, x, y);
            }
        }

/**
 * все остальные от земли объекты нужно рисовать в правильном порядке в порядке У(герой не первый)
 *  начинаем рисовать персонажей,+ стены + снаряды вначале тех что повыше потом тех что пониже в листах:
 * начинаем отрисовку с самой верхней ячейки Map.MAP_CELLS_HEIGHT - 1 и бежим до 0
 *  *      for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {
 *  * во втором цикле бежим от 0 до drawables[y].size() длинны подячейки массивалистов drawables[y].
 *  *      for (int i = 0; i < drawables[y].size(); i++) {
 *  рисуем персонажей стоящей на этой линии drawables[y].get(i).render(batch, null);
 *  а потом рисуем стены(они перекроют живые объекты если они есть):
 *         for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
 *         gc.getMap().renderUpper(batch, x, y);
 * вконце рисуем ГУИ gc.getHero().renderGUI(batch, font32);
 */
        for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {
            for (int i = 0; i < drawables[y].size(); i++) {
                drawables[y].get(i).render(batch, null);
            }
            for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
                gc.getMap().renderUpper(batch, x, y);
            }
        }
        gc.getHero().renderGUI(batch, font32);
        batch.end();
    }
}
