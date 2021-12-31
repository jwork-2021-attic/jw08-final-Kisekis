package com.sikesik.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sikesik.game.Assets;
import com.sikesik.game.SlimeGame;

public class MainMenuScreen implements Screen {
    final SlimeGame game;

    OrthographicCamera camera;

    public MainMenuScreen(final SlimeGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 896);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        Assets.font24.draw(game.batch, "Welcome to SlimeGAME!!! ", 100, 150);
        Assets.font24.draw(game.batch, "Tap anywhere to begin!Press O to online game!", 100, 100);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            try {
                game.setScreen(new GameScreen(game));
            } catch (Exception e) {
                e.printStackTrace();
            }
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }


}
