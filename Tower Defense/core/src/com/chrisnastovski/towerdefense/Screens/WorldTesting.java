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
import com.chrisnastovski.towerdefense.Units.PathFollower;
import com.chrisnastovski.towerdefense.Units.Tank;
import com.chrisnastovski.towerdefense.Utilities.GameClass;
import com.chrisnastovski.towerdefense.Utilities.SquareMath;

import java.util.ArrayList;
import java.util.Random;

public class WorldTesting implements Screen {

    GameClass myGame;

    TextureAtlas bgAtlas;
    TextureAtlas objAtlas;
    TextureAtlas vehicleAtlas;

    public Terrain[][] terrain;
    ArrayList<PathFollower> vehicles = new ArrayList<PathFollower>();
    String[] textureNames = { "grass10", "grass11", "grass12", "grass13", "grass14", "grass15", "grass16"};
    String borderTile = "grass05";


    int size = SquareMath.size;
    int playColumns = 15, playRows = 12;
    public int borderColumns = 0, borderRows = 0;
    int fullWidth = (playColumns + borderColumns *2)*size;
    int fullHeight = (int) SquareMath.squareToPixel(0, playRows + borderRows *2).y + size/4;

    SpriteBatch batch;
    OrthographicCamera camera;
    Random rand;
    long randSeed = System.currentTimeMillis();

    public WorldTesting(GameClass g) {
        rand = new Random(randSeed);
        myGame = g;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 700, 500);

        camera.translate(borderColumns *size - size/2, fullHeight / 2 - camera.viewportHeight / 2);

        batch = new SpriteBatch();

        bgAtlas = new TextureAtlas("sprites/background-square.txt");
        objAtlas = new TextureAtlas("sprites/buildings.txt");
        vehicleAtlas = new TextureAtlas("sprites/vehicles.txt");

        terrain = new Terrain[borderRows *2 + playRows][borderColumns *2 + playColumns];

        // Generate Map
        for(int i = 0; i < playRows + borderRows *2; i++) {
            for(int j = 0; j < playColumns + borderColumns *2; j++) {

                String texture = textureNames[rand.nextInt(textureNames.length)];
                if(i < borderRows || i >= playRows + borderRows)
                    texture = borderTile;

                if(j < borderColumns || j >= playColumns + borderColumns)
                    texture = borderTile;

                Sprite s = bgAtlas.createSprite(texture);
                Vector2 pt = SquareMath.squareToPixel(j, i);
                s.setBounds(pt.x, pt.y, size, size);
                Terrain t = new Terrain(s, texture);
                terrain[i][j] = t;

            }
        }

        // Create Bases
        Sprite s = objAtlas.createSprite("campingTent");
        int row = playRows /2 + 1;
        int col = borderColumns + 1;
        Building b = new Building(s, terrain[row][col]);
        terrain[row][col].addBuilding(b);
        clearLand(row, col);
        clearAround(row, col);

        s = objAtlas.createSprite("campingTent");
        col = playColumns + borderColumns - 2;
        b = new Building(s, terrain[row][col]);
        terrain[row][col].addBuilding(b);
        clearLand(row, col);
        clearAround(row, col);

        // Place Vehicles
        Vector2 path[] = {new Vector2(borderColumns + 1, row), new Vector2(col, row)};

        Tank tank = new Tank(this, vehicleAtlas.createSprite("tank"), path, 1);
        vehicles.add(tank);

    }

    public void clearLand(int row, int col) {
        terrain[row][col].clearLand(bgAtlas.createSprite("grass05"));

        for(PathFollower f : vehicles)
            f.updatePath();
    }

    public void clearAround(int row, int col) {
        clearLand(row, col);
        clearLand(row-1, col);
        clearLand(row-1, col-1);
        clearLand(row-1, col+1);
        clearLand(row+1, col-1);
        clearLand(row+1, col+1);
        clearLand(row+1, col);
        clearLand(row, col-1);
        clearLand(row, col+1);
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
        for(PathFollower f: vehicles)
            f.update();

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
        for(PathFollower f : vehicles)
            f.sprite.draw(batch);
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



        if(camera.position.x < camera.viewportWidth/2 )
            camera.position.x = camera.viewportWidth/2;

        if(camera.position.x > fullWidth - camera.viewportWidth/2 )
            camera.position.x = fullWidth - camera.viewportWidth/2;

        if(camera.position.y < camera.viewportHeight/2 )
            camera.position.y = camera.viewportHeight/2;

        if(camera.position.y > fullHeight - camera.viewportHeight/2 )
            camera.position.y = fullHeight - camera.viewportHeight/2;


        if(Gdx.input.justTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            Vector2 pt = SquareMath.pixelToSquare(touch.x, touch.y);
            if(pt.y > borderRows && pt.y < playRows + borderRows && pt.x > borderColumns && pt.x < playColumns + borderColumns) {
                for(Terrain[] ta : terrain) {
                    for (Terrain t : ta) {
                        t.setSelected(false);
                    }
                }
                terrain[(int) pt.y][(int) pt.x].setSelected(true);
                clearLand((int) pt.y, (int) pt.x);
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