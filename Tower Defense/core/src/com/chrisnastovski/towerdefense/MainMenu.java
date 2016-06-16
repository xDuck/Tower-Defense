package com.chrisnastovski.towerdefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;


public class MainMenu implements Screen {

	GameClass myGame;

	SpriteBatch batch;

	boolean isServer = true;
	MultiplayerClient client;
	MultiplayerServer server;

	Sprite s;
	Texture img;

	public static ArrayList<PathFollower> followers;

	OrthographicCamera camera;

	public MainMenu(GameClass g) {
		myGame = g;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 500, 500);

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		s = new Sprite(img);
		s.setBounds(0, 0, 20, 20);

		followers = new ArrayList<PathFollower>();


		server = new MultiplayerServer();
		client = new MultiplayerClient();

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

		for(PathFollower f : followers)
			f.update();

		processInput();

		batch.begin();
		for(PathFollower f : followers) {
			s = new Sprite(img);
			s.setX(f.x);
			s.setY(f.y);
			s.draw(batch);
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

	public void processInput() {
		if(Gdx.input.justTouched()) {
			Vector2[] path = {new Vector2(Gdx.input.getX(), Gdx.input.getY()), new Vector2(500, 200)};
			PathFollower follower = new PathFollower(path, 1, s);

			client.sendData(follower);
		}
	}
}
