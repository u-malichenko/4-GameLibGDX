package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.screens.utils.Assets;

/**
 * * Мы добились:
 * *              логика обработки состояний живет только в GameCharacter
 * *              а переход из состояния в состояние живет в кадом конкретном виде персонажей МОнстр Герой
 * <p>
 * protected GameCharacter lastAttacker; - последний атакующий персонаж нужен для реализации убегания монстра в случае малого здоровья
 * <p>
 * protected float attackRadius; - радиус видимости
 * protected float attackRadius; - радиус атаки
 */
public abstract class GameCharacter implements MapElement {
    /**
     * состояния персонажей
     * RETREAT - раненый монст должен убегать
     */
    public enum State {
        IDLE, MOVE, ATTACK, PURSUIT, RETREAT
    }

    //высота и ширина стандартной картинки
    static final int WIDTH = 60;
    static final int HEIGHT = 60;

    protected GameController gc;

    //делаем массив текстур для анимации:
    protected TextureRegion[][] textures;
    protected TextureRegion textureHp;

    protected State state;
    protected float stateTimer;

    protected GameCharacter lastAttacker;
    protected GameCharacter target;

    protected Vector2 position;
    protected Vector2 dst;
    protected Vector2 tmp;
    protected Vector2 tmp2;

    protected Circle area;

    protected float lifetime;
    protected float attackTime;
    protected float walkTime; //таймер для анимации
    protected float timePerFrame;  //скорость смены кадра анимации

    protected float visionRadius;
    protected float speed;
    protected int hp, hpMax;

    protected Weapon weapon; //каждый персонаж владеет только 1 оружием оружие делается в конструкторе каждого персонада отдельно сами они его вояют


    public int getCellX() {
        return (int) position.x / 80;
    }

    public int getCellY() {
        return (int) (position.y - 20) / 80;
    }

    /**
     * метод установить подобранное оружие
     *
     * @param weapon
     */
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    /**
     * корректриов ка координат по окну -20 по ногам персонажа
     * Map.MAP_CELLS_HEIGHT*80 - колличество ячеек умноженное на 80-размер квадрата
     * поскольку только этот метод занимается перемещением
     * он тут же перемещает окружность персонажа
     * он тут же проверяет выход за пределы экрана
     * смена позиции везде только через этот метод, ни в коем случае не должно быть set
     *
     * @param x
     * @param y
     */
    public void changePosition(float x, float y) {
        position.set(x, y);
        if (position.x < 0.1f) {
            position.x = 0.1f;
        }
        if (position.y - 20 < 0.1f) { //-20 так как рассчитываем все для позиции ног
            position.y = 20.1f;
        }
        if (position.x > Map.MAP_CELLS_WIDTH * 80 - 1) {
            position.x = Map.MAP_CELLS_WIDTH * 80 - 1;
        }
        if (position.y - 20 > Map.MAP_CELLS_HEIGHT * 80 - 1) {
            position.y = Map.MAP_CELLS_HEIGHT * 80 - 1 + 20;
        }
        area.setPosition(position.x, position.y - 20);
    }

