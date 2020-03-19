package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.geekbrains.rpg.game.logic.utils.ObjectPool;

public class BonusController extends ObjectPool<Bonus> {
    private GameController gc;
    protected float walkTime; //таймер для анимации
    protected float timePerFrame;  //скорость смены кадра анимации

    @Override
    protected Bonus newObject() {
        return new Bonus(gc);
    }

    public BonusController(GameController gc) {
        this.gc = gc;
        this.timePerFrame = 0.2f; //скорость смены кадра анимации
    }

    /**
     * указание позиции - передаем х у public void setup(float x, float y) {
     *        Weapon.Type type = Weapon.Type.MELEE; //по умолчанию оружие ближнебойное выпадает
     *         float range = 60.0f;
     * проверяем рандом 40% меняем оружие на дальнее
     *         if (MathUtils.random(100) < 40) {
     *             type = Weapon.Type.RANGED;
     *             range = 160.0f;
     *         }
     * создаем оружие
     *         w.setup(type, "Weapon", MathUtils.random(1, 4), maxDamage, 0.4f, range);
     * задаем оружию текущую позицию:
     *         w.setPosition(x, y);
     * @param x
     * @param y
     */
    public void setup(float x, float y, int coins) {
        Bonus bonus = getActiveElement();
        Bonus.Type type = Bonus.Type.HEALTH;
        if (MathUtils.random(100) < 40) {
            type = Bonus.Type.COINS;
        }
        bonus.setup(type, "Bonus", coins);
        bonus.setPosition(x, y);
    }

    public void update(float dt) {
        walkTime += dt; // для включения анимации
        checkPool();
    }

    public int getCurrentFrameIndex(int l) {
        return (int) (walkTime / timePerFrame) % l;
    }
}
