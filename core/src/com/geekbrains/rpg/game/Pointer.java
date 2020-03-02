package com.geekbrains.rpg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Pointer {
    private Texture texturePointer;
    private Vector2 pointerPosition;
    private float rt;

    public Pointer() {
        this.texturePointer = new Texture("pointer.png");
        this.pointerPosition = new Vector2(500, 500);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texturePointer, pointerPosition.x - 32, pointerPosition.y - 32, 32, 32, 64, 64, 1, 1, rt, 0, 0, 64, 64, false, false);
    }

    public void update(float dt) {
        //только для того чтоб поинтер крутился
        rt -= dt * 90.0f; //время сколько времени прошло с момента запуска приложения
        //проверяем нажатие мышки или пальца единичные клики если мы отследили что ктото тыкнул мышкой
        // то
        if (Gdx.input.justTouched()) {
            pointerPosition.set(Gdx.input.getX(), 720.0f - Gdx.input.getY()); // задаем нашей метке SEt новые х и у
            // Gdx.input.getX() - запрос координат мышки по х
            // 720.0f - Gdx.input.getY() высота экрана - у так как координаты мира декартовы(у вверх идет) а координаты мышки получаем оконные(у идет вниз)
            // соответсвенно высота окна минус координаты мышки 720-200=520
        }
    }

    public Vector2 getPointerPosition() {
        return pointerPosition;
    }
}
