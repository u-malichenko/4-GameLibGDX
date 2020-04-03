package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.geekbrains.rpg.game.logic.utils.ObjectPool;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeaponsController extends ObjectPool<Weapon> {
    private GameController gc;
    private List<Weapon> prototypes; //сприсок всех возможных видов оружия цсв файл

    @Override
    protected Weapon newObject() {
        return new Weapon(gc);
    }

    public WeaponsController(GameController gc) {
        this.gc = gc;
        this.loadPrototypes(); //подтягиваем прототипы
    }

    /**
     * когда какое то оружие должно выпасть на экран:
     */
    public void setup(float x, float y) {
        Weapon out = getActiveElement(); //вытаскиваем оружие из пула
        out.copyFrom(prototypes.get(MathUtils.random(0,prototypes.size()-1)));        //TODO копируем случайное оружие
        forge(out); //куем оружие
        out.setPosition(x, y);        //задаем оружию текущую позицию:
        out.activate();
    }
    /**
     * чтоб отдать персаонажу первое его оружте:
     * дай мне любое оружие из прототипа
     */
    public Weapon getOneFromAnyPrototype (){
        Weapon out = new Weapon(gc); //создаем пустой экземпляр оружия
        out.copyFrom(prototypes.get(MathUtils.random(0, prototypes.size()-1))); //выбираем случайный тип оружия и копируем его
        forge(out);
        return out;
    }

    /**
     * оружие случайным образом усиливается
     * @param w
     */
    public void forge(Weapon w){
        for (int i = 0; i < 30; i++) {
            if(MathUtils.random(100)<5){
                w.setMinDamage(w.getMinDamage()+1);
            }
        }
        for (int i = 0; i < 30; i++) {
            if(MathUtils.random(100)<10){
                w.setMaxDamage(w.getMaxDamage()+1);
            }
        }
        if(w.getMinDamage()>w.getMaxDamage()){
            w.setMinDamage(w.getMaxDamage());
        }
    }

    public void update(float dt) {
        checkPool();
    }

    public void loadPrototypes (){
        prototypes = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = Gdx.files.internal("data/weapons.csv").reader(8192);
            reader.readLine();//вычитываем первую строку так как она заголовок
            String line =null;
            while ((line = reader.readLine())!=null){
                prototypes.add(new Weapon(line)); //оружие создаетя по прочитанной строчке и попадает в список прототипов
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
