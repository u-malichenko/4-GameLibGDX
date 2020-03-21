package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.Poolable;
import com.geekbrains.rpg.game.screens.utils.Assets;

/**
 * должен быть имплементс implements Poolable
 * <p>
 * * Мы добились:
 * *              логика обработки состояний живет только в GameCharacter
 * *              а переход из состояния в состояние живет в кадом конкретном виде персонажей МОнстр Герой
 * <p>
 * у монстра есть свои мозги - бот
 * он ллибо выбирает состояние случайно из двух случайных стоять или бежать в рандом
 * если он увиддел в зоне видимости персонажа то начинает атаковать его - догоняет и начинает стрелять
 */
public class Monster extends GameCharacter implements Poolable {
    private StringBuilder strBuilder;

    /**
     * проверка активности монстра исходя из его здоровья
     * если его здоровье больше чем 0 тогда тру активный
     * отдельного метода деактивации для его не требуется
     *
     * @return
     */
    @Override
    public boolean isActive() {
        return hp > 0;
    }

    /**
     * грузим пачку текстур распиливаем текстуру на 8 регионов
     * метод сплит режет на кусочки 60*60 получаем двумерный массив 8 столбцов и одна строка:
     * this.texture = new TextureRegion(Assets.getInstance().getAtlas().findRegion("dwarf60")).split(60,60);
     * если рандом выдал до 30
     * if(MathUtils.random(100)< 30){
     * выдать оружие дальнобойное:
     * this.weapon = Weapon.createSimpleRangedWeapon();
     * } else {
     * иначе выдать ближнебойное оружие:
     * this.weapon = Weapon.createSimpleMeleeWeapon();
     *
     * @param gc
     */
    public Monster(GameController gc) {
        super(gc, 20, 100.0f);
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("dwarf")).split(60, 60);
        this.changePosition(800.0f, 300.0f);
        this.dst.set(this.position);
        this.visionRadius = 160.0f;
        this.strBuilder = new StringBuilder();
        if (MathUtils.random(100) < 50) {
            this.weapon = Weapon.createSimpleRangedWeapon();
        } else {
            this.weapon = Weapon.createSimpleMeleeWeapon();
        }

    }

    /**
     * метод генерации нового монстра нужен дял того тчоб каждые 30 сек
     * используется в MonstersController. update . getActiveElement().generateMe();
     * задаем рандоммную позицию, пока не поподем на свободное место
     * do {
     * changePosition(MathUtils.random(0, 1280), MathUtils.random(0, 720));
     * делаем дувайл (чтоб не влипать) до тех пор пока тыаемся в стену, повторяем цикл::
     * } while (!gc.getMap().isGroundPassable(position));
     * заполнияем здоровье hp
     */
    public void generateMe() {
        do {
            changePosition(MathUtils.random(0, 1280), MathUtils.random(0, 700));
        } while (!gc.getMap().isGroundPassable(position));
        hpMax = 10;
        hp = hpMax;
    }

    /**
     * нужно вызывать родительский метод так как там прописано что у всех кто сюда метил сбрасывается цель и атака
     * если монстр умирает то выподает оружие:
     * gc.getWeaponsController().setup(position.x,position.y);
     */
    @Override
    public void onDeath() {
        super.onDeath();
        if (MathUtils.random(100) < 50) {
            gc.getBonusController().setup(position.x, position.y, this.getCoins());
        } else {
            gc.getWeaponsController().setup(position.x, position.y);
        }
    }

    /**
     * рисуем текстуру индекс картнки берем в методом из GameCharacter
     * batch.draw(textures[0][getCurrentFrameIndex()], position.x -
     * //для разворота анимации: так же нужно делать и в герое
     * если наш персонаж идет вправо:
     * if (dst.x > position.x){ , точка dst назначения справа
     * if(currentRegion.isFlipX()){ /и уже он флипнут по х а наш регион сейчас отзеркален
     * currentRegion.flip(true,false);//разворот x развернись и ссмотри вправо
     * }else { иначе если надо идти влево
     * иначе        if(!currentRegion.isFlipX()){ //и он НЕ флипнут по х а он не отзеркален влевую сторону
     * currentRegion.flip(true,false); //флипаем по х отзеркаливаем
     * <p>
     * textures[0] - отвечает за строки массива где должны лежать разне анимации - удар отскок и прочие ее нужно меннять в зависимости от действия:
     * ВИД анаимации:  TextureRegion currentRegion = textures[0][getCurrentFrameIndex()];
     * <p>
     * прячем полоску здоровья если она целая:
     * if(hp<hpMax){
     * batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
     *
     * @param batch
     * @param font
     */
    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        TextureRegion currentRegion = textures[0][getCurrentFrameIndex()];
        //для разворота анимации:
        if (dst.x > position.x) {
            if (currentRegion.isFlipX()) {
                currentRegion.flip(true, false);
            }
        } else {
            if (!currentRegion.isFlipX()) {
                currentRegion.flip(true, false);
            }
        }
        batch.draw(currentRegion, position.x - 30, position.y - 30, 30, 30, 60, 60, 2, 2, 0);
        if (hp < hpMax) {
            batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
            strBuilder.setLength(0);
            strBuilder.append(hp).append("\n");
            font.draw(batch, strBuilder, position.x - 30, position.y+42);
        }
    }

    /**
     * в ПЕрсонажах будут правила смены состояний а то как эти состояния обрабатываются будут в базовом классе(GameCharacter)
     * тоесть по сути и герои и монстры ведут себя одинаково а как меняются их состояния зависит либо от мозгов бота либо от нашей мышки
     * <p>
     * stateTimer -= dt; - таймер состояний с каждым разом убывает
     * <p>
     * СОТОЯНИЕ СПОКОЙСТВИЯ:
     * если стейт таймер ==0         if (stateTimer < 0.0f) { то монстру нужно выбрать какое либо действие
     * если монстр хотел атаковать а время у его вышло:
     * if (state == State.ATTACK) {
     * target = null;то мишень его должна быть сброшена
     * <p>
     * если ни чего не рпоизошло (для вызова нового стейтмента то мы вызываем рандомно стоять либо идти в рандомную точку:
     * state = State.values()[MathUtils.random(0, 1)];
     * если он выбрат потопать в рандом- направляем его туда:
     * if (state == State.MOVE) {dst.set(MathUtils.random(1280), MathUtils.random(720));
     * он должен это делать в течении случайного времени, задаем его:
     * stateTimer = MathUtils.random(2.0f, 5.0f); минимум и максимум
     * <p>
     * <p>
     * СОСТОЯНИЕ АТАКИ:
     * если стейт не равен убегающий И герой находится в радиусе поражения то :
     * if (state != State.RETREAT && this.position.dst(gc.getHero().getPosition()) < visionRadius) {
     * state = State.ATTACK; - меняем стейт на АТАК
     * target = gc.getHero(); - назначаем зель за конторой гонятся - герой
     * stateTimer = 10.0f; - время на это дело 10 сек есил герой выходит за пределы видимости то через 10 минут монстр перейдет в БЕЗДЕЙСТВИЕ:
     * <p>
     * СОСТОЯНИЕ УБЕГАТЬ если сильно ранен и если ты уже не в статусе убегающий):
     * if (hp < hpMax * 0.2 && state != State.RETREAT) {
     * state = State.RETREAT;
     * stateTimer = 1.0f;
     * минимум 100 пик максимум 200 = MathUtils.random(100, 200)
     * умноженное на знак Math.signum( либо +1 либо -1
     * разница между нашим положением и положением последнего атакующего = (position.x - lastAttacker.position.x)
     * dst.set(position.x + MathUtils.random(100, 200) * Math.signum(position.x - lastAttacker.position.x),
     * position.y + MathUtils.random(100, 200) * Math.signum(position.y - lastAttacker.position.y));
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
//        if (hp < hpMax * 0.2 && state != State.RETREAT) {
//            state = State.RETREAT;
//            stateTimer = 3.0f;
//            Vector2 healthPosition = new Vector2(gc.getBonusController().checkPositionHealthInActiveElement(this));
//            if(healthPosition.dst(this.getPosition())>10.0f){
//                dst.set(healthPosition);
//            }else{
//                dst.set(position.x + MathUtils.random(100, 200) * Math.signum(position.x - lastAttacker.position.x),
//                        position.y + MathUtils.random(100, 200) * Math.signum(position.y - lastAttacker.position.y));
//            }
//        }
        if (hp < hpMax * 0.2 && state != State.RETREAT) {
            state = State.RETREAT;
            stateTimer = 1.0f;
            dst.set(position.x + MathUtils.random(100, 200) * Math.signum(position.x - lastAttacker.position.x),
                    position.y + MathUtils.random(100, 200) * Math.signum(position.y - lastAttacker.position.y));
        }
    }
}
