package com.chrisnastovski.towerdefense.Utilities;

import com.badlogic.gdx.Game;
import com.chrisnastovski.towerdefense.Screens.WorldTesting;

public class GameClass extends Game {

    @Override
    public void create () {
        this.setScreen(new WorldTesting(this));
    }


}