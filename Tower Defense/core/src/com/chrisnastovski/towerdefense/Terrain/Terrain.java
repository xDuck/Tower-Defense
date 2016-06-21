package com.chrisnastovski.towerdefense.Terrain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Chris on 6/18/16.
 */
public class Terrain {

    public Sprite sprite;
    public String tile;

    public boolean isSelected = false;

    public Building building;

    public Terrain(Sprite sprite, String tile) {
        this.tile = tile;
        this.sprite = sprite;
    }

    public void update() {
        if(building != null)
            building.update();

        if(isSelected)
            sprite.setColor(Color.WHITE.cpy().lerp(Color.BLACK, 0.5f));
        else
            sprite.setColor(Color.WHITE.cpy().lerp(Color.BLACK, 0f));

    }

    public void drawBuilding(SpriteBatch batch) {
        if(building != null) {
            building.sprite.draw(batch);
        }
    }

    public void addBuilding(Building building) {
        this.building = building;
    }

    public void clearLand(Sprite s) {
        s.setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        sprite = s;
        tile = "grass05";
    }

    public void setSelected(boolean s) {
        if(building != null)
            building.isSelected = s;
        isSelected = s;
    }

    public String getTile() {
        return tile;
    }
}
