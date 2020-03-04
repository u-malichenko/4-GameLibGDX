package com.geekbrains.rpg.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Target {
    private TextureRegion textureRegion;
    private Vector2 position;
    private float width;
    private float height;
    private int originX;
    private int originY;

    public Target(TextureAtlas atlas) {
        this.textureRegion = atlas.findRegion("apple");
        this.position = new Vector2(500, 500);
        this.width = 60;
        this.height = 60;
        this.originX = 30;
        this.originY = 30;
    }

    public void reSetup() {
        float x = (float) (Math.random() * 1100) + 30;
        float y = (float) (Math.random() * 650) + 30;
        position.set(x, y);

    }

    public void render(SpriteBatch batch) {
        batch.draw(textureRegion, position.x - 30, position.y - 30, originX, originY, width, height, 1, 1, 0);
    }

    public void update(Projectile projectile) {
        //1 если растояние между яблоком и центром стрелы+30 меньше растояния половина яблока значит попали
        //position.dst(pos); для рассчета растояния между объектами
        if (position.dst(projectile.getPosition()) < height / 2) {
            reSetup();
            projectile.deactivate();
        }
    }
}
