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

    /**
     * нужен для преобразования оружия из типов оружия в ассецс csv файла
     */
    public enum WeaponClass {
        SWORD, MACE, AXE, SPEAR, BOW, CROSSBOW;

        /**
         * преобразовалка из строк в тип - класс оружия
         *
         * @param in
         * @return
         */
        public static WeaponClass fromString(String in) {
            switch (in) {
                case "SWORD":
                    return SWORD;
                case "MACE":
                    return MACE;
                case "AXE":
                    return AXE;
                case "SPEAR":
                    return SPEAR;
                case "BOW":
                    return BOW;
                case "CROSSBOW":
                    return CROSSBOW;
                default:
                    throw new RuntimeException("Unknown weapon class");
            }
        }
    }

    public enum Type {
        MELEE, RANGED;

        public static Type fromString(String in) {
            switch (in) {
                case "MELEE":
                    return MELEE;
                case "RANGED":
                    return RANGED;
                default:
                    throw new RuntimeException("Unknown weapon type");
            }
        }
    }

    private GameController gc;
    private TextureRegion texture;
    private WeaponClass weaponClass;
    private Type type;
    private String title;
    private Vector2 position; //позиция где мы хотим его нарисовать
    private int minDamage;
    private int maxDamage;
    private float speed;
    private float range;
    private boolean active;

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public int getCellX() {
        return (int) (position.x / Map.CELL_WIDTH);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / Map.CELL_HEIGHT);
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

    public float getSpeed() {
        return speed;
    }

    public float getRange() {
        return range;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setRange(float range) {
        this.range = range;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public int generateDamage() {
        return MathUtils.random(minDamage, maxDamage);
    }

    public void activate() {
        this.active = true;
    }

    public Vector2 getPosition() {
        return position;
    }

    public String getDps() {
        return String.valueOf((int) (maxDamage - minDamage) / 2.0f / speed);
    }

    /**
     * если персонаж потребляет это оружие то у персонажа берем оружие и копируем в его данные из себя(поднятого оружия);
     * а себя деактивируем active = false;
     *
     * @param gameCharacter
     */
    @Override
    public void consume(GameCharacter gameCharacter) {
        gameCharacter.getWeapon().copyFrom(this);
        active = false;
    }

    /**
     * при создании задаем позицию по умолчанию
     */
    public Weapon(GameController gc) {
        this.gc = gc;
        this.position = new Vector2(0, 0);
    }

    public Weapon(String line) {
        String[] tokens = line.split(",");
        this.weaponClass = WeaponClass.fromString(tokens[0].trim());
        this.type = Type.fromString(tokens[1].trim());
        this.title = tokens[2].trim();
        this.minDamage = Integer.parseInt(tokens[3].trim());
        this.maxDamage = Integer.parseInt(tokens[4].trim());
        this.speed = Float.parseFloat(tokens[5].trim());
        this.range = Float.parseFloat(tokens[6].trim());
        if (this.type == Type.MELEE) {
            texture = Assets.getInstance().getAtlas().findRegion("weaponMelee");
        } else {
            texture = Assets.getInstance().getAtlas().findRegion("weaponRanged");
        }
    }

    public void copyFrom(Weapon from) {
        this.weaponClass = from.weaponClass;
        this.type = from.type;
        this.title = from.title;
        this.minDamage = from.minDamage;
        this.maxDamage = from.maxDamage;
        this.speed = from.speed;
        this.range = from.range;
        this.texture = from.texture;
    }


    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texture, position.x - 32, position.y - 32,32,32,64,64, 0.8f, 0.8f, 0.0f);
    }
}
