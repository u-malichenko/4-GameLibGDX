package com.geekbrains.rpg.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private static int count = 0;
    private TextureRegion textureRegion;
    private Vector2 position;
    private Vector2 velocity;
    private float speed;
    private int index;
    private boolean active;
    private StringBuilder strBuilder;

    public Projectile(TextureAtlas atlas) {
        this.index = count++;
        this.textureRegion = atlas.findRegion("arrow");
        this.position = new Vector2(0, 0); //кординаты
        this.velocity = new Vector2(0, 0); //скорость
        this.speed = 800.0f;
        this.active = false; //либо активен либо нет
        this.strBuilder = new StringBuilder();

    }

    //метод для выстрела стрелой
    public void setup(float x, float y, float targetX, float targetY) {//из какой точки она вылетает, и куда мы хотим выпульнуть?
        if (!active) {//только если снаряд не активный
            position.set(x, y);//позиция объекта устанавливается в начальную точку
            velocity.set(targetX, targetY).sub(x, y).nor().scl(speed); // получаем вектор в назанчение нормируем его и множим на скорость
            active = true; //активировали снаряд
        }
    }

    public void deactivate() {
        active = false;
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        if (active) {//только если снаряд не активный
            strBuilder.setLength(0); //отчистить
            strBuilder.append(index); //не использовать конкатенацию только аппенд
            font.draw(batch, strBuilder, position.x, position.y + 20);
        }
    }

    public void render(SpriteBatch batch) {
        if (active) { //если объект активен, нарисуй
            batch.draw(textureRegion, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, velocity.angle());
        }
    }

    public void update(float dt) {
        if (active) { // если санаряд активен
            position.mulAdd(velocity, dt); //двигаем снаряд
            if (position.x < 0 || position.x > 1280 || position.y < 0 || position.y > 720) {
                deactivate(); //деактивация за размерами экрана
                //выпустить снаряд можно только если он не активен
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }
}
