package com.geekbrains.rpg.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * контроллер прожектилей
 * обычно их называют имитерами
 * унаследовали от обджектпул и
 * сразу указали Дженерик < Прожектили > и соотвественно
 * там указано что нужно переопределить абстрактный метод   protected abstract T newObject();
 */
public class ProjectilesController extends ObjectPool<Projectile> {

    private TextureRegion projectileTextureRegion; //пока будет один, но
    // как только будет много раззных прожектилей, заменим это на ммассив

    /**СОЗДАЕМ ПРОЖЕКТИЛЬ ПЕРЕОПРЕДЕЛЯЕМ АБСТРАКТНЫЙ МЕТОД class ObjectPool
     * (пустой инициализируем пустым набором свойств, так что тут его становится создавать легко)
     * создаем унаследованный от ObjectPool абстрактный метод создания нового экземпляра данного класса
     * @return
     */
    @Override
    protected Projectile newObject() {
        return new Projectile(); //просто создадим вам новыый экземпляр Прожектиля
    }

    /**
     * конструктор контроллера
     * внешняя программа ни в коем случае не должна копаться во внутреннем массиве контроллера
     * достаем ссылку на атлас через синглтон атлас менеджера ресурсв Assets.getInstance().getAtlas().findRegion("arrow")
     */
    public ProjectilesController() {
        this.projectileTextureRegion = Assets.getInstance().getAtlas().findRegion("arrow");
    }
    /** ПОИСК СВОБОДНОГО ОБЪЕКТА И ЕГО НАСТРОЙКА
     * я хочу запустить (стрелу)
     * из какой то точки в какуюто точку
     * поскольку мы наследуемся от пула объектов мы сами у себя выхываем метод гет эктивэлемент
     * получаем элемент из фрилистоа(который уже перещел в активлист)
     * при вызове  getActiveElement() мы из списка свободных элементов достали объект,
     * перенесли его в список активных и тут же мы его настраиваем .setup
     * .setup(projectileTextureRegion, x, y, targetX, targetY);
     * отдаем туда (прокидываем) текстуру и координаты
     * сам контролле ртвечает за поиск свободного объектра и за его настройку
     */
    public void setup(float x, float y, float targetX, float targetY) {

        getActiveElement().setup(projectileTextureRegion, x, y, targetX, targetY);
    }

    /**СРАЗУ БЕГАЕМ ТОЛЬКО ПО СПИСКУ АКТИВНЫХ ОБЪЕКТОВ
     * запускаем цикл по всему списку активных элементов пула объектов
     * получаем элемент из активлиста и вызываем метод рендера у его
     * и всех их рендерим
     * так как тут не может быть неактивных объектов мы везде в прожектиле убрали проверки активности
     * @param batch
     */
    public void render(SpriteBatch batch) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).render(batch);
        }
    }

    /**
     *проходим только по списку активных обектов и вызываем у них абдейт метод
     * после всего запускам checkPool() - проверку неактивных элементов  для удаления из актив и переноса в фрипулл
     * @param dt
     */
    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool(); //для удаления неактивных объектов делаем проверку освобождения объектов
    }
}
