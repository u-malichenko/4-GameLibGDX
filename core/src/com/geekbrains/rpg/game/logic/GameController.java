package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.screens.ScreenManager;

import java.util.ArrayList;
import java.util.List;

/**
 * есть монстр контроллер:  private MonstersController monstersController;
 * private List<GameCharacter> allCharacters; - список всех персонажей
 */
public class GameController {
    private ProjectilesController projectilesController;
    private MonstersController monstersController;
    private WeaponsController weaponsController;
    private BonusController bonusController;
    private List<GameCharacter> allCharacters;
    private Map map;
    private Hero hero;
    private Vector2 tmp, tmp2;
    private Vector2 mouse;
    private float worldTime;

    /**
     * геттер на всех персонажей нужен для GameCharacter.onDeath -сброса у дохлого персонажа состояния атаки у всех остальных
     * @return
     */
    public List<GameCharacter> getAllCharacters() {
        return allCharacters;
    }

    public float getWorldTime() {
        return worldTime;
    }

    public Vector2 getMouse() {
        return mouse;
    }

    public Hero getHero() {
        return hero;
    }

    /**
     * геттер на контроллер монстров
     * @return
     */
    public MonstersController getMonstersController() {
        return monstersController;
    }

    public Map getMap() {
        return map;
    }

    public ProjectilesController getProjectilesController() {
        return projectilesController;
    }

    public WeaponsController getWeaponsController() {
        return weaponsController;
    }

    public BonusController getBonusController() {
        return bonusController;
    }

    /**
     * монстр контроллер можно создать только после того как мапа будет готова такак мы там ее используем для проверки залипания:
     * this.monstersController = new MonstersController(this, 5); - передаем туда ссылку на эту игру и колличество монстров в пачке
     * this.allCharacters = new ArrayList<>(); - список всех персонажей
     */
    public GameController() {
        this.allCharacters = new ArrayList<>();
        this.projectilesController = new ProjectilesController();
        this.weaponsController = new WeaponsController(this);
        this.bonusController = new BonusController(this);
        this.hero = new Hero(this);
        this.map = new Map();
        this.monstersController = new MonstersController(this, 5);
        this.tmp = new Vector2(0, 0);
        this.tmp2 = new Vector2(0, 0);
        this.mouse = new Vector2(0, 0);
    }

    /**
     * у контроллера монстров запускаем метод - update
     *
     * чистим список всх персонажей:
     *         allCharacters.clear();
     * добавляем первым персонажем героя:
     *         allCharacters.add(hero);
     * добавляем в список всех активных монстров из контроллера
     *         allCharacters.addAll(monstersController.getActiveList());
     *
     *         monstersController.update(dt);
     * отрисовка оружия:
     *         weaponsController.update(dt);
     * @param dt
     */
    public void update(float dt) {
        mouse.set(Gdx.input.getX(),Gdx.input.getY()); //в мышку зашиваем координаты х у
        ScreenManager.getInstance().getViewport().unproject(mouse); // преобразует мышинные координаты в мировые координаты проецирует на карту
        worldTime +=dt;
        allCharacters.clear();

        allCharacters.add(hero);
        allCharacters.addAll(monstersController.getActiveList());

        monstersController.update(dt);
        hero.update(dt);

        checkCollisions();
        projectilesController.update(dt);
        weaponsController.update(dt);
        bonusController.update(dt);
    }

    public void collideUnits(GameCharacter u1, GameCharacter u2) {
        if (u1.getArea().overlaps(u2.getArea())) {
            tmp.set(u1.getArea().x, u1.getArea().y);
            tmp.sub(u2.getArea().x, u2.getArea().y);
            float halfInterLen = ((u1.getArea().radius + u2.getArea().radius) - tmp.len()) / 2.0f;
            tmp.nor();

            tmp2.set(u1.getPosition()).mulAdd(tmp, halfInterLen);
            if (map.isGroundPassable(tmp2)) {
                u1.changePosition(tmp2);
            }

            tmp2.set(u2.getPosition()).mulAdd(tmp, -halfInterLen);
            if (map.isGroundPassable(tmp2)) {
                u2.changePosition(tmp2);
            }
        }
    }

