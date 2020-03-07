package com.geekbrains.rpg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * герой может работать с экраном игры для связи
 */
public class Hero {
    private GameScreen gameScreen;
    private TextureRegion texture;
    private TextureRegion texturePointer;
    private TextureRegion textureHp;
    private Vector2 position;
    private Vector2 dst;
    private Vector2 tmp;
    private float lifetime;
    private float speed;
    private int hp;
    private int hpMax;
    private StringBuilder strBuilder;
    private int coin;

    /**
     * гетер места где находится герой
     * нужно чтоб за героем бегал монстр используетс в GameScreen
     *
     * @return
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * пробрасываем герою ссылку на экран игры в которой он находится для связи (этот экран связывает все что у нас есть)
     * указываем через ассистента герою регион его текстуры //ищем регион а менеджере ресурсов в его аталсе
     * указываем через ассистентагерою регион текстуры указателя
     * указываем через ассистента герою регион его жизни
     * указываем нчальную позицию
     * указываем что позиция куда нужно передвигатся сейчас = начальной позиции
     * указываем временный вектор - для рассчетов
     * указываем скорость
     * указываем максимум жизни
     * указываем начальную жиззнь
     * создаем Стригбилдер для использования в написании текста на экране ГУИ
     *
     * @param gameScreen - ссылка на игру
     */
    public Hero(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.texture = Assets.getInstance().getAtlas().findRegion("knight");
        this.texturePointer = Assets.getInstance().getAtlas().findRegion("pointer");
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
        this.position = new Vector2(100, 100);
        this.dst = new Vector2(position);
        this.tmp = new Vector2(0, 0);
        this.speed = 300.0f;
        this.hpMax = 10;
        this.hp = 10;
        this.strBuilder = new StringBuilder();
        this.coin = 0;
    }

    /**НАНОСИТЬ УРОН герою
     * из здрровья монстра вычитаем то что получили
     * @param amount - сколько утрона вычитать?
     */
    public void takeDamage(int amount) {
        hp -= amount;
        if (hp==0){
            reSetup();
        }
    }

    public void reSetup() {
        float x = (float) (Math.random() * 1100) + 30;
        float y = (float) (Math.random() * 650) + 30;
        position.set(x, y);
        dst = position;
        hp = 10;
    }

    public void addCoin() {
        coin += (Math.random() * 10) + 3;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texturePointer, dst.x - 30, dst.y - 30, 30, 30, 60, 60, 0.5f, 0.5f, lifetime * 90.0f);
        batch.draw(texture, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append("Class: ").append("Knight").append("\n");
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        strBuilder.append("Money: ").append(coin).append("\n");
        font.draw(batch, strBuilder, 10, 710);
    }

    /**
     * gameScreen.getProjectilesController().setup(position.x, position.y, Gdx.input.getX(), 720.0f - Gdx.input.getY());
     * экран игры. дай мне ссылку на прожектиль контроллер. предоставь мне любую стрелу и выпусти ее с этими данными)
     * gameScreen. getProjectilesController().              setup(position.x, position.y, Gdx.input.getX(), 720.0f - Gdx.input.getY());
     *
     * @param dt
     */
    public void update(float dt) {
        lifetime += dt;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            dst.set(Gdx.input.getX(), 720.0f - Gdx.input.getY());
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            gameScreen.getProjectilesController().setup(position.x, position.y, Gdx.input.getX(), 720.0f - Gdx.input.getY());
            //экран игры. дай мне ссылку на прожектиль контроллер. предоставь мне любую стрелу и выпусти ее с этими данными)
        }
        tmp.set(dst).sub(position).nor().scl(speed); // вектор скорости
        if (position.dst(dst) > speed * dt) {
            position.mulAdd(tmp, dt);
        } else {
            position.set(dst);
        }
    }
}