package com.geekbrains.rpg.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * private Texture texture; - есть текстура которую он всегда будет при себе держать
 */
public class LoadingScreen extends AbstractScreen {
    private Texture texture;

    /**
     * конструктор лоадинскрина
     * так как он унаследован от AbstractScreen мы передаем туда бач
     * Pixmap pixmap = new Pixmap( создаем массив пикселей разсером 1280*20
     * заливаем его зеленым
     * создаем текстуру из этого пиксмапа -  this.texture = new Texture(pixmap); получается длинная зеленая полоска,
     * после этого пиксмап уничтожаем pixmap.dispose();
     * @param batch - бач получаем из менеджера экранов он там уже создан с прокинутой ссылкой на бач из основного модуля игры
     */
    public LoadingScreen(SpriteBatch batch) {
        super(batch);
        Pixmap pixmap = new Pixmap(1280, 20, Pixmap.Format.RGB888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        this.texture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void show() {
    }

    /**
     * если загрузка ресурсов не завершена? if (Assets.getInstance().getAssetManager().update()) то
     * когда этот экран рендерится - он рисует на экране текстуру этой зеленой полоски
     * batch.draw(texture, 0, 0, 1280 * Assets.getInstance().getAssetManager().getProgress(), 20);
     * эта текстура будет масштабироваться по х -  1280 * Assets.getInstance().getAssetManager().getProgress()
     * Assets.getInstance().- занимаеся загрузкой ресурсов,
     * getAssetManager().getProgress() - и когда он грузит ресурсы он возвращает нам некое число от 0 до 1
     * получается чембольше процент ресурсов был загружен тем длинше будет эта полоска
     * пока зываем прогресс этой загрузки
     * но тут есть проверка загрузки if (Assets.getInstance().getAssetManager().update()) { если загрузка завершена?
     * то мы перейдем на следующий экран :
     * когда для экрана ресурссы загружены то метод  просто запоминает ссылку на основной игровой атлас  - Assets.getInstance().makeLinks();
     * вызываем метод перезода на targetScreen у - ScreenManager.getInstance().goToTarget();
     *
     *
     * @param delta
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Assets.getInstance().getAssetManager().update()) {
            Assets.getInstance().makeLinks(); //когда для экрана ресурссы загружены то он просто запоминает ссылку на основной игровой атлас
            ScreenManager.getInstance().goToTarget(); //вызываем метод загрузки исходного экрана втаргетскрин, метод переключает игру в целевой экран(запомненый)
        }
        batch.begin();
        batch.draw(texture, 0, 0, 1280 * Assets.getInstance().getAssetManager().getProgress(), 20);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
