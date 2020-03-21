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

public class Weapon implements MapElement, Poolable, Consumable {
    public enum Type {
        MELEE, RANGED
    }

    private TextureRegion texture;
    private Type type;
    private String title;
    private Vector2 position;
    private int minDamage;
    private int maxDamage;
    private float speed;
    private float range;
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

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int generateDamage() {
        return MathUtils.random(minDamage, maxDamage);
    }

    public float getSpeed() {
        return speed;
    }

    public float getRange() {
        return range;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    /**
     * при создании задаем позицию по умолчанию
     */
    public Weapon() {
        this.position = new Vector2(0, 0);
    }

    public Weapon(Type type, String title, int minDamage, int maxDamage, float speed, float range) {
        this.type = type;
        this.title = title;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.speed = speed;
        this.range = range;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * если персонаж потребляет это оружие то персонажу задаем оружие равное себе(оружию) -gameCharacter.setWeapon(this);
     * а себя деактивируем active = false;
     * @param gameCharacter
     */
    @Override
    public void consume(GameCharacter gameCharacter) {
        gameCharacter.setWeapon(this);
        active = false;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texture, position.x - 32, position.y - 32);
    }

    /**
     *выбираем нужную текстуру  в зависимости от типа
     *         if (type == Type.MELEE) {
     *             texture = Assets.getInstance().getAtlas().findRegion("weaponMelee");
     *         } else {
     *             texture = Assets.getInstance().getAtlas().findRegion("weaponRanged");
     * @param type
     * @param title
     * @param minDamage
     * @param maxDamage
     * @param speed
     * @param range
     */
    public void setup(Type type, String title, int minDamage, int maxDamage, float speed, float range) {
        this.type = type;
        if (type == Type.MELEE) {        //выбираем нужную текстуру  в зависимости от типа
            texture = Assets.getInstance().getAtlas().findRegion("weaponMelee");
        } else {
            texture = Assets.getInstance().getAtlas().findRegion("weaponRanged");
        }
        this.title = title;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.speed = speed;
        this.range = range;
        this.active = true; //активируем оружие
    }

    public static Weapon createSimpleRangedWeapon() {
        return new Weapon(
                Type.RANGED,
                "Bow",
                MathUtils.random(1, 2),
                MathUtils.random(3, 5),
                0.5f,
                150.0f
        );
    }

    public static Weapon createSimpleMeleeWeapon() {
        return new Weapon(
                Type.MELEE,
                "Sword",
                MathUtils.random(1, 2),
                MathUtils.random(3, 4),
                0.4f,
                60.0f
        );
    }
}
