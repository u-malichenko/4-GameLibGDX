package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.geekbrains.rpg.game.logic.utils.ObjectPool;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class MonsterController extends ObjectPool<Monster> {
    private float lifetime;
    private GameController gc;
    private TextureRegion texture;

    @Override
    protected Monster newObject() {
        return new Monster(gc);
    }

    public MonsterController(GameController gc) {
        this.lifetime = 28;
        this.gc = gc;
        this.texture = Assets.getInstance().getAtlas().findRegion("knight");
    }

    public void setup() {
        getActiveElement().setup(texture);
    }

    public void update(float dt) {
        lifetime += dt;
        if (lifetime > 30) {
            lifetime = 0;
            setup();
        }
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