    public void changePosition(Vector2 newPosition) {
        changePosition(newPosition.x, newPosition.y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Circle getArea() {
        return area;
    }

    //TODO
    public boolean isAlive() {
        return hp > 0;
    }

    /**
     * this.state = State.IDLE; - когда создаем персонаж он всегда в состоянии ни чего не деланья
     * this.target = null; - цель
     * this.stateTimer = 1.0f; - таймер на состояния
     *
     * @param gc
     * @param hpMax
     * @param speed
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
        this.state = State.IDLE;
        this.stateTimer = 1.0f;
        this.timePerFrame = 0.2f; //скорость смены кадра анимации
        this.target = null;
    }

    /**
     * метод который будет возвращать индекс кадра  текстуры
     * //формула для рассчета индекса показываемого кадра, в зависимости от текущего времени и и от вермени на кадре чем меньше время на кадре тем быстрее дергаются
     * // index = (int)(walkTime/timePerFrame)%колличество кадров
     *
     * @return
     */
    public int getCurrentFrameIndex() {
        return (int) (walkTime / timePerFrame) % textures[0].length;
    }

    /**
     * все относительно атаки вынесли в оружие и как персонаж будет бить зависит от того какое у его оружие в руках
     * <p>
     * все поведние общее для персонажей выносим в суперский апдейт и потом просто его выполняем
     * в ПЕрсонажах будут правила смены состояний а то как эти состояния обрабатываются будут в базовом классе(тут)
     * тоесть по сути и герои и монстры ведут себя одинаково а как меняются их состояния зависит либо от мозгов бота либо от нашей мышки
     * <p>
     * lifetime += dt; в любом случае увеличивается на Дельтатайм
     * <p>
     * //ЕСЛИ ТЫ В СТОСТОЯНИ АТАКИ:
     * если ты в состоянии АТАКИ то:
     * if (state == State.ATTACK) { dst.set(target.getPosition()); - дст = координатам нашей мишени
     * <p>
     * //ДВИЖЕНИЕ ДЛЯ ОПРЕЛЕННЫХ СТАТУСОВ
     * если статус движение ИЛИ статус = УБЕГАТЬ ИЛИ АТАКА И цель находится в радиусе атаки ОРУЖИЯ -10(чтоб не ближний код)  то:
     * if (state == State.MOVE || state == State.RETREAT || (state == State.ATTACK && this.position.dst(target.getPosition()) > weapon.getRange()-10)) {
     * moveToDst(dt); - запускаем метод перемещения
     * <p>
     * //ЕСЛИ В СОСТОЯНИИ АТАКА И ЦЕЛЬ попрежнему в РАДИУСЕ ВИДИМОСТИ:
     * если ты в состоянии АТАКИ и расстояние до героя меньше радиуса атаки который берем у оружия то:
     * if (state == State.ATTACK && this.position.dst(target.getPosition()) < weapon.getRange()) {
     * attackTime += dt; - атактайм накапливается
     * if (attackTime > weapon.getSpeed()) { //скорость атаки сравниваем с течением времени // OLDif (attackTime > 0.3f) { - если атак тайм больше 3 секунд(каждые 3 сек)
     * attackTime = 0.0f; - сбрасывать атактайм
     * if (type == Type.MELEE) { - если тип персонажа БЛИЖНИЙ БОЙ:
     * target.takeDamage(this, 1); мы наносим урон 1
     * if (type == Type.RANGED) { - если тип СТРЕЛОК: среляем прожектилем(вызываем мтеод сетап прожектиля передаем ему владельца - себя)
     * gc.getProjectilesController().setup(this, position.x, position.y, target.getPosition().x, target.getPosition().y);
     * <p>
     * * каждый раз когда персонаж перемещается его ОКРУЖНОСТЬ должна следовать за ним:
     * * area.setPosition(position.x, position.y - 20)
     *
     * @param dt
     */
    public void update(float dt) {
        lifetime += dt;
        //ЕСЛИ ТЫ В СТОСТОЯНИ АТАКИ:
        if (state == State.ATTACK) {
            dst.set(target.getPosition()); //установим дст в цель
        }

        //ДВИЖЕНИЕ ДЛЯ ОПРЕЛЕННЫХ СТАТУСОВ
        if (state == State.MOVE || state == State.RETREAT || (state == State.ATTACK && this.position.dst(target.getPosition()) > weapon.getRange() - 10)) {
            moveToDst(dt);
        }

        //ЕСЛИ В СОСТОЯНИИ АТАКА И ЦЕЛЬ попрежнему в РАДИУСЕ атаки:
        if (state == State.ATTACK && this.position.dst(target.getPosition()) < weapon.getRange()) { //
            attackTime += dt;
            if (attackTime > weapon.getSpeed()) { //скорость атаки сравниваем с течением времени
                attackTime = 0.0f;
                if (weapon.getType() == Weapon.Type.MELEE) { //наносим урон без прожектиля, если оружие в руках для ближнего боя =
                    target.takeDamage(this, weapon.generateDamage());
                }
                if (weapon.getType() == Weapon.Type.RANGED && target != null) { // стреляем прожектилем, если оружие в руках = дальнобойное и цель еще есть
                    gc.getProjectilesController().setup(this, position.x, position.y, target.getPosition().x, target.getPosition().y, weapon.generateDamage());
                }
            }
        }
    }


    /**
     * ОБЩИЙ МЕТОД ПЕРЕМЕШЕНИЯ ПЕРСОНАЖА
     * ВЫнесли в общий класс движение монстра и героя:
     * ветор скорости=:
     * tmp.set(dst).sub(position).nor().scl(speed);
     * запоминаем старую позицию=:
     * tmp2.set(position);
     * если позишин больше тогда он двигается в сторону назначения dst:
     * if (position.dst(dst) > speed * dt) {
     * position.mulAdd(tmp, dt);
     * иначе он уставнавливается в позицию назначения  чтоб не дергался:
     * } else {
     * position.set(dst);
     * если мы добрались до точки назначения то стейт меняем на бездействие
     * и мы перестанем делать проверки что нам надо кудато бежать: пришли и стоим на месте
     * state = State.IDLE;
     * сделаем так чтоб персонаж не мог зайти в стену, перед перемещением проверяем что не стена:
     * после первоначального перемещения - если клетка не проходима?:
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
     *
     * @param dt
     */
    public void moveToDst(float dt) {
        tmp.set(dst).sub(position).nor().scl(speed);
        tmp2.set(position); //запоминаем старую позицию
        walkTime += dt; // для включения анимации, если перемешаем то ногами шевелит

        if (position.dst(dst) > speed * dt) { //перемещение в штатном режиме
            changePosition(position.x + tmp.x * dt, position.y + tmp.y * dt);
        } else {
            changePosition(dst); //перемешение когда путь мешьше шага
            state = State.IDLE;
        }
        if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) { //если вдруг мы влипли в стену
            changePosition(tmp2.x + tmp.x * dt, tmp2.y); //плывем по х
            if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {//если и по х мы попадаем в сткну
                changePosition(tmp2.x, tmp2.y + tmp.y * dt);//плывем по у
                if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) { //если опять влипли то стоим
                    changePosition(tmp2);
                }
            }
        }
    }

