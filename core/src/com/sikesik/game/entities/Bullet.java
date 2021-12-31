package com.sikesik.game.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sikesik.game.Assets;
import com.sikesik.game.worlds.World;

import java.util.Random;

public class Bullet extends Sprite{
    public Vector2 velocity;
    public World world;
    public float speed = 60*2;
    public Entity owner;
    public float stateTime;
    public float finalStateTime;
    public State state;
    public enum State{
        GENERATING,
        HITTING,
        HITTED,
        ONAIR
    }
    public int id;

    public Bullet(Sprite sprite, World world, Entity owner) {
        super(sprite);
        velocity = new Vector2();
        this.world = world;
        stateTime = 0;
        finalStateTime = 0;
        state = State.GENERATING;
        this.owner = owner;
        Random r = new Random();
        id = r.nextInt(1000000);
    }


    public void update(float deltaTime) {
        stateTime += deltaTime;
        if(state==State.GENERATING) {
            state = State.ONAIR;
        }
        if(state==State.ONAIR) {
            updatePosition(deltaTime);
            finalStateTime += deltaTime;
        }else if(state == State.HITTING){
            stateTime = 0;
            state = State.HITTED;
        }else {
            if(stateTime - finalStateTime >= 1f) {
                world.remove(this);
            }
        }
    }

    public void update(String info) {
        String[] data = info.split("&");
        setPosition(Float.parseFloat(data[0]),Float.parseFloat(data[1]));
        velocity.x = Float.parseFloat(data[2]);
        velocity.y = Float.parseFloat(data[3]);
        finalStateTime = Float.parseFloat(data[4]);
        stateTime = Float.parseFloat(data[5]);
        state = Bullet.State.valueOf(data[6]);
    }
    @Override
    public void draw(Batch batch) {
        TextureRegion keyFrame;
        if(state != State.HITTED) {
            batch.draw(Assets.bullet,getX(),getY());
        }else {
            keyFrame =(TextureRegion) Assets.bulletAnimation
                    .getKeyFrame(stateTime, false);
            batch.draw(keyFrame,getX(),getY());
            if(Assets.bulletAnimation.isAnimationFinished(stateTime)) {
                world.remove(this);
            }
        }
    }

    public void updatePosition(float deltaTime) {
        float oldX = getX();
        float oldY = getY();
        float newX = oldX+velocity.x*deltaTime;
        float newY = oldY+velocity.y*deltaTime;
        if((velocity.x <0 && newX < 0 ) ||
                (velocity.x >0&& newX > World.WORLD_WIDTH*16-getWidth()/2 )) {
            doDamage();
            return;
        }
        if((velocity.y <0 && newY < 0 ) ||
                (velocity.y >0&& newY > World.WORLD_HEIGHT*16-getHeight()/2 )) {
            doDamage();
            return;
        }
        if(world.isCellBlocked(newX,newY)) {
            doDamage();
            return;
        }
        setPosition(newX,newY);
    }

    public void doDamage() {
        state = State.HITTING;
    }

    public void doDamage(Entity e) {
        if(!(e instanceof Player)) {
            e.hp -= 0.1;
            if(state == State.ONAIR) {
                state = State.HITTING;
            }
        }
    }

    public String info() {
        String info;
        info = "Bullet,";
        info+=getX()+"&"+getY()+"&"+velocity.x+"&"+velocity.y+"&"+finalStateTime+"&"
                +stateTime+"&"+state+"&"+owner.id+","+id;
        return info;
    }

}




