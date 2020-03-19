package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.Consumable;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.logic.utils.Poolable;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class Bonus implements MapElement, Poolable, Consumable {
    public enum Type {
        HEALTH, COINS
    }


    private GameController gc;
    private TextureRegion[][] textures;
    private Type type;
    private String title;
    private Vector2 position;
    private int amount;
    private boolean active;


    public Vector2 getPosition() {
        return position;
    }

    @Override
    public int getCellX() {
        return (int) (position.x / 80);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / 80);
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }


    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    /**
     * при создании задаем позицию по умолчанию
     */
    public Bonus(GameController gc) {
        this.gc = gc;
        this.amount = 0;
        this.position = new Vector2(0, 0);
    }

    public Bonus(Type type, String title) {
        this.type = type;
        this.title = title;
    }


    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * если персонаж потребляет это оружие то персонажу задаем оружие равное себе(оружию) -gameCharacter.setWeapon(this);
     * а себя деактивируем active = false;
     *
     * @param gameCharacter
     */
    @Override
    public void consume(GameCharacter gameCharacter) {
        if (type == Type.HEALTH) {
            gameCharacter.setHealth();
        } else {
            gameCharacter.addCoins(MathUtils.random(10));
        }
        active = false;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(textures[0][gc.getBonusController().getCurrentFrameIndex(textures[0].length)], position.x - 32, position.y - 32);
    }

    public void setup(Type type, String title, int coins) {
        this.type = type;
        //выбираем нужную текстуру  в зависимости от дальности
        if (type == Type.HEALTH) {
            textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("heart60")).split(60, 60);
        } else {
            textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("coin60")).split(60, 60);
            this.amount += coins;
        }
        this.title = title;
        this.active = true; //активируем bonus
    }

    //TODO
    public static Bonus createBonusHealth() {
        return new Bonus(Type.HEALTH, "Health");
    }

    public static Bonus createBonusCoins() {
        return new Bonus(Type.COINS, "Coins");
    }
}
