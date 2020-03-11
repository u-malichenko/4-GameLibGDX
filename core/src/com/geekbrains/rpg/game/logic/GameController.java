package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * ИТОГОВЫЙ ХАБ ВСЕГО ПРИЛОЖЕНИЯ
 * он только прощитывает логику если нужно ччто то добавить в апдейт то тут, а если хотите что-то нарисовать то идете в WorldRenderer
 * удобно что графика не перемешивается с апдейтами лучше их не смешивать
 * <p>
 * отсюда запрашивать монстра героя снаряд - все они живут в контроллере
 * <p>
 * тут все относится к игре а не к графике избавился от всех рендеров
 * два вектра для рассчета пересечения окружностей персонажей - private Vector2 tmp, tmp2;
 * map Объявляем карту
 */
public class GameController {
    private ProjectilesController projectilesController;
    private Map map;
    private Hero hero;
    private MonsterController monsterController;
    private Vector2 tmp, tmp2;

    /**
     * геттер на героя
     *
     * @return
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * геттер на монстра
     *
     * @return
     */
    public MonsterController getMonsterController() {
        return monsterController;
    }

    /**
     * геттер на карту
     *
     * @return
     */
    public Map getMap() {
        return map;
    }

    /**
     * геттер на снаряды
     *
     * @return
     */
    public ProjectilesController getProjectilesController() {
        return projectilesController;
    }

    /**
     * конструктор геймконтроллера
     * инициализируем героя монстра карту и временные вектора
     * <p>
     * создае карту:
     * this.map = new Map();
     */
    public GameController() {
        this.projectilesController = new ProjectilesController();
        this.hero = new Hero(this);
        this.monsterController = new MonsterController(this);
        this.map = new Map();
        this.tmp = new Vector2(0, 0);
        this.tmp2 = new Vector2(0, 0);
    }

    /**
     * после проверки - checkCollisions(); - проверка урона
     * делаем - collideUnits(hero, monster); - проверяем на вселение)
     * если монстров много то запускаем цикл проверки каждого с каждым
     *
     * @param dt
     */
    public void update(float dt) {
        hero.update(dt);
        monsterController.update(dt);

        checkCollisions();

        //проверка на невселение друг в друга
        for (int i = 0; i < monsterController.getActiveList().size(); i++) {
            collideUnits(hero, monsterController.getActiveList().get(i)); //проверяем каждого монстра с героем
            for (int j = 0; j < monsterController.getActiveList().size(); j++) {
                collideUnits(monsterController.getActiveList().get(i), monsterController.getActiveList().get(j)); //проверяем монстров - каждого с каждым
            }
        }

        projectilesController.update(dt);
    }

    /**
     * Запретить персонажам друг в друга входить
     * если две окружности пересекаются под ногами тогда нужно оттолкнуть одно и другого персонажа
     * если область под ногами одного персонажа пересекается под ногами с областью другоуго персонажа?:
     * (u1.getArea().overlaps(u2.getArea())) {
     * то тогда их нужно разнести в разные стороны:
     * в вектор темп мы запишем центр первой окружности
     * tmp.set(u1.getArea().x, u1.getArea().y);
     * <p>
     * затем мы из центра первой окружности вычтем центр вотрой окружности
     * tmp.sub(u2.getArea().x, u2.getArea().y);
     * получаем вектор из конца второко в первый вектор
     * <p>
     * вычисляем -halfInterLen - половина области пересечения двух этих центров
     * float halfInterLen = ((u1.getArea().radius + u2.getArea().radius) - tmp.len()) / 2.0f;
     * u1.getArea().radius - радиус окружности 1 и 2 u2.getArea().radius
     * из их суммы вычитаем длинну вектора темп tmp.len()
     * может пригодится
     * растояние между центрами = сумме их радиусов, если
     * радиусы сближаются то это расстояние тсановится меньше чем сумма радиусов
     * <p>
     * далее нормируем темп tmp.nor(); тепреь указывает на центр первого круга
     * далее берем темп2 и смещиаем
     * берем координаты первого круга - u1.getPosition()
     * и смешаем в направлени темп - .mulAdd(tmp, на половину длинны - halfInterLen
     * tmp2.set(u1.getPosition()).mulAdd(tmp, halfInterLen);
     * = координаты первого круга перемешаем в обратном! направленияя от центра второго круга!
     * <p>
     * и аналогично пперемещаем центр второго круга в обраттном к первому, напривлении
     * координаты второго круга перемещаем в обратном расстоянии к тому самому вектору темп
     * tmp2.set(u2.getPosition()).mulAdd(tmp, -halfInterLen);
     * <p>
     * тем самым разносим круги , делаем их не пересекающимися
     * <p>
     * при это идет проверка чтоб персонаж не влип в стену или дерево
     * проверяем что персонаж не влипает в стену:
     * если проверка проходимости земли возвращает тру то переталкиваем персонаж
     * if (map.isGroundPassable(tmp2)) {
     * тогда меняем его позицию: нужно для того чтоб одновременно с персонажем переместить и его окружность там использвется перегрузка методов
     * u1.changePosition(tmp2);
     * иначе перонж стоит на месте
     *
     * @param u1- первый персонаж
     * @param u2  - второй персонаж
     */
    public void collideUnits(GameCharacter u1, GameCharacter u2) {
        if (u1.getArea().overlaps(u2.getArea())) {
            tmp.set(u1.getArea().x, u1.getArea().y);
            tmp.sub(u2.getArea().x, u2.getArea().y);
            float halfInterLen = ((u1.getArea().radius + u2.getArea().radius) - tmp.len()) / 2.0f;
            tmp.nor();

            tmp2.set(u1.getPosition()).mulAdd(tmp, halfInterLen); //координаты первого круг u1 перемешаем звобратном направлении 00:53
            if (map.isGroundPassable(tmp2)) {
                u1.changePosition(tmp2);
            }

            tmp2.set(u2.getPosition()).mulAdd(tmp, -halfInterLen); //координаты второго круга перемещаем в обратном расстоянии к тому самому вектору темп
            if (map.isGroundPassable(tmp2)) {
                u2.changePosition(tmp2);
            }
        }
    }

    /**
     * прожектиль может столкнуться с препятсвием на карте:
     * если мапа возвращает не фолс(клетка не проходима) - деактивируем снаряд и завершаем проверку этого снаряда
     * if (!map.isAirPassable(p.getCellX(), p.getCellY())) {
     * p.deactivate();
     * continue;
     * прожектиль может столкнуться с монстром:
     * if (p.getPosition().dst(monster.getPosition()) < 24) {
     * p.deactivate();
     * if (monster.takeDamage(1)) {
     * hero.addCoins(MathUtils.random(1, 10));
     */
    public void checkCollisions() {
        for (int i = 0; i < projectilesController.getActiveList().size(); i++) {
            Projectile p = projectilesController.getActiveList().get(i);
            if (!map.isAirPassable(p.getCellX(), p.getCellY())) {
                p.deactivate();
                continue;
            }
            //проверяем попадание снаряда в монстра
            for (int j = 0; j < monsterController.getActiveList().size(); j++) {
                if (p.getPosition().dst(monsterController.getActiveList().get(j).getPosition()) < 24) {
                    p.deactivate();
                    if (monsterController.getActiveList().get(j).takeDamage(1)) {
                        hero.addCoins(MathUtils.random(1, 10));
                    }
                }
            }

        }
    }
}
