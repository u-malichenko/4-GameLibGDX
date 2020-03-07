package com.geekbrains.rpg.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Projectile implements Poolable {//делаем прожектиль пулабл чтоб создавать пулы оных
    private TextureRegion textureRegion;
    private Vector2 position;
    private Vector2 velocity;
    private boolean active;

    /**
     * геттер для прожектиля, получаем его координаты
     * нужно для проверки попадания стрелы в монстра
     * @return
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * метд который мы обязаны создать так как прожектилю сделали пулабл инетрфейс(он там написан)
     * @return
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * конструктор пустой- создаем болванки (не нужно прокидывать атлас и ни чего иного)
     * это нужно для того чтоб не париться с созданием newObject в конструкторе прожектилей,
     * там мы ему не смогли бы передать все нужные свойства,
     * а нам нужно его просто создать а все свойства мвы пропишем ему в блоке setup ниже
     */
    public Projectile() { //создаем абсолютно пустые объекты
        this.textureRegion = null; //когда создаем снаряд должны сказать как он выглядит(их может бытть много разных видов)
        this.position = new Vector2(0, 0); //позиция
        this.velocity = new Vector2(0, 0); //скорость
        this.active = false; //активность
    }

    /** ВКЛЮЧИТЬ ПРОЖЕКТИЛЬ
     * пробрасываем сюда регион текстур! разные снаряды будут требовать рзные текстуры
     * посему передаем регион (передем его из контроллера
     * проверка на активность убрана так как все идет через контроллер (стоит снаружи)
     */
    public void setup(TextureRegion textureRegion, float x, float y, float targetX, float targetY) {
        this.textureRegion = textureRegion;
        this.position.set(x, y);
        this.velocity.set(targetX, targetY).sub(x, y).nor().scl(800.0f);
        this.active = true;
    }

    /**
     * ДЕАКТИВИРОВТАЬ ПРОЖЕКТИЛЬ
     */
    public void deactivate() {
        active = false;
    }

    /** НАРИСОВАТЬ
     * убираем тут проверку на активность так как есть теперь контроллер - он сам это делает
     * так как мы ходим только по списку активных элементов
     * @param batch
     */
    public void render(SpriteBatch batch) {
        batch.draw(textureRegion, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, velocity.angle());
    }

    /**ОБНОВИТЬ
     * проверку на активность в нутри самого прожектиля вообще везде убираем
     * ак кк она идет вконтроллере и тут просто не нужна чтоб не тратить время
     * @param dt
     */
    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if (position.x < 0 || position.x > 1280 || position.y < 0 || position.y > 720) {
            deactivate();
        }
    }
}
