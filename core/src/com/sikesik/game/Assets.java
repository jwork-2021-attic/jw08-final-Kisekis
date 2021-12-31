package com.sikesik.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Assets {
    public static Texture overworld;
    public static TextureRegion playerStand;
    public static TextureRegion slimeStand;
    public static TextureRegion bullet;
    public static Animation bulletAnimation;
    public static Animation PlayerRunUp;
    public static Animation PlayerRunDown;
    public static Animation PlayerRun;
    public static Animation SlimeRun;
    public static BitmapFont font6;
    public static BitmapFont font24;
    public static ParticleEffect slimeExplode;
    public static Pixmap cursorImage;
    public static Cursor cursor;
    public static Texture loadTexture (String file) {
        return new Texture(Gdx.files.internal(file));
    }

    public static void load() throws Exception {
        font6 = generateFont(6);
        font24 = generateFont(24);
        Texture playerRun = loadTexture("characters/2_side.png");
        Texture slime = loadTexture("monster/slime1_front.png");
        Texture bulletTexture = loadTexture("other/bullet_tomato.png");
        overworld = new Texture(Gdx.files.internal("Overworld.png"));
        bullet = new TextureRegion(loadTexture("other/bullet_tomato_single.png"));

        bulletAnimation = new Animation(0.1f,
                new TextureRegion(bulletTexture, 22, 0, 22, 22),
                new TextureRegion(bulletTexture, 22*2, 0, 22, 22),
                new TextureRegion(bulletTexture, 22*3 + 2, 0, 22, 22),
                new TextureRegion(bulletTexture, 22*4 + 2, 0, 22, 22),
                new TextureRegion(bulletTexture, 22*5 + 2, 0, 22, 22)
                );
        PlayerRun = new Animation(0.2f,
                new TextureRegion(playerRun, 2, 2, 16, 21),
                new TextureRegion(playerRun, 19+2, 2, 16, 21),
                new TextureRegion(playerRun, 19*2 + 2, 2, 16, 21),
                new TextureRegion(playerRun, 19*3 + 2, 2, 16, 21)
        );
        slimeStand = new TextureRegion(loadTexture("monster/slime1.png"),0,0,16,16);
        SlimeRun = new Animation(0.2f,
                new TextureRegion(slime, 0, 0, 16, 16),
                new TextureRegion(slime, 16, 0, 16, 16),
                new TextureRegion(slime, 16*2, 0, 16, 16),
                new TextureRegion(slime, 16*3, 0, 16, 16));

        playerStand = new TextureRegion(loadTexture("characters/2.png"),0,0,16,21);
        slimeExplode = new ParticleEffect();
        slimeExplode.load(Gdx.files.internal("monster/slime.party"),Gdx.files.internal("monster/"));
        slimeExplode.scaleEffect(0.2f);
        cursorImage = new Pixmap(Gdx.files.internal("cursors/4crosshair2.png"));
        int xHotspot = cursorImage.getWidth() / 2;
        int yHotspot = cursorImage.getHeight() / 2;
        cursor = Gdx.graphics.newCursor(cursorImage, xHotspot, yHotspot);
    }
    public static BitmapFont generateFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Minecraft.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);   // font size 12 pixels
        generator.dispose();
        return font;
    }

    public static void dispose() {
        font6.dispose();
        font24.dispose();
        overworld.dispose();
        cursorImage.dispose();
        slimeExplode.dispose();
        cursor.dispose();
    }
}
