package com.geekbrains.rpg.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.geekbrains.rpg.game.screens.ScreenManager;

public class GeekRpgGame extends Game {
    private SpriteBatch batch;

    // Домашнее задание:
    // - Разбор кода и пишите какие вопросы возникли
    // - Добавить класс Weapon (оружие) и раздать каждому персонажу по оружию
    // Оружие определяет тип бойца - ближний/дальний бой. Свойства оружия:
    // дальность атаки, урон, скорость атаки
    // - * Из монстров может выпадать оружие (рисуем картинку оружия), которое
    // подбирается как монстрами, так и героем, заменяя имеющееся

    /*
    Валентин Изотов
​- У всех персонажей (герой, монстр) есть опыт, жизни, стартовый урон, стартовая броня (0), стартовая скорость.
    Валентин Изотов
​- у всех персонажей есть ячейки шмота: оружие, нагрудник, штаны, сапоги. шлем, перчатки (пустые по умолчанию)
    Валентин Изотов
​- все могут подбирать любой шмот, но можно иметь только одну шмотку каждого вида
    Валентин Изотов
​- у шмота есть рандомные статы с определенной вероятностью - при смерти выпадает все
    Валентин Изотов
​- если монстр натыкается на шмотку. то подбирает ее, если на нем шмотка, то он ее сравнивает и надевает лучшую, а снятую оставляет
    Валентин Изотов
​- монсты разных видов враждуют - если монстр убил, другого, то он получает за него опыт, сравнивает шмотки, одевает лучшее. забирает золото
    Валентин Изотов
​- если монстр убил героя, то он получает за него опыт, сравнивает шмотки, одевает лучшее. забирает золото
     */

    @Override
    public void create() {
        batch = new SpriteBatch();
        ScreenManager.getInstance().init(this, batch);
        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float dt = Gdx.graphics.getDeltaTime();
        getScreen().render(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}