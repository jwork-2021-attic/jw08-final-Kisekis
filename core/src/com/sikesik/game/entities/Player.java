package com.sikesik.game.entities;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.sikesik.game.Assets;
import com.sikesik.game.worlds.World;

import java.util.Random;

public class Player extends Entity implements InputProcessor {

    public Vector3 touchPos;
    float speed = 60*6;

    public Player(Sprite sprite, World world) {
        super(sprite, world,20);
        runAnimation = Assets.PlayerRun;
        touchPos = new Vector3();

    }

    public Player(Sprite sprite, World world,int id) {
        super(sprite, world,20);
        runAnimation = Assets.PlayerRun;
        this.id = id;
        touchPos = new Vector3();
    }

    @Override
    public void update(String info) {
        String[] data = info.split("&");
        setPosition(Float.parseFloat(data[0]),Float.parseFloat(data[1]));
        velocity.x = Float.parseFloat(data[2]);
        velocity.y = Float.parseFloat(data[3]);
        finalStateTime = Float.parseFloat(data[4]);
        stateTime = Float.parseFloat(data[5]);
        hp = Float.parseFloat(data[6]);
        MaxHp = Float.parseFloat(data[7]);
        state = Entity.State.valueOf(data[8]);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                velocity.y += speed;
                break;
            case Input.Keys.DOWN:
                velocity.y -= speed;
                break;
            case Input.Keys.LEFT:
                velocity.x -= speed;
                break;
            case Input.Keys.RIGHT:
                velocity.x += speed;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
            case Input.Keys.DOWN:
                velocity.y = 0;
                break;
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
                velocity.x = 0;
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public String toString() {
        return "Player";
    }

    @Override
    public String info() {
        String info;
        info = this.toString()+",";
        info += getX()+"&"+getY()+"&"+velocity.x +"&" +velocity.y + "&" + finalStateTime + "&" + stateTime
                + "&" + hp + "&" + MaxHp + "&" + state + "&"+touchPos.x+"&"+touchPos.y+ ","+super.id;
        return info;
    }
}
