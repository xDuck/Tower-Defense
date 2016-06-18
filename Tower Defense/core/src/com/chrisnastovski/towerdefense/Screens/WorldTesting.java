package com.chrisnastovski.towerdefense.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.chrisnastovski.towerdefense.Terrain.Building;
import com.chrisnastovski.towerdefense.Terrain.Terrain;
import com.chrisnastovski.towerdefense.Utilities.GameClass;
import com.chrisnastovski.towerdefense.Utilities.HexMath;

import java.util.Random;

public class WorldTesting implements Screen {

    GameClass myGame;

    TextureAtlas bgAtlas;
    TextureAtlas objAtlas;
    TextureAtlas vehicleAtlas;

    Terrain[][] terrain;
    String[] textureNames = { "grass10", "grass11", "grass12", "grass13", "grass14", "grass15", "grass16"};
    String borderTile = "grass05";


    int hexWidth = HexMath.hexWidth, hexHeight = HexMath.hexHeight;
    int playWidth = 15, playHeight = 12;
    int borderWidth = 1, borderHeight = 1;
    int fullWidth = (playWidth+borderWidth*2)*hexWidth;
    int fullHeight = (int) HexMath.hexToPixel(0, playHeight + borderHeight*2).y + hexHeight/4;

    SpriteBatch batch;
    OrthographicCamera camera;
    Random rand;
    long randSeed = System.currentTimeMillis();

    public WorldTesting(GameClass g) {
        rand = new Random(randSeed);
        myGame = g;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 700, 500);

        camera.translate(borderWidth*hexWidth - hexWidth/2, fullHeight / 2 - camera.viewportHeight / 2);

        batch = new SpriteBatch();

        bgAtlas = new TextureAtlas("sprites/background-tiles.txt");
        objAtlas = new TextureAtlas("sprites/buildings.txt");
        vehicleAtlas = new TextureAtlas("sprites/vehicles.txt");

        terrain = new Terrain[borderHeight*2 + playHeight][borderWidth*2 + playWidth];

        // Generate Map
        for(int i = 0; i < playHeight + borderHeight*2; i++) {
            for(int j = 0; j < playWidth + borderWidth*2; j++) {

                String texture = textureNames[rand.nextInt(textureNames.length)];
                if(i < borderHeight || i >= playHeight + borderHeight)
                    texture = borderTile;

                if(j < borderWidth || j >= playWidth + borderWidth)
                    texture = borderTile;

                Sprite s = bgAtlas.createSprite(texture);
                Vector2 pt = HexMath.hexToPixel(j, i);
                System.out.println("Hex " + i + ", " + j + ": " + pt);
                s.setBounds(pt.x, pt.y, hexWidth, hexHeight);
                Terrain t = new Terrain(s);
                terrain[i][j] = t;

            }
        }

        // Create Bases
        Sprite s = objAtlas.createSprite("campingTent");
        int row = playHeight/2 + 1;
        int col = borderWidth + 1;
        Building b = new Building(s, terrain[row][col]);
        terrain[row][col].addBuilding(b);
        clearLand(row, col);
        clearAround(row, col);

        s = objAtlas.createSprite("campingTent");
        col = playWidth + borderWidth - 2;
        b = new Building(s, terrain[row][col]);
        terrain[row][col].addBuilding(b);
        clearLand(row, col);
        clearAround(row, col);

        // Place Vehicles

    }

    public void clearLand(int row, int col) {
        terrain[row][col].clearLand(bgAtlas.createSprite("grass05"));
    }

    public void clearAround(int row, int col) {
        terrain[row-1][col].clearLand(bgAtlas.createSprite("grass05"));
        terrain[row-1][col-1].clearLand(bgAtlas.createSprite("grass05"));
        terrain[row][col-1].clearLand(bgAtlas.createSprite("grass05"));
        terrain[row+1][col].clearLand(bgAtlas.createSprite("grass05"));
        terrain[row+1][col+1].clearLand(bgAtlas.createSprite("grass05"));
        terrain[row][col+1].clearLand(bgAtlas.createSprite("grass05"));
        terrain[row-1][col+1].clearLand(bgAtlas.createSprite("grass05"));
        terrain[row+1][col-1].clearLand(bgAtlas.createSprite("grass05"));
    }

    public void show() {

    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0.153f, 0.682f, 0.376f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glBlendFunc(Gdx.gl.GL_ONE, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

        batch.setProjectionMatrix(camera.combined);
        camera.update();

        processInput();
        for(Terrain[] ta : terrain)
            for(Terrain t: ta)
                t.update();

        batch.begin();
        for(Terrain[] ta : terrain) {
            for (Terrain t : ta) {
                t.sprite.draw(batch);
            }
        }
        for(Terrain[] ta : terrain) {
            for (Terrain t : ta) {
                t.drawBuilding(batch);
            }
        }
        batch.end();
    }

    int cameraSpeed = 5;
    public void processInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.translate(-cameraSpeed, 0);
           // System.out.println(camera.position.x);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.translate(cameraSpeed, 0);
           // System.out.println(camera.position.x);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.translate(0, cameraSpeed);
            //System.out.println(camera.position.x);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.translate(0, -cameraSpeed);
            //System.out.println(camera.position.x);
        }



        if(camera.position.x < hexWidth/2 + camera.viewportWidth/2 )
            camera.position.x = hexWidth/2 + camera.viewportWidth/2;

        if(camera.position.x > fullWidth - camera.viewportWidth/2 )
            camera.position.x = fullWidth - camera.viewportWidth/2;

        if(camera.position.y < hexHeight/2 + camera.viewportHeight/2 )
            camera.position.y = hexHeight/2 + camera.viewportHeight/2;

        if(camera.position.y > fullHeight - hexHeight/2 - camera.viewportHeight/2 )
            camera.position.y = fullHeight - hexHeight/2 - camera.viewportHeight/2;


        if(Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            Vector2 pt = HexMath.pixelToHex(touch.x, touch.y);
            if(pt.y > borderHeight && pt.y < playHeight + borderHeight && pt.x > borderWidth && pt.x < playWidth + borderWidth) {
                for(Terrain[] ta : terrain) {
                    for (Terrain t : ta) {
                        t.setSelected(false);
                    }
                }
                terrain[(int) pt.y][(int) pt.x].setSelected(true);
            }
        }

    }

    public void hide(){

    }
    public void resize(int x, int y) {

    }
    public void pause() {

    }
    public void resume() {

    }
    public void dispose() {
        bgAtlas.dispose();
    }
}