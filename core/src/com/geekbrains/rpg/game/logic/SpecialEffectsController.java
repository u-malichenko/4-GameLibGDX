package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.geekbrains.rpg.game.logic.utils.ObjectPool;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class SpecialEffectsController extends ObjectPool<SpecialEffect> {
    private TextureRegion[][] texturesSwordSwing; // массив текстур

    @Override
    protected SpecialEffect newObject() {
        return new SpecialEffect();
    }

    public SpecialEffectsController() {//подгружает и нарезает текстуры
        this.texturesSwordSwing = new TextureRegion(Assets.getInstance().getAtlas().findRegion("swinganim502")).split(50, 50);
    }

    public void setupSwordSwing(float x, float y, float angle) { //прокидывает в создаваемые объекты ссылку на какуюто строку
        getActiveElement().setup(texturesSwordSwing[MathUtils.random(0, 5)], x, y, angle);
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).render(batch, null);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
