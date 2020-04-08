package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.Consumable;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.logic.utils.Poolable;

public class PowerUp implements Consumable, Poolable, MapElement {
    public enum Type {
        COINS(0), MEDKIT(1);

        int index;

        Type(int index) {
            this.index = index;
        }
    }

    private GameController gc;
    private Type type;
    private TextureRegion[][] textures;
    private Vector2 position;
    private Vector2 velocity;
    private float time;
    private boolean active;

    public Vector2 getPosition() {
        return position;
    }

    public PowerUp(GameController gc, TextureRegion[][] textures) {
        this.gc = gc;
        this.textures = textures; //одна ссылка всем, каждый выбирает свой регион
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(0.0f, 0.0f);
    }

    @Override
    public void consume(GameCharacter gameCharacter) {
        active = false;
        switch (type) {
            case MEDKIT:
                int restored = gameCharacter.restoreHP(0.1f); //востанавливаем здоровье на 10%
                gc.getInfoController().setupAnyAmount(gameCharacter.getPosition().x, gameCharacter.getPosition().y, Color.GREEN, "+", restored);
                break;
            case COINS:
                int amount = MathUtils.random(3, 10);
                gameCharacter.addCoins(amount);//TODO получать манет столько сколько там насобирал монстр+10
                gc.getInfoController().setupAnyAmount(gameCharacter.getPosition().x,gameCharacter.getPosition().y, Color.GOLD,"+", amount);
                break;
        }
    }

    public void setup(float x, float y) {
        position.set(x, y);
        time = 0.0f;
        velocity.set(MathUtils.random(-60,60),MathUtils.random(-60,60)); //чтоб летала с разной скоростью
        type = Type.values()[MathUtils.random(0,1)];//TODO тип выбираем рандомно, а лучше передавать
        active = true;
    }

    public void update(float dt) {
        time += dt;
        position.mulAdd(velocity,dt); //чтоб летал бонус
        if(time>4.0f){//если летают больше 4 секунд деактивируем бонус
            active = false;
        }
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(textures[type.index][0], position.x - 30, position.y - 30);//textures[type.index] -аптечка это нулоевой индекс монетка 1й индекс [0] - только первый кадр массива
    }

    @Override
    public int getCellX() {
        return (int) (position.x / Map.CELL_WIDTH);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / Map.CELL_HEIGHT);
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
