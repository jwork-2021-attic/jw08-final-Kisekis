package com.sikesik.game.worlds;

import com.sikesik.game.entities.Player;
import com.sikesik.game.factory.EntityFactory;

public class WorldBuilder {
    public WorldBuilder() {

    }
    public World build(World world) {
        generateEntity(world);
        return world;
    }

    private void generateEntity(World world) {
        Player p = EntityFactory.newPlayer(world);
        world.curPlayer = p;
        world.slimeNum = 10;
        for(int i =0;i< world.slimeNum;i++) {
            EntityFactory.newSlime(world);
        }

    }

}
