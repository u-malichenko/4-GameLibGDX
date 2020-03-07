package com.geekbrains.rpg.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** ВСЯ ОСНОВНАЯ ЛОГИКА ИГРЫ В наследующих классах-окнах
 * имплемент готовый интерфейс Screen - он позволяет
 * public void show() - метод срабатывает каждый раз когда вы попадаете на этот экран, чтото вроде мтеода криейт
 * public void render(float delta) -60 раз в секунду рендерим этот экран
 * public void resize(int width, int height) - срабатывает когда ктото начинает масштабировать форму,
 * или если телефон когда идет первичная настройка рабочего окна
 *  public void pause() - когда приложение переходит в режим ожидания
 *  public void hide()  - когда приложение свернули, на телефоне перешли на любой дрйгой экран
 *   public void resume() - это когда вернулись из состояния hide()
 *    public void dispose() - когда экран задиспозили
 *
 *    создаем базовый экран, так как во многих экранах не хотим пока реализовывать многие методы типа паузы ресайз
 *    у которого есть бач
 *    и есть пустые реализации этих методов
 *    теперь можно геймскрин унаследовать от абстрактскрин тем самым сделав его ИМПЛЕМЕНТС скрином
 *    и методы эти не писать там потому что они уже реализованны в родительстком классе, пусть и пустые они пока
 *    это такое сокращение кода - мы реализуем только то что нам инетресно
 *
 */
public abstract class AbstractScreen implements Screen {
    protected SpriteBatch batch;

    /**
     * конструктор абстрактного класса
     * ачь = проброшеному бачу
     * @param batch
     */
    public AbstractScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