    /**
     * lastAttacker = attacker; - если мы получаем урон то нужно указать от кого мы получаем урон на случай возможноно бегства в случае малого здоровья
     * метод нанесения урона
     * если здоровье  меньше 0 то запускаем метод ондеад у персонажа
     *
     * @param attacker
     * @param amount
     * @return
     */
    public boolean takeDamage(GameCharacter attacker, int amount) {
        lastAttacker = attacker;
        hp -= amount;
        if (hp <= 0) {
            onDeath();
            return true;
        }
        return false;
    }

    /**
     * сброс статуса атаки в случае если цель уже погибла
     * останавливаемся - дст = позиция
     * статус=бездействие
     * цель = ноль
     */
    public void resetAttackState() {
        dst.set(position);
        state = State.IDLE;
        target = null;
    }

    /**
     * когда любой пперсонаж погибает мы должны пройти по всем персонажам и скитнуть цель если это она и была
     * сам убиваемый успокаивает всех кто на его охотился
     * сброс у всх персонажей активной цели если она умерла
     * обходим всех персонажей:
     * for (int i = 0; i < gc.getAllCharacters().size(); i++) {
     * GameCharacter gameCharacter = gc.getAllCharacters().get(i);
     * если цель этого персонажа была та что вызвала этот метод то:
     * if (gameCharacter.target == this) {
     * сбросить статус атаки
     * gameCharacter.resetAttackState();
     */
    public void onDeath() {
        for (int i = 0; i < gc.getAllCharacters().size(); i++) {
            GameCharacter gameCharacter = gc.getAllCharacters().get(i);
            if (gameCharacter.target == this) {
                gameCharacter.resetAttackState();
            }
        }
    }
}
