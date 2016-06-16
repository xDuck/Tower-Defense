package com.chrisnastovski.towerdefense;

import com.badlogic.gdx.Game;

public class GameClass extends Game {

    @Override
    public void create () {
        this.setScreen(new MainMenu(this));
    }


}