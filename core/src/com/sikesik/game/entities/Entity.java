package com.sikesik.game.entities;

import com.badlogic.gdx.graphics.g2d.*;

import com.badlogic.gdx.math.Vector2;
import com.sikesik.game.Assets;
import com.sikesik.game.worlds.World;

import java.io.Serializable;
import java.util.Random;

public class Entity extends Sprite implements Serializable {
    public Vector2 velocity;
    public World world;
    protected float speed = 60*2;
    public float finalStateTime;
    public float stateTime;
    public transient Animation runAnimation;
    public State state;
    public int id;
    public enum State {
        DEAD,
        WEAK,
        INVINCIBLE
    }
    public float hp;
    public float MaxHp;

    public Entity(Sprite sprite, World world, float maxHp) {
        super(sprite);
        velocity = new Vector2();
        this.world = world;
        stateTime = 0;
        state = State.WEAK;
        this.hp = maxHp;
        this.MaxHp = maxHp;
        Random r = new Random();
        id = r.nextInt(10000000);
    }




    public void update(float deltaTime) {
        if(state!=State.DEAD) {
            updatePosition(deltaTime);
            finalStateTime += deltaTime;
            if((int)(10*stateTime%10) == 0 && state!=State.DEAD) {
                state = State.WEAK;
            }
            updateAI(deltaTime);
        }
        stateTime += deltaTime;
        deadDetect();
    }

    public void update(String info) {
        return;
    }
    protected void updateAI(float deltaTime) {
        return;
    }

    @Override
    public void draw(Batch batch) {
        if(state!=State.DEAD) {
            TextureRegion keyFrame;
            if(this.velocity.x == 0 && this.velocity.y ==0 ){
                keyFrame = new TextureRegion(getTexture());
            }else {
                keyFrame =(TextureRegion)runAnimation.getKeyFrame(stateTime, true);
            }
            if(velocity.x >= 0) {
                batch.draw(keyFrame,getX(),getY(),getWidth(),getHeight());
            }else {
                batch.draw(keyFrame,getX()+getWidth(),getY(),-getWidth(),getHeight());
            }
            Assets.font6.draw(batch,hp+"",getX(), (float) (getY()+ getHeight()*1.5));
        }else {
            deadAnimation(batch);
        }

    }

    public void updatePosition(float deltaTime) {
        float oldX = getX();
        float oldY = getY();
        float newX = oldX+velocity.x*deltaTime;
        float newY = oldY+velocity.y*deltaTime;
        if((velocity.x <0 && newX < 0 ) ||
        (velocity.x >0&& newX > World.WORLD_WIDTH*16-getWidth()/2 )) {
            velocity.x = 0;
            newX = oldX;
        }
        if((velocity.y <0 && newY < 0 ) ||
                (velocity.y >0&& newY > World.WORLD_HEIGHT*16-getHeight()/2 )) {
            velocity.y = 0;
            newY = oldY;
        }

        boolean collisionX = false,collisionY = false;

        if(velocity.x<0) {
            collisionX = detectCollisionLeft(newX,newY);
            if(collisionX) {
                newX = oldX;
                velocity.x = 0;
            }

            collisionX = false;

        }else if(velocity.x > 0) {
            collisionX = detectCollisionRight(newX+getWidth()/2,newY);
            if(collisionX) {
                newX = oldX;
                velocity.x = 0;
            }

        }

        if(velocity.y > 0 ) {
            collisionY = detectCollisionUp(newX,newY + getHeight()/2);
            if(collisionY) {
                newY = oldY;
                velocity.y = 0;
            }

            collisionY =false;

        }else if(velocity.y < 0) {
            collisionY = detectCollisionDown(newX,newY);
            if(collisionY) {
                newY = oldY;
                velocity.y = 0;
            }
        }
        setX(newX);
        setY(newY);
    }


    private boolean detectCollisionRight(float x, float y) {
        boolean isBlocked = false;
        for(float step = 0;step < getHeight();step += 8) {
            isBlocked = world.isCellBlocked(x,y+step);
            if(isBlocked) break;
        }
        return isBlocked;
    }

    private boolean detectCollisionLeft(float x, float y) {
        boolean isBlocked = false;
        for(float step = 0;step < getHeight();step += 8) {
            isBlocked = world.isCellBlocked(x,y+step);
            if(isBlocked) break;
        }
        return isBlocked;
    }

    private boolean detectCollisionUp(float x, float y) {
        boolean isBlocked = false;
        for(float step = 0;step < getWidth();step += 8) {
            isBlocked = world.isCellBlocked(x+step,y);
            if(isBlocked) break;
        }
        return isBlocked;
    }

    private boolean detectCollisionDown(float x, float y) {
        boolean isBlocked = false;
        for(float step = 0;step < getWidth();step += 8) {
            isBlocked = world.isCellBlocked(x+step,y-step);
            if(isBlocked) break;
        }
        return isBlocked;
    }
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public void entityCollisionUpdate(Entity other) {

    }

    public void randomWalk(float deltaTime) {
        if((int)(10*stateTime%10) == 0) {
            Random r = new Random();
            int a = r.nextInt(10);
            int b = r.nextInt(10);
            int c = (int) Math.sqrt(a*a+b*b);
            c = c==0?1:c;
            velocity.x = a/c*speed;
            velocity.y = b/c*speed;
        }
    }

    public void deadDetect() {
        if(this.hp<=0 && state != state.DEAD) {
            state = State.DEAD;
            return;
        }
        if(state == State.DEAD) {
            dying();
            if(stateTime - finalStateTime >= 2) {
                world.remove(this);
            }
        }
    }
    public void dying() {
        return;
    }
    public void deadAnimation(Batch batch) {

    }

    public String info() {
        String info;
        info = this.toString()+",";
        info += getX()+"&"+getY()+"&"+velocity.x +"&" +velocity.y + "&" + finalStateTime + "&" + stateTime
+ "&" + hp + "&" + MaxHp + "&" + state + ","+id;
        return info;
    }

}
