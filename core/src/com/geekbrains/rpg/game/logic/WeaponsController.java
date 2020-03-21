package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.math.MathUtils;
import com.geekbrains.rpg.game.logic.utils.ObjectPool;

public class WeaponsController extends ObjectPool<Weapon> {
    private GameController gc;

    @Override
    protected Weapon newObject() {
        return new Weapon();
    }

    public WeaponsController(GameController gc) {
        this.gc = gc;
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
    public void setup(float x, float y) {
        Weapon w = getActiveElement();
        int maxDamage = MathUtils.random(3, 4);
        for (int i = 0; i < 10; i++) {
            if (MathUtils.random(100) < 50 - i * 5) {
                maxDamage++;
            }
        }
        Weapon.Type type = Weapon.Type.MELEE; //по умолчанию оружие ближнебойное выпадает
        float range = 60.0f;
        if (MathUtils.random(100) < 50) {        //проверяем рандом 40% меняем оружие на дальнее
            type = Weapon.Type.RANGED;
            range = 160.0f;
        }
        w.setup(type, type.toString(), MathUtils.random(1, 4), maxDamage, 0.4f, range);        //создаем оружие
        w.setPosition(x, y);        //задаем оружию текущую позицию:
    }

    public void update(float dt) {
        checkPool();
    }
}
