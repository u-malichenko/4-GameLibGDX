package com.geekbrains.rpg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.geekbrains.rpg.game.logic.GameController;
import com.geekbrains.rpg.game.logic.Map;
import com.geekbrains.rpg.game.logic.WorldRenderer;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class GameScreen extends AbstractScreen {
    private GameController gc;
    private WorldRenderer worldRenderer;
    private Stage stage;
    private BitmapFont font72;
    private boolean active;

    public GameScreen(SpriteBatch batch) {
        super(batch);
        active = true;
    }

    @Override
    public void show() {
        gc = new GameController();
        worldRenderer = new WorldRenderer(gc, batch);
        createGui();
    }

    @Override
    public void render(float delta) {
        batch.begin();
        if (active) {
            batch.setColor(1, 1, 1, 1);
            gc.update(delta);
        } else {
            batch.setColor(0.5f, 0.5f, 0.5f, 0.7f);
        }
        batch.end();
        worldRenderer.render();
        stage.draw();
    }

    public void createGui() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage); //обрабатываем ввод для стейджа
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        //подготовка стиля:
        BitmapFont font10 = Assets.getInstance().getAssetManager().get("fonts/font10.ttf");
        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("smButton"), null, null, font10);
        //создаем кнопку:
        TextButton btnMenuGame = new TextButton("Menu", menuBtnStyle);
        btnMenuGame.setPosition((Map.MAP_CELLS_WIDTH * 80) - 130, (Map.MAP_CELLS_HEIGHT * 80) - 42);
        TextButton btnPauseGame = new TextButton("Pause", menuBtnStyle);
        btnPauseGame.setPosition((Map.MAP_CELLS_WIDTH * 80) - (130 + 130), (Map.MAP_CELLS_HEIGHT * 80) - 42);

        btnMenuGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });

        btnPauseGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                active = !active;
            }
        });
        stage.addActor(btnMenuGame);
        stage.addActor(btnPauseGame);
        skin.dispose();
    }

    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void dispose() {
        gc.dispose(); //отключить музыку
    }
}