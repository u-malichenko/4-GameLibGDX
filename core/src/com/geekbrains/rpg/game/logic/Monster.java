package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.logic.utils.Poolable;
import com.geekbrains.rpg.game.screens.utils.Assets;


/**
 * монстр тож становится игровым персонажем и навследуется от GameCharacter
 */
public class Monster extends GameCharacter implements Poolable, MapElement {

    private float attackTime;
    private boolean active;

    /**
     * пробрасываем контроллер игры здоровье макс и скорость в конструктор родителя - GameCharacter:
     * super(gc, 20, 100.0f);
     * меняем позицию персонажа при инициализации:
     * this.changePosition(800.0f, 300.0f);
     *
     * @param gc - гейм контроллер, получаем при создании при инициализауии и пробрасываем к родителю в конструктор нужен для связи
     */
    public Monster(GameController gc) {
        super(gc, 20, 100.0f);
        this.changePosition(800.0f, 600.0f);
        this.dst.set(position);
    }

    /**
     * ЕСЛИ МОНСТРА УНИЧТОЖИЛИ то тогда деактивируем
     */
    @Override
    public void onDeath() {
        active = false;
    }

    /**
     * этот мтеод -  extends GameCharacte - implements MapElement
     *
     * @param batch
     * @param font
     */
    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.setColor(0.5f, 0.5f, 0.5f, 0.7f);
        batch.draw(texture, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        batch.setColor(1, 1, 1, 1);
        batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
    }

    /**
     * super.update(dt); - прокидываем в родительский метод ДТ
     * дст = dst.set(gc.getHero().getPosition()); - координаты героя на каждом кадре проверяются, монстр бегает за героем,
     * бедет двигаться к дст так же как герой двигается за указателем, раньше просто двигали к герою а теперь есть промежуточная переменная ДСТ к ней и бежит монстр а она = герю
     *
     * @param dt - разница времени с предыдущего обновления
     */
    public void update(float dt) {
        super.update(dt);
        //TODO проверять расстояние до героя если оно меньше 300 тогда идти иначе двигаться к рандомной точке на карте
        // если герой в зоне видимости то дст перемещать к герою
        // иначе бот бежит в последнюю точку дст, если он добежал до это й дст то тогда гененрирует себе новю случайную точку дст и идет теперь к ней

        if (this.position.dst(gc.getHero().getPosition()) < 300) {
            System.out.println(this.position.dst(gc.getHero().getPosition()));
            dst.set(gc.getHero().getPosition());
            if (this.position.dst(gc.getHero().getPosition()) < 40) {
                attackTime += dt;
                if (attackTime > 0.3f) {
                    attackTime = 0.0f;
                    gc.getHero().takeDamage(1);
                }
            }
        } else if (dst.equals(position)) {
            dst.set(MathUtils.random(0, 1280), MathUtils.random(0, 720));
        }

    }

    @Override
    public boolean isActive() {
        return active;
    }
}
