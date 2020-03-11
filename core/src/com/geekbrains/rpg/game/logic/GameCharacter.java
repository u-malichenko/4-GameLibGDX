package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.screens.utils.Assets;

/**
 * этот класс является мап элементом(и все его подклассы)
 * implements MapElement
 * <p>
 * Абстрактный игровой персонаж
 * <p>
 * ДЕлаем все параметры протектед чтоб могли пользоваться дочки чтоб не писать геттеры
 * <p>
 * gc - ссылка на контроллер игры - итоговый хаб всего нашего приложения
 * position; - позиция персонажа
 * dst - место назначения движения персонажа нужно для рассчета перемещений
 * tmp; - темповый вектор для рассчетов
 * protected Vector2 tmp2; - для общета поподания в стену
 * protected Circle area; - у каждого персонажа есть окружнотсь куда не могут вселяться монстры
 * speed - скорость
 * hp, hpMax; - здоровье и мкасимально возможное
 * texture - текстура персонажа
 * textureHp - тексстура здровья персонажа так как она у всех персонажей будет одинаковая
 * <p>
 * lifetime - жизненное вермя
 */
public abstract class GameCharacter implements MapElement {
    protected GameController gc;

    protected TextureRegion texture;
    protected TextureRegion textureHp;

    protected Vector2 position;
    protected Vector2 dst;
    protected Vector2 tmp;
    protected Vector2 tmp2;

    protected Circle area;

    protected float lifetime;
    protected float speed;
    protected int hp, hpMax;

    /**
     * научим персонажа вычислять свою клетку в данный моент
     * геттер на вычисление нужной строки для отрисовки на карте
     *
     * @return
     */
    public int getCellX() {
        return (int) position.x / 80;
    }

    /**
     * высота персонажа -20 - относительно ног делаем
     * геттер на вычисление нужной клетки для отрисовки на карте
     * берем координаты по У делим на размер клетки получаем индекс ячейки листов в которую нужно положить персонажа
     * 720/80=9
     *
     * @return
     */
    public int getCellY() {
        return (int) (position.y - 20) / 80;
    }

    /**
     * чтоб не дублировать логику
     * перегруженный метод смены позиции персонажа И ЕГО ОКРЖНОСТИ
     *
     * @param x
     * @param y
     */
    public void changePosition(float x, float y) {
        position.set(x, y);
        area.setPosition(x, y - 20);
    }

    /**
     * СМЕНА ПОЗИЦИИ
     * если начал вселятся в другого персонажа используется в GameController.collideUnits
     * запускаем перегруженный метод смены координат, см выше changePosition(float x, float y)
     * нужно для того чтоб одновременно с персонажем переместить и его окружность
     *
     * @param newPosition - передаются скорректированные координаты из GameController.collideUnits
     */
    public void changePosition(Vector2 newPosition) {
        changePosition(newPosition.x, newPosition.y);
    }

    /**
     * ПОЛУЧЕНИЕ позиции персонажа
     *
     * @return - координаты персонажа
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * геттер на окружность героя
     *
     * @return
     */
    public Circle getArea() {
        return area;
    }

    /**
     * конструктор
     * this.gc = gc; - получаем нконтроллер игры hpMax и скорость -speed
     * this.tmp = new Vector2(0.0f, 0.0f); - инициализаруем - задаем пустой вектор для темпа и для дст
     * this.area = new Circle(0.0f, 0.0f, 15); - располагаем окркжности героев в ногах и делем ее нулевой
     * а когда герой инициализируется в дочернем классе мы ему меняем этот параметр this.changePosition(100.0f, 100.0f);
     * this.hp = this.hpMax; - инициализируем сразу максимально возможное здоровье
     *
     * @param gc    - гейм контроллер, получаем при создании при инициализауии нужен для связи
     * @param hpMax - максимальное здоровье получаем при создании от потомка(из его конструктора)
     * @param speed -скорость получаем при создании от потомка(из его конструктора)
     */
    public GameCharacter(GameController gc, int hpMax, float speed) {
        this.gc = gc;
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
        this.tmp = new Vector2(0.0f, 0.0f);
        this.tmp2 = new Vector2(0.0f, 0.0f);
        this.dst = new Vector2(0.0f, 0.0f);
        this.position = new Vector2(0.0f, 0.0f);
        this.area = new Circle(0.0f, 0.0f, 15);
        this.hpMax = hpMax;
        this.hp = this.hpMax;
        this.speed = speed;
    }

    /**
     * * все поведние общее для персонажей выносим в суперский апдейт и потом просто его выполняем
     * lifetime += dt; в любом случае увеличивается на Дельтатайм
     * ВЫнесли в общий класс движение монстра и героя:
     * tmp.set(dst).sub(position).nor().scl(speed); - ветор скорости
     * tmp2.set(position); - запоминаем старую позицию
     * если позишин больше тогда он двигается в сторону назначения dst
     * if (position.dst(dst) > speed * dt) {
     * position.mulAdd(tmp, dt);
     * } else { position.set(dst);}
     * иначе он уставнавливается в позицию назначения  чтоб не дергался
     * <p>
     * сделаем так чтоб персонаж не мог зайти в стену, перед перемещением проверяем что не стена:
     * осле первоначального перемещения - если клетка не проходима?:
     * мапа говорит нам что земля в той точке где мы сейчас стоим не проходима:
     * if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
     * то мы возвращаемся на точку назад:
     * position.set(tmp2);
     * далее делаем плавное обтекание стены:
     * пробуем сместится только по х:
     * position.add(tmp.x * dt, 0);
     * если после этого мы все еще в стене?:
     * if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
     * то мы сбрасываем наше положение:
     * position.set(tmp2);
     * и пробуем сместится по У:
     * position.add(0, tmp.y * dt);
     * если после смещения по У мы все равно оказались в стене:
     * if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
     * то стоим на месте и не щшевелимся:
     * position.set(tmp2);
     * можно сделать чтоб стена выталкивала персонажа от себя, как от врага-много вычислений
     * <p>
     * каждый раз когда персонаж перемещается его ОКРУЖНОСТЬ должна следовать за ним:
     * area.setPosition(position.x, position.y - 20)
     *
     * @param dt - разница времени с предыдущего обновления
     */
    public void update(float dt) {
        lifetime += dt;
        tmp.set(dst).sub(position).nor().scl(speed); //рассчитываем скорость персонажа
        tmp2.set(position); //запоминаем старую позицию
        if (position.dst(dst) > speed * dt) {
            position.mulAdd(tmp, dt);
        } else {
            position.set(dst);
        }

        if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
            position.set(tmp2); //если влипли то вернулись назад
            position.add(tmp.x * dt, 0); //пробуем сместится только по х
            if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
                position.set(tmp2);
                position.add(0, tmp.y * dt);
                if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
                    position.set(tmp2);
                }
            }
        }
        area.setPosition(position.x, position.y - 20);
    }

    /**
     * на уничтожение должны реагировать по разному герой и монстр
     * общее для персонажей выносим в суперский takeDamage и потом просто его выполняем
     * если убили совсем здроровье=0 выполняем абстрактный метод onDeath();- чтоб отличать героя от монстра
     *
     * @param amount - колличество урона
     * @return - вернем тру если уничтожили вконец иначе фолс
     */
    public boolean takeDamage(int amount) {
        hp -= amount;
        if (hp <= 0) {
            onDeath();
            return true;
        }
        return false;
    }

    /**
     * реализуется поразному у каждого персонажа
     * когда его уничтожили тогда он должен вот это выполнить
     */
    public abstract void onDeath();
}
