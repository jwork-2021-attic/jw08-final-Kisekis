package com.sikesik.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.sikesik.game.Assets;
import com.sikesik.game.worlds.World;

import java.util.Random;

public class Slime extends Entity {
    public boolean isDead = false;
    public Slime(Sprite sprite, World world) {
        super(sprite, world,20);
        runAnimation = Assets.SlimeRun;
    }
    public Slime(Sprite sprite, World world,int id) {
        super(sprite, world,20);
        runAnimation = Assets.SlimeRun;
        this.id = id;
    }

    @Override
    public void entityCollisionUpdate(Entity other) {
        if(state == State.WEAK) {
            hp-=2.5;
            other.hp-=0.05;
            float dx = this.getX()-other.getX();
            float dy = this.getY()-other.getY();
            float r = (float) Math.sqrt((dx*dx)+(dy*dy));
            this.velocity.x = dx/r*speed;
            this.velocity.y = dy/r*speed;
            state = State.INVINCIBLE;
        }
    }

    @Override
    protected void updateAI(float deltaTime) {
        for(Player other : world.playerArray) {
            float dx = this.getX()-other.getX();
            float dy = this.getY()-other.getY();
            float r = (float) Math.sqrt((dx*dx)+(dy*dy));
            if(r<256 && r > 64) {
                Random rd = new Random();
                if(rd.nextInt(2)%2==0) {
                    this.velocity.x = -dx/r*speed;
                    this.velocity.y = -dy/r*speed;
                }
            }else {
                randomWalk(deltaTime);
            }
        }
    }
    @Override
    public void dying() {
        if(isDead == false) {
            isDead = true;
        }
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
    public void deadAnimation(Batch batch) {
        Assets.slimeExplode.update(Gdx.graphics.getDeltaTime());
        for(ParticleEmitter p:Assets.slimeExplode.getEmitters() ) {
            p.setPosition(this.getX()+getWidth()/2,this.getY()+getHeight()/2);
        }
        Assets.slimeExplode.start();
        Assets.slimeExplode.draw(batch);
    }

    @Override
    public String toString() {
        return "Slime";
    }
}
