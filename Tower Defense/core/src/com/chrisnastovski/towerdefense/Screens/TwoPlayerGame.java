package com.chrisnastovski.towerdefense.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.chrisnastovski.towerdefense.Multiplayer.MultiplayerServer;
import com.chrisnastovski.towerdefense.Units.PathFollower;
import com.chrisnastovski.towerdefense.Utilities.GameClass;

import java.util.ArrayList;

public class TwoPlayerGame implements Screen {

    GameClass myGame;

    boolean isServer = true;
    com.chrisnastovski.towerdefense.Multiplayer.MultiplayerClient client;
    com.chrisnastovski.towerdefense.Multiplayer.MultiplayerServer server;
    int playerNumber = -1;
    int numPlayers = -1;

    SpriteBatch batch;
    Sprite s;
    Texture img;

    public static ArrayList<PathFollower> followers;

    OrthographicCamera camera;

    //Paths
    Vector2[] path1 = {new Vector2(0, 200), new Vector2(500, 200)};
    Vector2[] path2 = {new Vector2(500, 200), new Vector2(0, 200)};


    public TwoPlayerGame(GameClass g) {
        myGame = g;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 500, 500);

        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        s = new Sprite(img);
        s.setBounds(0, 0, 20, 20);

        followers = new ArrayList<PathFollower>();

        server = new MultiplayerServer();
        client = new com.chrisnastovski.towerdefense.Multiplayer.MultiplayerClient();

        if(isServer)
            server.startLobby("Chris's Lobby");

        //client.findLobbies();
        client.joinOwnLobby("Chris");
    }

    public void show() {

    }

    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        camera.update();

        updateClient();
        processInput();

        batch.begin();
        if(playerNumber != -1) {
            processGameInput();
            updateObjects();
            drawGame();
        }
        batch.end();
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

    }

    public void updateClient() {
        if(client.playerNumber != -1 && playerNumber == -1) {
            System.out.println("Player Number received! " + client.playerNumber);
            playerNumber = client.playerNumber;
        }
        numPlayers = client.numPlayers;
        followers = client.followers;
    }

    public void updateObjects() {
        for(PathFollower f : followers)
            f.update();
    }

    public void drawGame() {
        for(PathFollower f : followers) {
            System.out.println(f.x);
            s = new Sprite(img);
            s.setBounds(f.x, f.y, 20, 20);
            s.draw(batch);
        }
    }

    // Game dependent input
    public void processGameInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if(playerNumber == 0)
                client.sendData(new PathFollower(path1, 2));
            else
                client.sendData(new PathFollower(path2, 2));
        }
    }

    // Non-Game dependent input
    public void processInput() {

    }
}