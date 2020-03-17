package com.geekbrains.rpg.game.logic;

import com.geekbrains.rpg.game.logic.utils.ObjectPool;

public class WeaponController extends ObjectPool<Weapon> {
    /**
     * есть пул  - extends ObjectPool<Monster>
     *      private GameController gc; - получает ссылку на геймконтроллер в конструкторе
     * внутренний таймер:
     *      private float innerTimer;
     * период перегенерации нового weapon:
     *      private float spawnPeriod;
     */

        private GameController gc;
        private float innerTimer;
        private float spawnPeriod;

        /**метод типа конструктор
         * прокидываем геймконтроллер
         * @return
         */
        @Override
        protected Weapon newObject() {
            return new Weapon(gc);
        }

        /**
         * конструктор геймконтроллера
         * хотим сразу генерировать пачку weapon, для этого запустим цикл до initialCount, и активируемна каждой итерации нового weapon с жизнью = активный в рандомной точке
         *         for (int i = 0; i < initialCount; i++) {
         *             getActiveElement().generateMe();
         *
         * @param gc -получает ссылку на гейм контролелер при инициализациии от GameController = this.monstersController = new MonstersController(this, 5);
         * @param initialCount - колличество одновременно генерируемых активных элементов(монстров)
         */
        public WeaponController(GameController gc, int initialCount) {
            this.gc = gc;
            this.spawnPeriod = 2.0f;
            for (int i = 0; i < initialCount; i++) {
                getActiveElement().generateMe();
            }
        }

        /**
         * на каждом ходе увеличиваем время таймера:
         *          innerTimer += dt;
         * если прошло больше 30 сек -spawnPeriod, если наш внутренний таймер превышает спаунпериод то тогда:
         *         if (innerTimer > spawnPeriod) {
         * сбрасываем таймер:
         *             innerTimer = 0.0f;
         * генерируем новвого монстра: получаем новый элемент(getActiveElement) и делаем метод генерейт:
         *             getActiveElement().generateMe();
         *
         * проходимся по всем монстрам и обновляем их:
         *         for (int i = 0; i < getActiveList().size(); i++) {
         *             getActiveList().get(i).update(dt);
         * проверяем пул на неактивных и выкидываем их из активного списка
         *              checkPool();
         *
         * @param dt
         */
        public void update(float dt) {
            innerTimer += dt;
            if (innerTimer > spawnPeriod) {
                innerTimer = 0.0f;
                getActiveElement().generateMe();
            }
            for (int i = 0; i < getActiveList().size(); i++) {
                getActiveList().get(i).update(dt);
            }
            checkPool();
        }


}
