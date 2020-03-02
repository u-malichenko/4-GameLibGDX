package com.geekbrains.rpg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    private Texture texture; //картинка героя
    private Vector2 position; //две координаты в векторе2 две компоненты х и у сильно упрощает работу с векторами есть методы сложени я ипрочие
    private float speed; //скорость в пикселях в секунду всех скоростей

    public Hero() {
        this.texture = new Texture("hero.png"); //герой сам себя рисует
        this.position = new Vector2(100, 100); // его позиция при инициализации
        this.speed = 100.0f; // скорость героя
    }

    /**
     * просим его нарисоваться на экране прокинув ему ссылку на бач
     *
     * @param batch прокидываем ссылку на бач
     */
    public void render(SpriteBatch batch) {
        //для вращения использыуем перегрузку
        // originXY - якорь относительно которого все делается
        // width(h) - изменять размер длинны и высоты картинки
        // scaleXY - масштаб 1 = исходный мастшаб
        // rotation -  поворот в грудусах
        // srcXY -  когда на текстуре много картинок координаты этого кусочка
        // srcWidth(H) -  длинна и выстота этого кусочка выпиливаемого квадратика
        // flipXY - отзеркаливание картинки
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 1, 1, 0, 0, 0, 64, 64, false, false);
        // бач нарисуй мою картинку нарисовать героя на позиции х у
        // возможны многие перегрузки этоо метода примерно 10
        //х-32 - отрисовать картинку от центра 64пикселей левее и ниже на половину размера картинки
    }

    /**
     * выполнение игоровой логики для персонажа тут
     *
     * @param dt время последней отрисовки
     */
    public void update(float dt, Pointer pointer) {
        if (position.x < pointer.getPointerPosition().x) {
            position.x += speed * dt;
        }
        if (position.x > pointer.getPointerPosition().x) {
            position.x -= speed * dt;
        }

        if (position.y < pointer.getPointerPosition().y) {
            position.y += speed * dt;
        }
        if (position.y > pointer.getPointerPosition().y) {
            position.y -= speed * dt;
        }

//        //модуль инпут - ввода - а зажата ли какая либо кнопка?
//        if (Gdx.input.isKeyPressed(Input.Keys.A)) { //если зажата кнопка А
//            if (position.x < 32) {
//                position.x = 32;
//            } else {
//                position.x -= speed * dt; // то тогда мы хотим чтоб координата по х уменьшалась по формуле скорость * на дельта тайм
//                // персонаж идет в левую сторону
//            }
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
//            if (position.x > 1280 - 32) {
//                position.x = 1280 - 32;
//            } else {
//                position.x += speed * dt; //персонадж идет в правую тсорону
//            }
//        }
//        //чтоб двигался в верх нам нужно тчоб координата у увеличивалась, аналогично наоборот уменьшалась - в низ
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
//            if (position.y < 32) {
//                position.y = 32;
//            } else {
//                position.y -= speed * dt; //двигатся вниз
//            }
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//            if (position.y > 720-32) {
//                position.y = 720-32;
//            } else {
//                position.y += speed * dt; //двигаться в верх
//            }
//        }
//        //по диагонали срабатывает пара ифов
    }
}