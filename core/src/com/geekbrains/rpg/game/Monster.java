package com.geekbrains.rpg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * связка делается через центральный хаб - GameScreen
 * <p>
 * знает все о геймскрине
 * есть ствоя текстура
 */
public class Monster {
    private GameScreen gameScreen;
    private TextureRegion texture;
    private TextureRegion textureHp;
    private Vector2 position;
    private Vector2 dst;
    private Vector2 tmp;
    private float lifetime;
    private float speed;
    private int hp;
    private int hpMax;

    /**
     * геттер для получени япозиции монстра
     * нужно для проверки попадания прожектиля в монстра используется в GameScreen
     *
     * @return
     */
    public Vector2 getPosition() {
        return position;
    }

    public Monster(GameScreen gameScreen) { //он запоминает геймскрин
        this.gameScreen = gameScreen;
        this.texture = Assets.getInstance().getAtlas().findRegion("knight");
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
        this.position = new Vector2(800, 300);
        this.dst = new Vector2(position);
        this.tmp = new Vector2(0, 0);
        this.speed = 100.0f;
        this.hpMax = 30;
        this.hp = 30;
        this.lifetime = 0;
    }

    /**
     * НАНОСИТЬ УРОН МОНСТРУ
     * из здрровья монстра вычитаем то что получили
     *
     * @param amount - сколько утрона вычитать?
     */
    public void takeDamage(int amount) {
        hp -= amount;
        if (hp == 0) {
            reSetup();
        }
    }

    public void reSetup() {
        float x = (float) (Math.random() * 1100) + 30;
        float y = (float) (Math.random() * 650) + 30;
        position.set(x, y);
        hp = 30;
        gameScreen.getHero().addCoin();

    }

    /**
     * рисуем его и его здоровье
     * перекрашиваем монстра - batch.setColor(0.5f, 0.5f, 0.5f, 0.7f);
     * возвращаем исходный цвет бачу - batch.setColor(1, 1, 1, 1); иначе все будет такое
     *
     * @param batch
     */
    public void render(SpriteBatch batch) {
        batch.setColor(0.5f, 0.5f, 0.5f, 0.7f);
        batch.draw(texture, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        batch.setColor(1, 1, 1, 1);
        batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
    }

    /**
     * будет бегать за героем
     * связка делается через центральный хаб - GameScreen
     * tmp.set(gameScreen.getHero().getPosition()).sub(position).nor().scl(speed);
     * используем временный вектор  - изменим его - tmp.set(
     * точка назначения - gameScreen.getHero().getPosition()
     * вычитаем из точи назначения точку где сейчас монстр - sub(position).nor().scl(speed) нормируем ее и умножаем на скорость
     * перемещаем позицию монстра (сразу обе координаты х и у) на временный вектор с учетом дельта тайм position.mulAdd(tmp, dt);
     *
     * @param dt
     */
    public void update(float dt) {
        lifetime += dt;
        tmp.set(gameScreen.getHero().getPosition()).sub(position).nor().scl(speed);
        position.mulAdd(tmp, dt);
        if (position.dst(gameScreen.getHero().getPosition()) < 60 && lifetime > 3) {
            gameScreen.getHero().takeDamage(1);
            lifetime =0;
        }
    }
}
