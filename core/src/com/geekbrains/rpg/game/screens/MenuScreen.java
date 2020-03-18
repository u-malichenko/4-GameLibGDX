package com.geekbrains.rpg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.geekbrains.rpg.game.screens.utils.Assets;


public class MenuScreen extends AbstractScreen {
    private Stage stage;
    private BitmapFont font72;
    public MenuScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf");
        createGui();

    }

    @Override
    public void render(float delta) {
        update(delta);//обновит дт
        Gdx.gl.glClearColor(0, 0, 0.4f, 1); //очистка экрана
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font72.draw(batch, "Geek Rpg Game 2020",0 ,500,1280, Align.center,false);//координаты натписи
        batch.end();

        stage.draw();
    }

    /**
     * метод ля создания гуи интерфейса, вызывается в show
     * создаем стейдж
     * чтобы перехватывать клики мышки и прочее  -Gdx.input.setInputProcessor(stage);
     * то как выглядит интерфейс - Skin skin =new Skin();
     * скин хотим использовать текстуры которые лежат у тебя в аталсе
     */
    public void createGui(){
        stage = new Stage();
        Gdx.input.setInputProcessor(stage); //обрабатываем ввод для стейджа
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        BitmapFont font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        //подготовка стиля:
        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("simpleButton"),null,null,font24);
        //создаем кнопку:
        TextButton btnNewGame = new TextButton("New Game", menuBtnStyle);
        //размещаем:
        btnNewGame.setPosition(480,300);

        TextButton btnExitGame = new TextButton("Exit Game", menuBtnStyle);
        btnExitGame.setPosition(480,200);

        // на кнопку вешаем листнер, прослушиватель кликов
        btnNewGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
                //запускаем скринманагер и меняем экран
            }
        });

        btnExitGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                //выход из приложения
            }
        });

        //добавить на сцену кнопки и пр.
        stage.addActor(btnNewGame);
        stage.addActor(btnExitGame);

        skin.dispose();
    }
    public void update(float dt) {
        stage.act(dt); //обработать события сцены
    }
}
