package com.sikesik.game;

import com.badlogic.gdx.Game;



import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.sikesik.game.entities.Bullet;
import com.sikesik.game.entities.Entity;
import com.sikesik.game.factory.EntityFactory;
import com.sikesik.game.screen.GameScreen;
import com.sikesik.game.screen.MainMenuScreen;
import com.sikesik.game.worlds.World;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import java.util.Set;

public class SlimeGame extends Game {
    public GameScreen gameScreen;
    public SpriteBatch batch;
    public SocketChannel socketChannel;
    public void create() {
        try {
            Assets.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        batch = new SpriteBatch();
        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
        Assets.dispose();
    }

    public void openServer(int port) throws Exception {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Successfully open");
        while(true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) continue;
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                iterator.remove();
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel, selector);
                }
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey, selector);
                }
            }
        }
    }
    private void acceptHandler(ServerSocketChannel serverSocketChannel,
                               Selector selector)
            throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        socketChannel.write(StandardCharsets.UTF_8.encode("Connection successful"));
    }

    private void readHandler(SelectionKey selectionKey, Selector selector)
            throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        String request = "";

        while (socketChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            request += StandardCharsets.UTF_8.decode(byteBuffer);
        }
        socketChannel.register(selector, SelectionKey.OP_READ);
        boolean created = false;
        for(int i=0;i< gameScreen.world.playerArray.size;i++) {
            if(gameScreen.world.playerArray.get(i).id == Integer.parseInt(request.split(",")[2])) {
                created = true;
            }
        }
        if(!created) EntityFactory.newPlayer(gameScreen.world,request.split(",")[2]);

        gameScreen.updateServer(request);
        broadCast(selector,genWorldInfo());
    }

    private String genWorldInfo() {
        String info = "";
        for(int i =0 ;i<gameScreen.world.entityArray.size;i++) {
            info +=gameScreen.world.entityArray.get(i).info()+":";
        }
        return info;
    }
    private void broadCast(Selector selector
                           , String request) {
        Set<SelectionKey> selectionKeySet = selector.keys();
        for(SelectionKey selectionKey : selectionKeySet) {
            Channel targetChannel = selectionKey.channel();
            try {
                if(targetChannel instanceof SocketChannel) {
                    ((SocketChannel) targetChannel).write(
                            StandardCharsets.UTF_8.encode(request));
                    System.out.println(request);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public void openClient(String ip,int port) throws Exception {
        SocketChannel socketChannel = SocketChannel.open(
                new InetSocketAddress(ip, port));
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        gameScreen.world.clear();
        new Thread(new NioClientHandler(selector)).start();
        this.socketChannel = socketChannel;
}

public void updateClient() throws IOException {
    World world = gameScreen.world;
    String info = world.curPlayer.info()+","+gameScreen.touchPosition.x+"&"+gameScreen.touchPosition.y+"\n";
    socketChannel.write(
            StandardCharsets.UTF_8
                    .encode(info));
}
class NioClientHandler implements Runnable {
    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            for (; ; ) {
                int readyChannels = selector.select();

                if (readyChannels == 0) continue;
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    iterator.remove();
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private void readHandler(SelectionKey selectionKey, Selector selector)
                throws IOException {
        try{
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(30000);
            String response = "";
            while (socketChannel.read(byteBuffer) > 0) {
                byteBuffer.flip();
                response += StandardCharsets.UTF_8.decode(byteBuffer);
            }
            socketChannel.register(selector, SelectionKey.OP_READ);

            parseInfo(response);
        }catch (Exception e) {
        }

        }

        private void parseInfo(String info) {
            String[] lines = info.split(":");
            World world = gameScreen.world;

            for(String line : lines) {
                String[] data = line.split(",");
                if(data.length == 3) {
//                    world.clearSlime();
                    switch (data[0]){
                        case "Player":
                            boolean created = false;
                            for(int i = 0;i<world.playerArray.size;i++) {
                                if(world.playerArray.get(i).id == Integer.parseInt(data[2])) {
                                    created = true;
                                    if(world.playerArray.get(i).id != world.curPlayer.id) {
                                        world.playerArray.get(i).update(data[1]);
                                        created = true;
                                        gameScreen.generateBullet(data[1],world.playerArray.get(i));
                                        break;
                                    }
                                }
                            }
                            if(!created) EntityFactory.newPlayerWithInfo(world,data[1],Integer.parseInt(data[2]));
                            break;
                        case "Slime":
                            created = false;
                            for(int i = 0;i<world.entityArray.size-world.playerArray.size;i++) {
                                if(world.entityArray.get(i).id == Integer.parseInt(data[2])) {
                                    world.entityArray.get(i).update(data[1]);
                                    created = true;
                                    break;
                                }
                            }
                            if(!created) EntityFactory.newSlimeWithInfo(world,data[1],Integer.parseInt(data[2]));
                            break;
                }
                }
            }
        }
    }
}