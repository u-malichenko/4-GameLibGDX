package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.geekbrains.rpg.game.logic.utils.ObjectPool;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class PowerUpsController extends ObjectPool<PowerUp> {
    private TextureRegion[][] textures;
    private GameController gc;

    @Override
    protected PowerUp newObject() {
        return new PowerUp(gc, textures);
    }

    public PowerUpsController(GameController gc) {
        this.gc = gc;
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("powerUps")).split(60,60);
    }

    public void setup(float x, float y) {
        for (int i = 0; i < 3; i++) { //возможность выподает сразу до трех бонусов
            if(MathUtils.random(100)<20){
                getActiveElement().setup(x,y);
            }
        }
       // getActiveElement().setup(owner, textures, x, y, targetX, targetY, damage);
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
