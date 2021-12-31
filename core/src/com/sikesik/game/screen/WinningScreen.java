package com.sikesik.game.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sikesik.game.Assets;
import com.sikesik.game.SlimeGame;

public class WinningScreen implements Screen {
    final SlimeGame game;
    OrthographicCamera camera;

    public WinningScreen(final SlimeGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false,1600,896);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BROWN);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        Assets.font24.draw(game.batch,"YOU WIN!!",400,240);
        game.batch.end();

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