    /**
     * перебираем столконовение героя со всеми АКТИВНЫМИ монстрами:
     *         for (int i = 0; i < monstersController.getActiveList().size(); i++) {
     *             Monster m = monstersController.getActiveList().get(i);
     *             collideUnits(hero, m);
     *
     * монстр могут толкатся между собой, проверяем двойным циклом, аналогично передыд.:
     *         for (int i = 0; i < monstersController.getActiveList().size() - 1; i++) {
     *             Monster m = monstersController.getActiveList().get(i);
     *             for (int j = i + 1; j < monstersController.getActiveList().size(); j++) {
     *                 Monster m2 = monstersController.getActiveList().get(j);
     *                 collideUnits(m, m2);
     *
     * перебираем все оружие и у активного оружия если расстояние от центра героя и до центра оружия меньше 20 тогда берем оружие
     *         for (int i = 0; i < weaponsController.getActiveList().size(); i++) {
     *             Weapon w = weaponsController.getActiveList().get(i);
     *             if (hero.getPosition().dst(w.getPosition()) < 20) {
     *                 w.consume(hero);
     *             }
     * прожектиль выпустили он летит по карте проверяется возможность удара в стену:
     *         for (int i = 0; i < projectilesController.getActiveList().size(); i++) {
     *             Projectile p = projectilesController.getActiveList().get(i);
     *             if (!map.isAirPassable(p.getCellX(), p.getCellY())) {
     *                 p.deactivate(); - деактивируем стрелу
     *                 continue;
     *
     *  //проверяем столкновение с героем, если разница позиций меньше 24 И хозяин не равно герою):
     *             if (p.getPosition().dst(hero.getPosition()) < 24 && p.getOwner() != hero) {
     *                 p.deactivate(); - деактивируем стрелу
     *                 hero.takeDamage(p.getOwner(), 1);
     *
     * //проверяем на столкноверие с АКТИВНЫМИ монстрами, пребираем активных монстров:
     *             for (int j = 0; j < monstersController.getActiveList().size(); j++) {
     *                 Monster m = monstersController.getActiveList().get(j);
     * //если владелец снаряда сам монстр то завершаем проверку:
     *                  if (p.getOwner() == m) {
     *                     continue;
     *
     * снаряд может влететь в героя если разница позиций меньше 24:
     * //снаряд может влететь в монстра не хозяина,если разница позиций меньше 24:
     *                 if (p.getPosition().dst(m.getPosition()) < 24) {
     *                     p.deactivate(); - деактивируем стрелу
     *           если при попадании прожектиля в монстра  начисляем бонусы герою и, в методе монстра проверяем его здоровье < 0 тогда запускаем метод onDeath = он исчезнет с карты :
     *           // TODO даже если монстры убивали сами себя случайно целясь в героя
     *                     if (m.takeDamage(p.getOwner(), 1)) {
     *                         hero.addCoins(MathUtils.random(1, 10));
     *
     * Мы добились:
     *              логика обработки состояний живет только в GameCharacter
     *              а переход из состояния в состояние живет в кадом конкретном виде персонажей МОнстр Герой
     *
     *
     *
     */
    public void checkCollisions() {
        //монстры и герой толкаются:
        for (int i = 0; i < monstersController.getActiveList().size(); i++) {
            Monster m = monstersController.getActiveList().get(i);
            collideUnits(hero, m);
        }
        //монстры толкают друг друга:
        for (int i = 0; i < monstersController.getActiveList().size() - 1; i++) {
            Monster m = monstersController.getActiveList().get(i);
            for (int j = i + 1; j < monstersController.getActiveList().size(); j++) {
                Monster m2 = monstersController.getActiveList().get(j);
                collideUnits(m, m2);
            }
        }
        //перебираем все оружие и у активного оружия, если расстояние от центра героя и до центра оружия меньше 20 тогда берем оружие
        for (int i = 0; i < weaponsController.getActiveList().size(); i++) {
            Weapon w = weaponsController.getActiveList().get(i);
            if (hero.getPosition().dst(w.getPosition()) < 20) {
                w.consume(hero);
            }
        }
        //перебираем все bonus и у активного , если расстояние от центра героя и до центра bonus меньше 20 тогда берем
        for (int i = 0; i < bonusController.getActiveList().size(); i++) {
            Bonus bonus = bonusController.getActiveList().get(i);
            if (hero.getPosition().dst(bonus.getPosition()) < 20) {
                bonus.consume(hero);
            }
        }
        //прожектиль выпустили он летит по карте
        for (int i = 0; i < projectilesController.getActiveList().size(); i++) {
            Projectile p = projectilesController.getActiveList().get(i);
            //проверяется возможность удара в стену
            if (!map.isAirPassable(p.getCellX(), p.getCellY())) {
                p.deactivate();
                continue;
            }
            //если снаряд влетел:
            if (p.getPosition().dst(hero.getPosition()) < 24 && p.getOwner() != hero) {
                p.deactivate();
                hero.takeDamage(p.getOwner(), p.getDamage());
            }
            //проверяем на столкноверие с АКТИВНЫМИ монстрами, пребираем активных монстров:
            for (int j = 0; j < monstersController.getActiveList().size(); j++) {
                Monster m = monstersController.getActiveList().get(j);
                //если владелец снаряда сам монстр то завершаем проверку:
                if (p.getOwner() == m) {
                    continue;
                }
                //снаряд может влететь в монстра не хозяина:
                if (p.getPosition().dst(m.getPosition()) < 24) {
                    p.deactivate();
                    m.takeDamage(p.getOwner(), p.getDamage());
                }
            }
        }
    }
}
