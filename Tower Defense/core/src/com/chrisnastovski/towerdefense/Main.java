package com.chrisnastovski.towerdefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.chrisnastovski.towerdefense.MultiplayerObjects.stringMessage;


public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	boolean isServer = false;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		MultiplayerServer server = new MultiplayerServer();
		MultiplayerClient client = new MultiplayerClient();

		if(isServer)
			server.startLobby("Chris's Lobby");

		client.findLobbies();
        client.joinOwnLobby("Chris");

		// Send message
        stringMessage msg = new stringMessage();
        msg.message = "Hey!";
		msg.name = "Chris";
        client.sendData(msg);

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
}
