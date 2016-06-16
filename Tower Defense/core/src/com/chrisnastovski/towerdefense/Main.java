package com.chrisnastovski.towerdefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	boolean isServer = true;
	MultiplayerClient client;
	MultiplayerServer server;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		server = new MultiplayerServer();
		client = new MultiplayerClient();

		if(isServer)
			server.startLobby("Chris's Lobby");

		client.findLobbies();
        client.joinOwnLobby("Chris");

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(Gdx.input.justTouched()) {
			// Send message
			MultiplayerObjects.stringMessage msg = new MultiplayerObjects.stringMessage();
			msg.message = "Hey!";
			msg.name = "Chris";

			client.sendData(msg);
		}

		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
}
