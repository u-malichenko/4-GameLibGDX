package com.geekbrains.rpg.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

/** ГЛОБАЛЬНЫЙ МЕНЕДЖЕР РЕСУРСОВ
 * умеет ассинхронно загружать рессурсы обработка распределяется на несколько ядер
 * это наши ресурсы
 * делаем его синглтоном
 * private AssetManager assetManager; //это и есть менеджер ресурсов
 * атлас будет находится внутри менеджераа но часто используемая штука что ссылку лучше вынести
 */
public class Assets {

    /**СИНГЛТОН ЧТОБ МЫ МОГЛИ ПОЛУЧАТЬ К ЕМУ ДОСТУП ИЗ ЛЮБОЙ ТОЧКИ ПРОГРАМЫ
     *      * сам скрин менеджер является СИНГЛТОНОМ - глобальный объект,
     *      * мы можем получить доступ к нему из любой част нашей программы
     *      * если вы хотите обратится к ScreenManager - выполните статический метод getInstance
     *      * идея сингл тона - нужно хранить состояние объекта и оно должно быть одно на все приложение -глобальный объект
     *      * реализация - создается обычный классджава
     *      * - создается приватное статическое поле private static ScreenManager ourInstance = new ScreenManager(); экземпляр меня, создается экземпляр этого класса
     *      * чтоб этот объект достать создается статический геттер public static ScreenManager getInstance() {return ourInstance;}
     *      * этот getInstance возвращает ссылку на этот объект
     *      * такой объект может быть только один вообще во всей программе, потому что
     *      * у данного класса конструктор приватный private ScreenManager() { }
     *      * при всем желении объект этого класса низя создать снаружи
     *      * когда мы запрашиваем public static ScreenManager getInstance()
     *      * срабатывает статическаое поле инициализации - private static ScreenManager ourInstance = new ScreenManager();
     *      * объект создается и нам возвращается на его ссылка,
     *      * каждый следующий раз мы будем получать ссылку на этот объект(уже готовый)
     */
    private static final Assets ourInstance = new Assets();
    public static Assets getInstance() {
        return ourInstance;
    }

    private AssetManager assetManager; //это и есть менеджер ресурсов
    private TextureAtlas textureAtlas; //часто используемая штука что ссылку лучше вынести

    /**
     * гетттер для обращения к аталсу из любой точки программы
     * @return
     */
    public TextureAtlas getAtlas() {
        return textureAtlas;
    }

    /**
     * геттер для обращения к менеджеру ресурсов из любой точки проги
     * @return
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * конструктор менеджера ресурсов
     * приватный так как синглтон
     */
    private Assets() {
        assetManager = new AssetManager();
    }

    /**ПОД РАЗНЫЕ ЭКРАНЫ ГРУЗИТЬ РАЗНЫЕ РЕСУРСЫ
     * менеджер ресурсов понимает для какоего экрана какие ресурсы нужны
     * его задача конкретно под этот экран загрузить все данные
     * загрузка ресурсов происходит только в одном месте
     *
     * @param type - тип экрана
     */
    public void loadAssets(ScreenManager.ScreenType type) {
        switch (type) {
            case MENU:
                assetManager.load("images/game.pack", TextureAtlas.class);
                createStandardFont(24);
                createStandardFont(72);
                break;
            case GAME:
                assetManager.load("images/game.pack", TextureAtlas.class);
                createStandardFont(32);
                break;
        }
    }

    /**МЕТОД ГЕНЕРАЦИИ НОВОГО ШРИФТА createStandardFont(32);
     * получает конкретный размер шрифта - size
     * fontParameter.fontFileName = "fonts/Roboto-Medium.ttf"; - тут лежит сам шрифт
     * дальше настраиваем параметры фонта
     * размер, цвет, бордюр, четрного цв, тень и пр.
     *  assetManager.load .. загузить этот шрифт с параметрами
     *  сгенерит файл и засунет в ассед манаджер из которого мы его возьмем потом
     * @param size - инетресующий размер шрифта
     */
    private void createStandardFont(int size) {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver)); //из ттф файла гинерить битмапфонты
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = "fonts/Roboto-Medium.ttf";
        fontParameter.fontParameters.size = size;
        fontParameter.fontParameters.color = Color.WHITE;
        fontParameter.fontParameters.borderWidth = 1;
        fontParameter.fontParameters.borderColor = Color.BLACK;
        fontParameter.fontParameters.shadowOffsetX = 1;
        fontParameter.fontParameters.shadowOffsetY = 1;
        fontParameter.fontParameters.shadowColor = Color.DARK_GRAY;
        assetManager.load("fonts/font" + size + ".ttf", BitmapFont.class, fontParameter); //название файла шрифра тут в первом параметре, так его инужно доставать
    }

    /**
     * ЗАПОМИНАТЬ АТЛАС
     * когда для экрана ресурссы загружены то он просто запоминает ссылку на основной игровой атлас
     */
    public void makeLinks() {
        textureAtlas = assetManager.get("images/game.pack", TextureAtlas.class);
    }

    /**ОТЧИЩАТЬ ВСЕ РЕСУРСЫ
     * все ресурсы освобождает
     * разом освобождает все что было загружено
     */
    public void clear() {
        assetManager.clear();
    }
}