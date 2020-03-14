package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * есть монстр контроллер:  private MonstersController monstersController;
 * private List<GameCharacter> allCharacters; - список всех персонажей
 */
public class GameController {
    private ProjectilesController projectilesController;
    private MonstersController monstersController;
    private WeaponController weaponController;
    private List<GameCharacter> allCharacters;
    private List<Weapon> allWeapon;
    private Map map;
    private Hero hero;
    private Vector2 tmp, tmp2;

    /**
     * геттер на всех персонажей нужен для GameCharacter.onDeath -сброса у дохлого персонажа состояния атаки у всех остальных
     * @return
     */
    public List<GameCharacter> getAllCharacters() {
        return allCharacters;
    }

    public List<Weapon> getAllWeapon() {
        return allWeapon;
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

    /**
     * монстр контроллер можно создать только после того как мапа будет готова такак мы там ее используем для проверки залипания:
     * this.monstersController = new MonstersController(this, 5); - передаем туда ссылку на эту игру и колличество монстров в пачке
     * this.allCharacters = new ArrayList<>(); - список всех персонажей
     */
    public GameController() {
        this.allCharacters = new ArrayList<>();
        this.allWeapon = new ArrayList<>();
        this.projectilesController = new ProjectilesController();
        this.hero = new Hero(this);
        this.map = new Map();
        this.weaponController =new WeaponController(this,7);
        this.monstersController = new MonstersController(this, 5);
        this.tmp = new Vector2(0, 0);
        this.tmp2 = new Vector2(0, 0);
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
     *
     * @param dt
     */
    public void update(float dt) {
        allCharacters.clear();

        //allWeapon.clear();

        allCharacters.add(hero);
        allCharacters.addAll(monstersController.getActiveList());

        hero.update(dt);
        monstersController.update(dt);
        checkCollisions();
        projectilesController.update(dt);
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
        //прожектиль выпустили он летит по карте
        for (int i = 0; i < projectilesController.getActiveList().size(); i++) {
            Projectile p = projectilesController.getActiveList().get(i);
            //проверяется возможность удара в стену
            if (!map.isAirPassable(p.getCellX(), p.getCellY())) {
                p.deactivate();
                continue;
            }

            if (p.getPosition().dst(hero.getPosition()) < 24 && p.getOwner() != hero) {
                p.deactivate();
                hero.takeDamage(p.getOwner(), 1);
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
                    //если монстр умер то начисляем бонусы герою)
                    // TODO даже если монстры убивали сами себя случайно целясь в героя
                    if (m.takeDamage(p.getOwner(), 1)) {
                        hero.addCoins(MathUtils.random(1, 10));
                    }
                }
            }
        }
    }
}
