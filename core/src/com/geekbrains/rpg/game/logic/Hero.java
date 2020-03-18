package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class Hero extends GameCharacter {
    private TextureRegion texturePointer;
    private int coins;
    private StringBuilder strBuilder;

    public void addCoins(int amount) {
        coins += amount;
    }

    /**
     * грузи свою картинку - распиливац ее на блоки 60 на 60 получится двумерныый массив размер будет пока 1 на 1
     *            this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("knight")).split(60,60);
     *      * Мы добились:
     *      *              логика обработки состояний живет только в GameCharacter
     *      *              а переход из состояния в состояние живет в кадом конкретном виде персонажей МОнстр Герой
     *      герой управляе мышкой может атаковать если ткнули в монстра то он станет целью для героя
     *      либо если ткнули в траву то трава станет дст для движения
     * даем персонажу оружие:
     *         this.weapon = Weapon.createSimpleMeleeWeapon();
     *
     * this.type = Type.RANGED; - герой типа дальнебойный
     * this.attackRadius = 150.0f; -радиус атаки героя
     * @param gc
     */
    public Hero(GameController gc) {
        super(gc, 80, 300.0f);
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("knight")).split(60,60);
        this.texturePointer = Assets.getInstance().getAtlas().findRegion("pointer");
        this.changePosition(100.0f, 100.0f);
        this.dst.set(position);
        this.strBuilder = new StringBuilder();
        this.weapon = Weapon.createSimpleMeleeWeapon();

    }

    /**
     *     * рисуем текстуру индекс картнки берем в методом из GameCharacter, анимация из 1 кадра
     *      * batch.draw(textures[0][getCurrentFrameIndex()], position.x -
     *
     * @param batch
     * @param font
     */
    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texturePointer, dst.x - 30, dst.y - 30, 30, 30, 60, 60, 0.5f, 0.5f, lifetime * 90.0f);
        batch.draw(textures[0][getCurrentFrameIndex()], position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        if(hp< hpMax) {
            batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
        }
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append("Class: ").append("Knight").append("\n");
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        strBuilder.append("Coins: ").append(coins).append("\n");
        strBuilder.append("Weapon: ").append(weapon.getTitle()).append(" [").append( weapon.getMinDamage()).append("-").append(weapon.getMaxDamage()).append("]\n");
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
     *         if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
     *  нужно понять куда мы тыкнули, в землю или в монстра, перебираем всех активных монстров:
     *             for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {
     *  получаем каждого монстра:
     *                 Monster m = gc.getMonstersController().getActiveList().get(i);
     *  если растояние от центра монстра до позиции клика мышки(Gdx.input.getX(), 720.0f - Gdx.input.getY()) меньше 30:
     *                 if (m.getPosition().dst(Gdx.input.getX(), 720.0f - Gdx.input.getY()) < 30.0f) {
     *  переходим в состояние атаки:
     *                     state = State.ATTACK;
     *  назначаем цель - данного монстра:
     *                     target = m;
     *  зыходим из метода вообще
     *                     return;
     *  если цикл закончился пустым значит это не монстр, а просто земля
     *  значит просто идем к этой точке:
     *             dst.set(Gdx.input.getX(), 720.0f - Gdx.input.getY());
     *  Меняем состояние на передвижение
     *             state = State.MOVE;
     *  цель нулевая
     *             target = null;
     * @param dt
     */
    @Override
    public void update(float dt) {
        super.update(dt);
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
        }
    }
}