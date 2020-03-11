package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.geekbrains.rpg.game.screens.utils.Assets;

/**
 * герой является игровым персонажем инаследуется от абстрактного класса GameCharacter
 *
 */
public class Hero extends GameCharacter {
    private TextureRegion texturePointer;
    private int coins;
    private StringBuilder strBuilder;

    public void addCoins(int amount) {
        coins += amount;
    }

    /**
     * конструктор героя
     * super(gc, 10, 300.0f); отдали гейм контроллер, жизнь и скорость в конструктор абстрактного класса родителя GameCharacter
     * загружаем сами текстуру героя и поинтера:
     *         this.texture = Assets.getInstance().getAtlas().findRegion("knight");
     *         this.texturePointer = Assets.getInstance().getAtlas().findRegion("pointer");
     * меняем позицию персонажа при инициализации:
     *         this.changePosition(100.0f, 100.0f);
     * this.dst.set(position); дст = начальной позиции, при инициализации
     *
     * @param gc
     */
    public Hero(GameController gc) {
        super(gc, 10, 300.0f);
        this.texture = Assets.getInstance().getAtlas().findRegion("knight");
        this.texturePointer = Assets.getInstance().getAtlas().findRegion("pointer");
        this.changePosition(100.0f, 100.0f);
        this.dst.set(position);
        this.strBuilder = new StringBuilder();
    }

    /**
     * этот мтеод -  extends GameCharacte - implements MapElement
     * @param batch
     * @param font
     */
    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texturePointer, dst.x - 30, dst.y - 30, 30, 30, 60, 60, 0.5f, 0.5f, lifetime * 90.0f);
        batch.draw(texture, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append("Class: ").append("Knight").append("\n");
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        strBuilder.append("Coins: ").append(coins).append("\n");
        font.draw(batch, strBuilder, 10, 710);
    }

    /**
     * абстрактный класс родителя GameCharacter
     * нужен для различных действий в случае гибили разных персонажей
     * тут конкретно мы просто сбрасываем деньги в 0 и залечиваем в макс - так кк это герой
     * у монстров этот метод будет совсем другой
     */
    @Override
    public void onDeath() {
        coins = 0;
        hp = hpMax;
    }

    /**все поведние общее для персонажей выносим в суперский апдейт и потом просто его выполняем
     * super.update(dt); - прокидываем в родительский метод ДТ
     * @param dt - разница времени с предыдущего обновления
     */
    @Override
    public void update(float dt) {
        super.update(dt);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            dst.set(Gdx.input.getX(), 720.0f - Gdx.input.getY());
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            gc.getProjectilesController().setup(position.x, position.y, Gdx.input.getX(), 720.0f - Gdx.input.getY());
        }
    }
}