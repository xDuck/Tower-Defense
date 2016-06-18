package com.chrisnastovski.towerdefense.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.chrisnastovski.towerdefense.Units.PathFollower;
import com.chrisnastovski.towerdefense.Utilities.GameClass;

import java.util.ArrayList;


public class MainMenu implements Screen {

	GameClass myGame;

	SpriteBatch batch;

	public static ArrayList<PathFollower> followers;

	OrthographicCamera camera;

	public MainMenu(GameClass g) {
		myGame = g;

		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 500, 500);

	}

	public void show() {

	}

	public void render(float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		camera.update();

		processInput();

		batch.begin();
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
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			myGame.setScreen(new WorldTesting(myGame));
		}
	}

}
