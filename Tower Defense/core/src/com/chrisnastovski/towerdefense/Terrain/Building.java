package com.chrisnastovski.towerdefense.Terrain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Chris on 6/18/16.
 */
public class Building {

    public Sprite sprite;
    public Terrain terrain;
    public boolean isSelected = false;

    public Building(Sprite sprite, Terrain terrain) {
        this.sprite = sprite;
        this.terrain = terrain;

        this.sprite.setX(terrain.sprite.getX() + terrain.sprite.getWidth() / 2 - this.sprite.getWidth() / 2);
        this.sprite.setY(terrain.sprite.getY() + terrain.sprite.getHeight() / 2 - this.sprite.getHeight() / 2);
    }

    public void update() {
        if(isSelected)
            sprite.setColor(Color.WHITE.cpy().lerp(Color.BLACK, 0.5f));
        else
            sprite.setColor(Color.WHITE.cpy().lerp(Color.BLACK, 0.0f));
    }

}
