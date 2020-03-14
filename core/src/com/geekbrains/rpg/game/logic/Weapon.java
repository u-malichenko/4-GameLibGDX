package com.geekbrains.rpg.game.logic;


// - Добавить класс Weapon (оружие) и раздать каждому персонажу по оружию
// Оружие определяет тип бойца - ближний/дальний бой. Свойства оружия:
// дальность атаки, урон, скорость атаки

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.List;

public abstract class Weapon {
    protected GameController gc;
    protected TextureRegion texture;
    protected List weaponList;
    protected Type type;
    protected float attackRadius;
    protected  int damage;
    protected int speedAttack;

    /**
     * варианты персонажей
     * ближний бой, лучник
     */
    public enum Type {
        MELEE, RANGED
    }

    public Weapon(GameController gc) {
        this.gc=gc;

    }

}
