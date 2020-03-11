package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.geekbrains.rpg.game.logic.utils.ObjectPool;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class ProjectilesController extends ObjectPool<Projectile> {
    private TextureRegion projectileTextureRegion;

    @Override
    protected Projectile newObject() {
        return new Projectile();
    }

    public ProjectilesController() {
        this.projectileTextureRegion = Assets.getInstance().getAtlas().findRegion("arrow");
    }

    public void setup(float x, float y, float targetX, float targetY) {
        getActiveElement().setup(projectileTextureRegion, x, y, targetX, targetY);
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
