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

    //TODO

    /**
     * варианты персонажей
     * ближний бой, лучник
     */
    public enum Type {
        MELEE, RANGED
    }

    protected GameController gc;

    protected TextureRegion texture;
    protected TextureRegion textureHp;

    protected Type type;//TODO
    protected State state;
    protected float stateTimer;
    protected float attackRadius;//TODO

    protected GameCharacter lastAttacker;
    protected GameCharacter target;

    protected Vector2 position;
    protected Vector2 dst;
    protected Vector2 tmp;
    protected Vector2 tmp2;

    protected Circle area;

    protected float lifetime;
    protected float visionRadius;
    protected float attackTime;//TODO
    protected float speed;
    protected int hp, hpMax;

    public int getCellX() {
        return (int) position.x / 80;
    }

    public int getCellY() {
        return (int) (position.y - 20) / 80;
    }

    public void changePosition(float x, float y) {
        if (x >= 0 && x <= 1280 || y >= 0 && y <= 720) {
            position.set(x, y);
            area.setPosition(x, y - 20);
        }
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
        this.target = null;
    }

    /**
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
     * если статус движение ИЛИ статус = УБЕГАТЬ ИЛИ АТАКА И цель находится в радиусе атаки-5(чтоб не ближний код)  то:
     * if (state == State.MOVE || state == State.RETREAT || (state == State.ATTACK && this.position.dst(target.getPosition()) > attackRadius - 5)) {
     * moveToDst(dt); - запускаем метод перемещения
     * <p>
     * //ЕСЛИ В СОСТОЯНИИ АТАКА И ЦЕЛЬ попрежнему в РАДИУСЕ ВИДИМОСТИ:
     * если ты в состоянии АТАКИ и расстояние до героя меньше радиуса атаки то:
     * if (state == State.ATTACK && this.position.dst(target.getPosition()) < attackRadius) {
     * attackTime += dt; - атактайм накапливается
     * if (attackTime > 0.3f) { - если атак тайм больше 3 секунд(каждые 3 сек)
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
        if (state == State.MOVE || state == State.RETREAT || (state == State.ATTACK && this.position.dst(target.getPosition()) > attackRadius - 5)) {
            moveToDst(dt);
        }

        //ЕСЛИ В СОСТОЯНИИ АТАКА И ЦЕЛЬ попрежнему в РАДИУСЕ атаки:
        if (state == State.ATTACK && this.position.dst(target.getPosition()) < attackRadius) {
            attackTime += dt;
            if (attackTime > 0.3f) {
                attackTime = 0.0f;
                if (type == Type.MELEE) { //наносим урон без прожектиля
                    target.takeDamage(this, 1);
                }
                if (type == Type.RANGED) { // стреляем прожектилем
                    gc.getProjectilesController().setup(this, position.x, position.y, target.getPosition().x, target.getPosition().y);
                }
            }
        }
        //area.setPosition(position.x, position.y - 20);
    }

    /**
     * ОБЩИЙ МЕТОД ПЕРЕМЕШЕНИЯ ПЕРСОНАЖА
     * ВЫнесли в общий класс движение монстра и героя:
     * ветор скорости=:
     *              tmp.set(dst).sub(position).nor().scl(speed);
     * запоминаем старую позицию=:
     *              tmp2.set(position);
     * если позишин больше тогда он двигается в сторону назначения dst:
     *              if (position.dst(dst) > speed * dt) {
     *                  position.mulAdd(tmp, dt);
     * иначе он уставнавливается в позицию назначения  чтоб не дергался:
     *              } else {
     *                  position.set(dst);
     * если мы добрались до точки назначения то стейт меняем на бездействие
     * и мы перестанем делать проверки что нам надо кудато бежать: пришли и стоим на месте
     *                      state = State.IDLE;
     * сделаем так чтоб персонаж не мог зайти в стену, перед перемещением проверяем что не стена:
     * после первоначального перемещения - если клетка не проходима?:
     * мапа говорит нам что земля в той точке где мы сейчас стоим не проходима:
     *          if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
     * то мы возвращаемся на точку назад:
     *          position.set(tmp2);
     * далее делаем плавное обтекание стены:
     * пробуем сместится только по х:
     *          position.add(tmp.x * dt, 0);
     * если после этого мы все еще в стене?:
     *          if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
     * то мы сбрасываем наше положение:
     *          position.set(tmp2);
     * и пробуем сместится по У:
     *          position.add(0, tmp.y * dt);
     * если после смещения по У мы все равно оказались в стене:
     *      if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
     * то стоим на месте и не щшевелимся:
     *      position.set(tmp2);
     *
     * @param dt
     */
    public void moveToDst(float dt) {
        tmp.set(dst).sub(position).nor().scl(speed);
        tmp2.set(position); //запоминаем старую позицию
        if (position.dst(dst) > speed * dt) {
            changePosition((position.x += tmp.x * dt),(position.y += tmp.y * dt));
            //position.mulAdd(tmp, dt);
        } else {

            changePosition(dst);
            //position.set(dst);
            state = State.IDLE;
        }
        if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
            position.set(tmp2);

            changePosition((position.x += tmp.x * dt),(position.y));
            //position.add(tmp.x * dt, 0);
            if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
                position.set(tmp2);

                changePosition((position.x),(position.y += tmp.y * dt));
                //position.add(0, tmp.y * dt);
                if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
                    position.set(tmp2);
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
