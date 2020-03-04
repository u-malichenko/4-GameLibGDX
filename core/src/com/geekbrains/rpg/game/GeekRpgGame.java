package com.geekbrains.rpg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class GeekRpgGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private BitmapFont font32;
    private BitmapFont font10;
    private TextureAtlas atlas;
    private TextureRegion textureGrass;
    private Hero hero;
    private ArrayList<Projectile> pull;
    private Target target;

    // Домашнее задание:
    // 0. Разобраться с кодом
    // 1. Добавить на экран яблоко, и попробовать отследить попадание
    // стрелы в яблоко, при попадании яблоко должно появиться в новом месте
    // 2. ** Попробуйте заставить героя выпускать по несколько стрел

    //2 прожектель нужно выносить сюда
    //создать отдельный контроллер для снарядов,
    //сам контроллер должен активировать снаряды которые не активны

    // хранить в себе массив снарядов

    //и персонаж должен выполнять сетап через контроллер
    //при старте подготавливаем 100 объектов типа стрела(прожекткль) и храним их в пуле объектов
    //берем стреляем активирыем, он поподает в стну -деактивируем и возвращаем в пул переиспользовать стрелы
    //поскольку объекты в пуле сборщик мусора не имет права их трогать

    //1 если растояние между яблоком и центром стрелы+30 меньше растояния половина яблока значит попали
    //position.dst(pos); для рассчета растояния между объектами

    //нормировка - получаем вектор в том же направлениии только имеющий длинну 1 (стянули вектор к 1)
    //прибавили вектор сместилсся
    //убавили - получили вектор из одной точки в дргую точку
    //умножили - вектор потянули
    //разделили - вектор стянули

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.atlas = new TextureAtlas("game.pack");
        this.hero = new Hero(atlas);
        this.textureGrass = atlas.findRegion("grass");
        this.font32 = new BitmapFont(Gdx.files.internal("font32.fnt"));
        this.font10 = new BitmapFont(Gdx.files.internal("font10.fnt"));
        this.target = new Target(atlas);
        this.pull = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            pull.add(new Projectile(atlas));
        }

    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        update(dt);
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        fillBackground();
        target.render(batch);
        for (int i = 0; i < 5; i++) {
            pull.get(i).render(batch);
            //pull.get(i).renderGUI(batch,font32);
        }

        hero.render(batch);
        hero.renderGUI(batch, font32); //выводить текст
        batch.end();
    }

    public void update(float dt) {
        for (int i = 0; i < 5; i++) {
            pull.get(i).update(dt);
            target.update(pull.get(i));
        }

        hero.update(dt,this);
    }

    public ArrayList<Projectile> getPull() {
        return pull;
    }

    public void fillBackground(){
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                batch.draw(textureGrass, i * 80, j * 80);
            }
        }
    }
    @Override
    public void dispose() {
        batch.dispose();
    }
}