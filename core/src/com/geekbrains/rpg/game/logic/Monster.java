package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.Poolable;
import com.geekbrains.rpg.game.screens.utils.Assets;

/**
 * должен быть имплементс implements Poolable
 *
 *      * Мы добились:
 *      *              логика обработки состояний живет только в GameCharacter
 *      *              а переход из состояния в состояние живет в кадом конкретном виде персонажей МОнстр Герой
 *
 *      у монстра есть свои мозги - бот
 *      он ллибо выбирает состояние случайно из двух случайных стоять или бежать в рандом
 *      если он увиддел в зоне видимости персонажа то начинает атаковать его - догоняет и начинает стрелять
 */
public class Monster extends GameCharacter implements Poolable {

    /**
     * проверка активности монстра исходя из его здоровья
     * если его здоровье больше чем 0 тогда тру активный
     * отдельного метода деактивации для его не требуется
     * @return
     */
    @Override
    public boolean isActive() {
        return hp > 0;
    }

    /**
     * this.attackRadius = 150.0f; - радиус видимости для атаки для гонки за героем
     * this.type = Type.RANGED; - монстр дальнебойныого типа
     * this.attackRadius = 150.0f; радиус атаки монстров
     * @param gc
     */
    public Monster(GameController gc) {
        super(gc, 20, 100.0f);
        this.texture = Assets.getInstance().getAtlas().findRegion("knight");
        this.changePosition(800.0f, 300.0f);
        this.dst.set(this.position);
        this.visionRadius = 160.0f;
        this.type = Type.values()[MathUtils.random(0, 1)];
        if(type.equals(Type.values()[0])){
            this.attackRadius = 60.0f;
        } else {
            this.attackRadius = 150.0f;
        }

    }

    /**
     * метод генерации нового монстра нужен дял того тчоб каждые 30 сек
     * используется в MonstersController. update . getActiveElement().generateMe();
     * задаем рандоммную позицию, пока не поподем на свободное место
     *        do {
     *             changePosition(MathUtils.random(0, 1280), MathUtils.random(0, 720));
     * делаем дувайл (чтоб не влипать) до тех пор пока тыаемся в стену, повторяем цикл::
     *         } while (!gc.getMap().isGroundPassable(position));
     *         заполнияем здоровье hp
     */
    public void generateMe() {
        do {
            changePosition(MathUtils.random(0, 1280), MathUtils.random(0, 720));
        } while (!gc.getMap().isGroundPassable(position));
        hpMax = 20;
        hp = hpMax;
    }
    /**
     * нужно вызывать родительский метод так как там прописано что у всех кто сюда метил сбрасывается цель и атака
     */
    @Override
    public void onDeath() {
        super.onDeath();
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.setColor(0.5f, 0.5f, 0.5f, 0.7f);
        batch.draw(texture, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        batch.setColor(1, 1, 1, 1);
        batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
    }

    /** в ПЕрсонажах будут правила смены состояний а то как эти состояния обрабатываются будут в базовом классе(GameCharacter)
     * тоесть по сути и герои и монстры ведут себя одинаково а как меняются их состояния зависит либо от мозгов бота либо от нашей мышки
     *
     * stateTimer -= dt; - таймер состояний с каждым разом убывает
     *
     * СОТОЯНИЕ СПОКОЙСТВИЯ:
     * если стейт таймер ==0         if (stateTimer < 0.0f) { то монстру нужно выбрать какое либо действие
     *          если монстр хотел атаковать а время у его вышло:
     *                 if (state == State.ATTACK) {
     *                 target = null;то мишень его должна быть сброшена
     *
     *          если ни чего не рпоизошло (для вызова нового стейтмента то мы вызываем рандомно стоять либо идти в рандомную точку:
     *                         state = State.values()[MathUtils.random(0, 1)];
     *             если он выбрат потопать в рандом- направляем его туда:
     *                          if (state == State.MOVE) {dst.set(MathUtils.random(1280), MathUtils.random(720));
     *                    он должен это делать в течении случайного времени, задаем его:
     *                             stateTimer = MathUtils.random(2.0f, 5.0f); минимум и максимум
     *
     *
     * СОСТОЯНИЕ АТАКИ:
     * если стейт не равен убегающий И герой находится в радиусе поражения то :
     *         if (state != State.RETREAT && this.position.dst(gc.getHero().getPosition()) < visionRadius) {
     *             state = State.ATTACK; - меняем стейт на АТАК
     *             target = gc.getHero(); - назначаем зель за конторой гонятся - герой
     *             stateTimer = 10.0f; - время на это дело 10 сек есил герой выходит за пределы видимости то через 10 минут монстр перейдет в БЕЗДЕЙСТВИЕ:
     *
     * СОСТОЯНИЕ УБЕГАТЬ если сильно ранен и если ты уже не в статусе убегающий):
     *           if (hp < hpMax * 0.2 && state != State.RETREAT) {
     *             state = State.RETREAT;
     *             stateTimer = 1.0f;
     *             минимум 100 пик максимум 200 = MathUtils.random(100, 200)
     *             умноженное на знак Math.signum( либо +1 либо -1
     *             разница между нашим положением и положением последнего атакующего = (position.x - lastAttacker.position.x)
     *             dst.set(position.x + MathUtils.random(100, 200) * Math.signum(position.x - lastAttacker.position.x),
     *                     position.y + MathUtils.random(100, 200) * Math.signum(position.y - lastAttacker.position.y));
     *
     *
     * @param dt
     */
    public void update(float dt) {
        super.update(dt);
        stateTimer -= dt;

        //состояние СПОКОЙСТВИЯ или МУВ
        if (stateTimer < 0.0f) {
            if (state == State.ATTACK) {
                target = null;
            }
            state = State.values()[MathUtils.random(0, 1)]; //либо постоять либо пойти
            if (state == State.MOVE) {
                dst.set(MathUtils.random(1280), MathUtils.random(720));
            }
            stateTimer = MathUtils.random(2.0f, 5.0f);
        }

        //состояние АТАКИ
        if (state != State.RETREAT && this.position.dst(gc.getHero().getPosition()) < visionRadius) {
            state = State.ATTACK;
            target = gc.getHero();
            stateTimer = 10.0f;
        }

        // стостояние УБЕГАТЬ, осталось мало жизней
        if (hp < hpMax * 0.2 && state != State.RETREAT) {
            state = State.RETREAT;
            stateTimer = 1.0f;
            dst.set(position.x + MathUtils.random(100, 200) * Math.signum(position.x - lastAttacker.position.x),
                    position.y + MathUtils.random(100, 200) * Math.signum(position.y - lastAttacker.position.y));
        }
    }
}
