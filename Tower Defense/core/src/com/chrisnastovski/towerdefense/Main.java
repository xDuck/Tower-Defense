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
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		MultiplayerHandler mp = new MultiplayerHandler();

		if(isServer)
			mp.startLobby("Chris's Lobby");

		mp.findLobbies();
        mp.joinOwnLobby("Chris");

		// Send message
        MultiplayerUtils.stringMessage msg = new MultiplayerUtils.stringMessage();
        msg.message = "Hey!";
        mp.sendData(msg);

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
