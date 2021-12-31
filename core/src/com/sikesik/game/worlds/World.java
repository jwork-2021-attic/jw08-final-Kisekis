package com.sikesik.game.worlds;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sikesik.game.entities.Bullet;
import com.sikesik.game.entities.Entity;
import com.sikesik.game.entities.Player;
import com.sikesik.game.entities.Slime;
import com.sikesik.game.screen.GameScreen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class World implements Serializable {
    public static final int WORLD_WIDTH = 100;
    public static final int WORLD_HEIGHT = 100;
    public Array<Entity> entityArray;
    public Array<Player> playerArray;
    public Player curPlayer;
    public Array<Bullet> bulletArray;
    public int slimeNum;
    private transient TiledMapTileLayer[] collisionLayers;

    public World() throws Exception{
        entityArray = new Array<Entity>();
        playerArray = new Array<Player>();
        bulletArray = new Array<Bullet>();
        collisionLayers = new TiledMapTileLayer[]{(TiledMapTileLayer) GameScreen.map.getLayers().get("foreground"),(TiledMapTileLayer) GameScreen.map.getLayers().get("background")};
    }

    public void addEntity(Entity e) {
        if(e instanceof Player) {
            playerArray.add((Player) e);
        }
        entityArray.add(e);
    }

    public void addBullet(Bullet b) {
        bulletArray.add(b);
    }

    public void update(float deltaTime) {
        collisionDetect();
        bulletHitDetect();
        for(Entity e : entityArray) {
            e.update(deltaTime);
        }
        for (Bullet b  : bulletArray) {
            b.update(deltaTime);
        }
    }

    public Vector2 getRandomPosition() {
        Vector2 pos = new Vector2();
        Random r = new Random();
        do{
            pos.x = r.nextInt(16*WORLD_HEIGHT);
            pos.y = r.nextInt(16*WORLD_WIDTH);
        }while(isCellBlocked(pos.x,pos.y));
        return pos;
    }

    public void draw(Batch batch) {
        for (Entity e : entityArray) {
            e.draw(batch);
        }
        for(Bullet b : bulletArray) {
            b.draw(batch);
        }
    }
    public void dispose() {
        for (Entity e : entityArray)
            e.getTexture().dispose();
    }

    private void collisionDetect() {
        for(Player p : playerArray) {
            for(Entity e : entityArray) {
                if(! (e instanceof Player)) {
                    if(e.getBoundingRectangle().overlaps(p.getBoundingRectangle())) {
                        e.entityCollisionUpdate(p);
                        p.entityCollisionUpdate(e);
                    }
                }
            }
        }
    }

    private void bulletHitDetect() {
        for(Bullet b : bulletArray) {
            for(Entity e : entityArray) {
                if(b.owner != e && b.getBoundingRectangle().overlaps(e.getBoundingRectangle())) {
                    b.doDamage(e);
                }
            }
        }
    }

    public void remove(Entity e) {
        entityArray.removeValue(e,false);
    }
    public void remove(Bullet b) {
        bulletArray.removeValue(b,false);
    }

    public boolean isCellBlocked(float x, float y) {
        boolean isBlocked = false;
        TiledMapTileLayer.Cell cell;
        for(TiledMapTileLayer layer : collisionLayers) {
            cell = layer.getCell((int)x/16,(int)y/16);
            if(cell!=null&&cell.getTile()!=null) {
                isBlocked = cell.getTile().getProperties().containsKey("blocked");
            }
            if(isBlocked) break;
        }
        return isBlocked;
    }

    public void clear() {
        entityArray.clear();
        entityArray.add(curPlayer);
        bulletArray.clear();
    }

    public void clearSlime() {
        for(Entity e : entityArray) {
            if(e instanceof Slime) {
                entityArray.removeValue(e,false);
            }
        }
    }
}
