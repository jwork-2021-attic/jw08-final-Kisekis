package com.sikesik.game.factory;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.sikesik.game.Assets;
import com.sikesik.game.entities.Bullet;
import com.sikesik.game.entities.Entity;
import com.sikesik.game.entities.Player;
import com.sikesik.game.entities.Slime;
import com.sikesik.game.worlds.World;

public class EntityFactory {
    private World world;

    public EntityFactory(World world) {
        this.world = world;
    }

    public static Player newPlayer(World world) {
        Player e = new Player(new Sprite(Assets.playerStand),world);
        e.setPosition(0,0);
        world.addEntity(e);
        return e;
    }
    public static Player newPlayer(World world,String id) {
        Player e = new Player(new Sprite(Assets.playerStand),world,Integer.parseInt(id));
        e.setPosition(0,0);
        world.addEntity(e);
        return e;
    }

    public static Player newPlayerWithInfo(World world, String info,int id) {
        Player e = new Player(new Sprite(Assets.playerStand),world);
        String[] data = info.split("&");

        e.setPosition(Float.parseFloat(data[0]),Float.parseFloat(data[1]));
        e.velocity.x = Float.parseFloat(data[2]);
        e.velocity.y = Float.parseFloat(data[3]);
        e.finalStateTime = Float.parseFloat(data[4]);
        e.stateTime = Float.parseFloat(data[5]);
        e.hp = Float.parseFloat(data[6]);
        e.MaxHp = Float.parseFloat(data[7]);
        e.state = Entity.State.valueOf(data[8]);
        e.id = id;
        world.addEntity(e);
        return e;
    }

    public static Slime newSlime(World world) {

        Slime slime = new Slime(new Sprite(Assets.slimeStand),world);
        Vector2 pos = world.getRandomPosition();
        slime.setPosition(pos.x,pos.y);
        world.addEntity(slime);
        return slime;
    }

    public static Slime newSlimeWithInfo(World world, String info,int id) {
        Slime slime = new Slime(new Sprite(Assets.slimeStand),world);
        String[] data = info.split("&");

        slime.setPosition(Float.parseFloat(data[0]),Float.parseFloat(data[1]));
        slime.velocity.x = Float.parseFloat(data[2]);
        slime.velocity.y = Float.parseFloat(data[3]);
        slime.finalStateTime = Float.parseFloat(data[4]);
        slime.stateTime = Float.parseFloat(data[5]);
        slime.hp = Float.parseFloat(data[6]);
        slime.MaxHp = Float.parseFloat(data[7]);
        slime.state = Entity.State.valueOf(data[8]);
        world.addEntity(slime);
        slime.id = id;
        return slime;
    }

    public static Bullet newBulletWithInfo(World world, String info,int id) {


        String[] data = info.split("&");
        int ownerId = Integer.parseInt(data[7]);
        Entity owner = null;
        for(int i = 0;i<world.entityArray.size;i++) {
            if(ownerId == world.entityArray.get(i).id) {
                owner = world.entityArray.get(i);
                break;
            }
        }
        Bullet bullet = new Bullet(new Sprite(Assets.bullet),world,owner);
        bullet.setPosition(Float.parseFloat(data[0]),Float.parseFloat(data[1]));
        bullet.velocity.x = Float.parseFloat(data[2]);
        bullet.velocity.y = Float.parseFloat(data[3]);
        bullet.finalStateTime = Float.parseFloat(data[4]);
        bullet.stateTime = Float.parseFloat(data[5]);
        bullet.state = Bullet.State.valueOf(data[6]);
        bullet.id = id;
        world.addBullet(bullet);
        return bullet;
    }
}

