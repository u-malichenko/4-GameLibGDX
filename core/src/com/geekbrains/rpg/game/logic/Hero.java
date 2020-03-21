package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class Hero extends GameCharacter {
    private TextureRegion texturePointer;
    private StringBuilder strBuilder;
    private float sleepTimer;

    /**
     * грузи свою картинку - распиливац ее на блоки 60 на 60 получится двумерныый массив размер будет пока 1 на 1
     * this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("knight")).split(60,60);
     * * Мы добились:
     * *              логика обработки состояний живет только в GameCharacter
     * *              а переход из состояния в состояние живет в кадом конкретном виде персонажей МОнстр Герой
     * герой управляе мышкой может атаковать если ткнули в монстра то он станет целью для героя
     * либо если ткнули в траву то трава станет дст для движения
     * даем персонажу оружие:
     * this.weapon = Weapon.createSimpleMeleeWeapon();
     * <p>
     * this.type = Type.RANGED; - герой типа дальнебойный
     * this.attackRadius = 150.0f; -радиус атаки героя
     *
     * @param gc
     */
    public Hero(GameController gc) {
        super(gc, 80, 300.0f);
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("knight")).split(60, 60);
        this.texturePointer = Assets.getInstance().getAtlas().findRegion("pointer");
        this.changePosition(100.0f, 100.0f);
        this.dst.set(position);
        this.strBuilder = new StringBuilder();
        this.weapon = Weapon.createSimpleMeleeWeapon();
        this.visionRadius = 170.0f;
        this.sleepTimer = 0;

    }

    /**
     * * рисуем текстуру индекс картнки берем в методом из GameCharacter, анимация из 1 кадра
     * * batch.draw(textures[0][getCurrentFrameIndex()], position.x -
     *
     * @param batch
     * @param font
     */
    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texturePointer, dst.x - 30, dst.y - 30, 30, 30, 60, 60, 0.5f, 0.5f, lifetime * 90.0f);
        batch.draw(textures[0][getCurrentFrameIndex()], position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        if (hp < hpMax) {
            batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
            strBuilder.setLength(0);
            strBuilder.append(hp).append("\n");
            font.draw(batch, strBuilder, position.x - 30, position.y+42);
        }
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append("Class: ").append("Knight").append("\n");
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        strBuilder.append("Coins: ").append(coins).append("\n");
        strBuilder.append("Weapon: ").append(weapon.getTitle()).append(" [").append(weapon.getMinDamage()).append("-").append(weapon.getMaxDamage()).append("]\n");
        strBuilder.append("sleepTimer: ").append(sleepTimer).append("\n");
        strBuilder.append("stateTimer: ").append(stateTimer).append("\n");
        strBuilder.append("State: ").append(state).append("\n");
        if (lastAttacker != null){
            strBuilder.append("lastAttacker: ").append(lastAttacker.name).append("\n");
        }
        if (target != null){
            strBuilder.append("target: ").append(target.name).append("\n");
        }
        font.draw(batch, strBuilder, 10, 710);
    }

    /**
     * нужно вызывать родительский метод так как там прописано что у всех кто сюда метил сбрасывается цель и атака
     */
    @Override
    public void onDeath() {
        super.onDeath();
        coins = 0;
        hp = hpMax;
    }

    /**
     * в ПЕрсонажах будут правила смены состояний а то как эти состояния обрабатываются будут в базовом классе(GameCharacter)
     * тоесть по сути и герои и монстры ведут себя одинаково а как меняются их состояния зависит либо от мозгов бота либо от нашей мышки
     * управляем только левой кнопкой
     * если нажата левая кнопка:
     * if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
     * нужно понять куда мы тыкнули, в землю или в монстра, перебираем всех активных монстров:
     * for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {
     * получаем каждого монстра:
     * Monster m = gc.getMonstersController().getActiveList().get(i);
     * если растояние от центра монстра до позиции клика мышки(Gdx.input.getX(), 720.0f - Gdx.input.getY()) меньше 30:
     * if (m.getPosition().dst(Gdx.input.getX(), 720.0f - Gdx.input.getY()) < 30.0f) {
     * переходим в состояние атаки:
     * state = State.ATTACK;
     * назначаем цель - данного монстра:
     * target = m;
     * зыходим из метода вообще
     * return;
     * если цикл закончился пустым значит это не монстр, а просто земля
     * значит просто идем к этой точке:
     * dst.set(Gdx.input.getX(), 720.0f - Gdx.input.getY());
     * Меняем состояние на передвижение
     * state = State.MOVE;
     * цель нулевая
     * target = null;
     * <p>
     * * в ПЕрсонажах будут правила смены состояний а то как эти состояния обрабатываются будут в базовом классе(GameCharacter)
     * * тоесть по сути и герои и монстры ведут себя одинаково а как меняются их состояния зависит либо от мозгов бота либо от нашей мышки
     * *
     * * stateTimer -= dt; - таймер состояний с каждым разом убывает
     * *
     * * СОТОЯНИЕ СПОКОЙСТВИЯ:
     * * если стейт таймер ==0         if (stateTimer < 0.0f) { то монстру нужно выбрать какое либо действие
     * *          если монстр хотел атаковать а время у его вышло:
     * *                 if (state == State.ATTACK) {
     * *                 target = null;то мишень его должна быть сброшена
     * *
     * *          если ни чего не рпоизошло (для вызова нового стейтмента то мы вызываем рандомно стоять либо идти в рандомную точку:
     * *                         state = State.values()[MathUtils.random(0, 1)];
     * *             если он выбрат потопать в рандом- направляем его туда:
     * *                          if (state == State.MOVE) {dst.set(MathUtils.random(1280), MathUtils.random(720));
     * *                    он должен это делать в течении случайного времени, задаем его:
     * *                             stateTimer = MathUtils.random(2.0f, 5.0f); минимум и максимум
     * *
     * *
     * * СОСТОЯНИЕ АТАКИ:
     * * если стейт не равен убегающий И герой находится в радиусе поражения то :
     * *         if (state != State.RETREAT && this.position.dst(gc.getHero().getPosition()) < visionRadius) {
     * *             state = State.ATTACK; - меняем стейт на АТАК
     * *             target = gc.getHero(); - назначаем зель за конторой гонятся - герой
     * *             stateTimer = 10.0f; - время на это дело 10 сек есил герой выходит за пределы видимости то через 10 минут монстр перейдет в БЕЗДЕЙСТВИЕ:
     * *
     * * СОСТОЯНИЕ УБЕГАТЬ если сильно ранен и если ты уже не в статусе убегающий):
     * *           if (hp < hpMax * 0.2 && state != State.RETREAT) {
     * *             state = State.RETREAT;
     * *             stateTimer = 1.0f;
     * *             минимум 100 пик максимум 200 = MathUtils.random(100, 200)
     * *             умноженное на знак Math.signum( либо +1 либо -1
     * *             разница между нашим положением и положением последнего атакующего = (position.x - lastAttacker.position.x)
     * *             dst.set(position.x + MathUtils.random(100, 200) * Math.signum(position.x - lastAttacker.position.x),
     * *                     position.y + MathUtils.random(100, 200) * Math.signum(position.y - lastAttacker.position.y));
     *
     * @param dt
     */
    @Override
    public void update(float dt) {
        super.update(dt);
        sleepTimer += dt;
        stateTimer -= dt;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {
                Monster m = gc.getMonstersController().getActiveList().get(i);
                if (m.getPosition().dst(Gdx.input.getX(), 720.0f - Gdx.input.getY()) < 30.0f) {
                    state = State.ATTACK;
                    target = m;
                    return;
                }
            }
            dst.set(Gdx.input.getX(), 720.0f - Gdx.input.getY());
            state = State.MOVE;
            target = null;
            sleepTimer = -3;
           //TODO stateTimer = 0;
        //если вермя ожидания вышло или нас атаковали то:
        } else if (sleepTimer > 0 ) { //|| lastAttacker != null || state != State.MOVE
            //включаем состояние АТАКИ если не убегаем и если уже не находимся в этом статусе:
            if (state != State.RETREAT && state != State.ATTACK) {
                //если нас атаковали и в зоне видимости то атакуем в ответ:
                if (lastAttacker != null && this.position.dst(lastAttacker.getPosition()) < visionRadius) {
                    state = State.ATTACK;
                    target = lastAttacker;
                    //иначе сами проверяем есть ли рядом монстр:
                }else if (gc.getMonstersController().checkVisionMonster(this)) {
                    //target назначается в методе проверки есил вернули тру
                    state = State.ATTACK;
                }
            } //если целей рядом нет то можно попробовать поправить здоровье:
            // стостояние УБЕГАТЬ, осталось мало жизней TODO бегать за сердцами
//            if (hp < hpMax * 0.2 && state != State.RETREAT) {
//                state = State.RETREAT;
//                stateTimer = 1.0f;
//                Vector2 healthPosition = new Vector2(gc.getBonusController().checkPositionHealthInActiveElement(this));
//                if(healthPosition.dst(this.getPosition())>10.0f){
//                    dst.set(healthPosition);
//                }else{
//                    dst.set(position.x + MathUtils.random(100, 200) * Math.signum(position.x - lastAttacker.position.x),
//                            position.y + MathUtils.random(100, 200) * Math.signum(position.y - lastAttacker.position.y));
//                }
//            } Bonus.Type.HEALTH

            //если здоровье не целое и на карте есть сердца:
            tmp = gc.getBonusController().checkPositionBonus(this, Bonus.Type.HEALTH);
            if (!tmp.epsilonEquals(position, 0.1f) && hp != hpMax) { //если точка вернулась совем НЕ рядом, и здоровье не =МАКС - значит бежим за сердцем!
                state = State.MOVE;
                dst.set(tmp);
            }else {
                //проверяем нет ли на карте монеток:
                tmp2 = gc.getBonusController().checkPositionBonus(this, Bonus.Type.COINS);
                if (!tmp2.epsilonEquals(position, 0.1f) ) { //если точка вернулась совем НЕ рядом, значит бежим за монетками!
                    state = State.MOVE;
                    dst.set(tmp2);
                } else {
                    //если точка вернулась рядом, значит делать нечего - бродяжничаем!
//                    state = State.values()[MathUtils.random(0, 1)]; //либо постоять либо пойти
//                        if (state == State.MOVE) {
//                            dst.set(MathUtils.random(1280), MathUtils.random(720));
//                        }
//                    sleepTimer = MathUtils.random(-1.0f, -3.0f); //либо идти либо стоять рандомное время
                }
            }
        }
    }
}