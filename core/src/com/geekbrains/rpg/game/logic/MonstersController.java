package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.geekbrains.rpg.game.logic.utils.ObjectPool;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class MonstersController extends ObjectPool<Monster> {
    private GameController gc;
    private float innerTimer;
    private float spawnPeriod;

    @Override
    protected Monster newObject() {
        return new Monster(gc);
    }

    public MonstersController(GameController gc, int initialCount) {
        this.gc = gc;
        this.spawnPeriod = 30.0f;
        for (int i = 0; i < initialCount; i++) {
            getActiveElement().generateMe();
        }
    }

    public void update(float dt) {
        innerTimer += dt;
        if (innerTimer > spawnPeriod) {
            innerTimer = 0.0f;
            getActiveElement().generateMe();
        }
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
