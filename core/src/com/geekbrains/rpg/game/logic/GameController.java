package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private ProjectilesController projectilesController;
    private MonstersController monstersController;
    private List<GameCharacter> allCharacters;
    private Map map;
    private Hero hero;
    private Vector2 tmp, tmp2;

    public List<GameCharacter> getAllCharacters() {
        return allCharacters;
    }

    public Hero getHero() {
        return hero;
    }

    public MonstersController getMonstersController() {
        return monstersController;
    }

    public Map getMap() {
        return map;
    }

    public ProjectilesController getProjectilesController() {
        return projectilesController;
    }

    public GameController() {
        this.allCharacters = new ArrayList<>();
        this.projectilesController = new ProjectilesController();
        this.hero = new Hero(this);
        this.map = new Map();
        this.monstersController = new MonstersController(this, 5);
        this.tmp = new Vector2(0, 0);
        this.tmp2 = new Vector2(0, 0);
    }

    public void update(float dt) {
        allCharacters.clear();
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

    public void checkCollisions() {
        for (int i = 0; i < monstersController.getActiveList().size(); i++) {
            Monster m = monstersController.getActiveList().get(i);
            collideUnits(hero, m);
        }
        for (int i = 0; i < monstersController.getActiveList().size() - 1; i++) {
            Monster m = monstersController.getActiveList().get(i);
            for (int j = i + 1; j < monstersController.getActiveList().size(); j++) {
                Monster m2 = monstersController.getActiveList().get(j);
                collideUnits(m, m2);
            }
        }

        for (int i = 0; i < projectilesController.getActiveList().size(); i++) {
            Projectile p = projectilesController.getActiveList().get(i);
            if (!map.isAirPassable(p.getCellX(), p.getCellY())) {
                p.deactivate();
                continue;
            }
            if (p.getPosition().dst(hero.getPosition()) < 24 && p.getOwner() != hero) {
                p.deactivate();
                hero.takeDamage(p.getOwner(), 1);
            }
            for (int j = 0; j < monstersController.getActiveList().size(); j++) {
                Monster m = monstersController.getActiveList().get(j);
                if (p.getOwner() == m) {
                    continue;
                }
                if (p.getPosition().dst(m.getPosition()) < 24) {
                    p.deactivate();
                    if (m.takeDamage(p.getOwner(), 1)) {
                        hero.addCoins(MathUtils.random(1, 10));
                    }
                }
            }
        }
    }
}
