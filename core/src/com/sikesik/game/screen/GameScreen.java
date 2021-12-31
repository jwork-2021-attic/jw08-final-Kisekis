package com.sikesik.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sikesik.game.Assets;
import com.sikesik.game.SlimeGame;
import com.sikesik.game.entities.Bullet;
import com.sikesik.game.entities.Player;
import com.sikesik.game.worlds.World;
import com.sikesik.game.worlds.WorldBuilder;

import java.io.IOException;

public class GameScreen implements Screen {
    final SlimeGame game;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    public static TiledMap map;
    private TiledMapRenderer mapRenderer;
    public Vector3 touchPosition;
    public World world;
    SpriteBatch hud;
    public boolean isClient = false;
    public static final float VPWIDTH = 1600/3;
    public static final float VPHEIGHT = 893/3;


    public GameScreen(final SlimeGame game) throws Exception {
        this.game = game;
        game.gameScreen = this;
        map = new TmxMapLoader().load("maps/map.tmx");
        world = new World();
        world = new WorldBuilder().build(world);

        camera = new OrthographicCamera(VPWIDTH,VPHEIGHT);
        hud = new SpriteBatch();

        tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
        Gdx.graphics.setCursor(Assets.cursor);
        touchPosition = new Vector3(-1,-1,-1);
    }


    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        Player player = world.playerArray.get(0);

        camera.position.x = Math.max(player.getX() + player.getWidth()/2,VPWIDTH/2);
        camera.position.x= Math.min(camera.position.x, World.WORLD_WIDTH*16-VPWIDTH/2);
        camera.position.y = Math.max(player.getY() + player.getHeight()/2,VPHEIGHT/2);
        camera.position.y = Math.min(camera.position.y , World.WORLD_HEIGHT*16-VPHEIGHT/2);

        camera.update();
        try {
            update(delta);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if(world.slimeNum == 0 && !isClient) {
//            endGame();
//        }
        draw();
    }
//    public void endGame() {
//        game.setScreen(new WinningScreen(game));
//    }

    public void draw() {

        ScreenUtils.clear(0, 0, 0.2f, 1);
        tiledMapRenderer.setView(camera);

        tiledMapRenderer.getBatch().begin();
        tiledMapRenderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("background"));
        world.draw(tiledMapRenderer.getBatch());
        tiledMapRenderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("foreground"));
        tiledMapRenderer.getBatch().end();

//        hud.begin();

//        hud.draw(Assets.slimeStand,1600-300,900-100,64,64);
////        Assets.font24.draw(hud,"LEFT : "+world.slimeNum,1600-200,900-100+32);
//        hud.end();
    }

    public void update(float deltaTime) throws IOException {
        if (deltaTime > 0.05f) deltaTime = 0.05f;
        world.slimeNum = world.entityArray.size-world.playerArray.size;
        if(Gdx.input.isTouched()) {
            touchPosition.set(Gdx.input.getX(), Gdx.input.getY(),0);
            System.out.println(touchPosition.x+" "+touchPosition.y +" "+ world.curPlayer.id);
            world.curPlayer.touchPos = touchPosition;
            camera.unproject(touchPosition);
            generateBullet(touchPosition, world.curPlayer);
        }else {
            touchPosition.set(-1,-1,-1);
            world.curPlayer.touchPos = touchPosition;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new EscScreen(game,this));
        }
        Gdx.input.setInputProcessor(world.curPlayer);
        world.update(deltaTime);
        if(isClient) {
            game.updateClient();
        }
    }
    public void generateBullet(Vector3 touchPosition,Player player) {
        if(touchPosition.x>0 && touchPosition.y>0){
            float dx = touchPosition.x-player.getX();
            float dy = touchPosition.y - player.getY();
            float r = (float) Math.sqrt(dx*dx+dy*dy);
            Bullet b = new Bullet(new Sprite(Assets.bullet),world,player);
            b.velocity.x = dx/r * b.speed;
            b.velocity.y = dy/r * b.speed;
            b.setPosition(player.getX(),player.getY());
            world.addBullet(b);
        }
    }

    public void generateBullet(String info,Player p) {
        String[] data = info.split("&");
        Vector3 pos = new Vector3(Float.parseFloat(data[9]),Float.parseFloat(data[10]),-1);
        if(p!=null && pos.x>0 && pos.y>0) {
            generateBullet(pos,p);
        }
    }

    public void updateServer(String info) {
        String[] infos = info.split("\n");
        for(String singleInfo : infos) {
            if (singleInfo!=null) {
                String[] data = singleInfo.split(",");
                Player player = null;
                if ("Player".equals(data[0])) {
                    for (int i = 0; i < world.playerArray.size; i++) {
                        if (Integer.parseInt(data[2]) == world.playerArray.get(i).id) {
                            world.playerArray.get(i).update(data[1]);
                            player = world.playerArray.get(i);
                            break;
                        }
                    }
                }
                String[] touchPos = data[3].split("&");
                if(Float.parseFloat(touchPos[0])!=-1 && Float.parseFloat(touchPos[1])!=-1) {
                    if(player!=null) {
                        generateBullet(new Vector3(Float.parseFloat(touchPos[0]),Float.parseFloat(touchPos[1]),-1),player);
                    }
                }
            }
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
        map.dispose();
        tiledMapRenderer.dispose();
        world.dispose();
    }
}
