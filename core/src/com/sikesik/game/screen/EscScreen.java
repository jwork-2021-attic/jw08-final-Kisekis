package com.sikesik.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sikesik.game.Assets;
import com.sikesik.game.SlimeGame;
import com.sikesik.game.entities.Bullet;
import com.sikesik.game.entities.Entity;
import com.sikesik.game.entities.Player;
import com.sikesik.game.factory.EntityFactory;
import com.sikesik.game.worlds.World;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EscScreen implements Screen {
    final SlimeGame game;
    OrthographicCamera camera;
    GameScreen screen;

    public EscScreen(final SlimeGame game, GameScreen screen) {
        this.game = game;
        this.screen = screen;
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
        Assets.font24.draw(game.batch,"Press S to save. Press L to load. Press O to open server",400,240);
        game.batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(screen);
            dispose();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            game.setScreen(screen);
            dispose();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            try {
                load();
            } catch (Exception e) {
                e.printStackTrace();
            }
            game.setScreen(screen);
            dispose();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            Input.TextInputListener t = new Input.TextInputListener() {
                @Override
                public void input(String text) {
                    try {
                        game.openServer(Integer.parseInt(text));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void canceled() {
                }
            };
            Gdx.input.getTextInput(t, "Open server with specific port", "8000", "");
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            Input.TextInputListener t = new Input.TextInputListener() {
                @Override
                public void input(String text) {
                    String[] data = text.split(":");
                    try {
                        game.openClient(data[0],Integer.parseInt(data[1]));
                        game.gameScreen.isClient = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void canceled() {

                }
            };
            Gdx.input.getTextInput(t, "Connect server", "127.0.0.1:8000", "");

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

    private void save() throws IOException {
        Path path = Paths.get("./core/saves/world.txt");
        File file =new File(String.valueOf(path));
        if(file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RandomAccessFile aFile = new RandomAccessFile(String.valueOf(path), "rw");
        FileChannel inChannel = aFile.getChannel();
        ByteBuffer buf = ByteBuffer.allocate(256);

        World world = screen.world;
        String info = new String();
        for(Player p : world.playerArray) {
            info = p.info()+"\n";
            write(buf,inChannel,info);
        }
        for(Entity e : world.entityArray) {
            if(!(e instanceof Player)) {
                info = e.info()+"\n";
                write(buf,inChannel,info);
            }
        }
        for(Bullet b : world.bulletArray) {
            info = b.info()+"\n";
            write(buf,inChannel,info);
        }
        inChannel.close();

    }
    private void write(ByteBuffer buf,FileChannel inChannel,String data) throws IOException {

        buf.clear();
        buf.put(data.getBytes());
        buf.flip();

        while(buf.hasRemaining()) {
            inChannel.write(buf);
        }
    }
    private void load() throws Exception {
        World world = new World();
        Path path = Paths.get("./core/saves/world.txt");
        FileInputStream fis = new FileInputStream(String.valueOf(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            switch (data[0]){
                case "Player":
                    world.curPlayer = EntityFactory.newPlayerWithInfo(world,data[1],Integer.parseInt(data[2]));
                    break;
                case "Slime":
                    EntityFactory.newSlimeWithInfo(world,data[1],Integer.parseInt(data[2]));
                    break;
                case "Bullet":
                    EntityFactory.newBulletWithInfo(world,data[1],Integer.parseInt(data[2]));
                    break;
            }
        }
        br.close();
        screen.world = world;
    }
}
