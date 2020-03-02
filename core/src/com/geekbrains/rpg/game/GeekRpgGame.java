package com.geekbrains.rpg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GeekRpgGame extends ApplicationAdapter {
    private SpriteBatch batch;  //пока просто рисовалка
    private Texture textureGrass;   // картинку загнать в видеопамять = текстура
    private Hero hero;
    private Pointer pointer;

    // Домашнее задание:
    // - Разобраться с кодом
    // - Персонаж должен двигаться к указателю

    /**
     * создание, срабатывает при запуске приложения
     * подготовительный этап
     */
    @Override
    public void create() {
        batch = new SpriteBatch();  // инициализируем спрайтбатч = создаем один бач на всю панель
        hero = new Hero(); //создаем героя
        pointer = new Pointer();
        textureGrass = new Texture("grass.png");    // из папки Ассес грузим текстуру создаем текстуру которая будет сформирована из файла  rass.png
    }

    /**
     * жизеный цикл = метод инит ( апдате и рендер)Цикл - диспоус
     * этот метод крутитится 60 раз в секунду. изменяется в зависимости от мощности железа FPS frames per seconds
     * тут отрисовка и обработка логики, отвечает за отрисовку обрабатывает слои по порядку
     */
    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime(); //сколько милисекунд прошло с последней отрисовки - дельтатайм 60 FPS = 1\60 sec 30FPS =1\30 sec
        // все рассчеты должны зависеть от этого времени, задаем скорость = 100px/sec за секунду перснаж должен подвинутья на 10 пикселей не важно какой там FPS
        // формула движения = x+=v*dt пример за секунду мы смещаемся на 100 пикселей =  60FPS = s = 60*100*(1/60) = 100px 60 раз сделаем эту операцию
        update(dt);     //обновляем игровую логику, запускаем метод апдате, а уже потом нарисовали мир
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    //цвет отчистки экрана
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);   //проим отчистить экран(форму) этим цветом что-то из ОпэнДЖИЭЛЬ)
        batch.begin();  //старт отрисовки обращаемся к бачу
        for (int i = 0; i < 16; i++) { //рисуем траву
            for (int j = 0; j < 9; j++) {
                batch.draw(textureGrass, i * 80, j * 80);   //бач нарисуй картинку в точке х у
            }
        }
        pointer.render(batch);
        hero.render(batch); //просим героя нарисоваться
        batch.end();    //конец отрисовки, все рисованое должно находится между бегином и эндом иначе словим Эксепшен
    }

    /**
     * игровая логика тут, чтоб не считать ее в рендере
     * метод отвечает за математику
     *
     * @param dt
     */
    public void update(float dt) {
        pointer.update(dt);
        hero.update(dt, pointer); //просим героя апдейтится и передаем ему дельта тайм
    }

    /**
     * после завершения работы приложения запускается этот метод
     * освобождение ресурсов, ресурсы видеопамяти нужно чистить самим из памяти так как сборщик туда доступа не имеет
     */
    @Override
    public void dispose() {
        batch.dispose();
    }
}