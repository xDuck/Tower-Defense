package com.chrisnastovski.towerdefense.Units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.chrisnastovski.towerdefense.Screens.WorldTesting;

/**
 * Created by Chris on 6/20/16.
 */
public class Tank extends PathFollower{

    public Tank(WorldTesting world, Sprite sprite, Vector2[] path, int speed) {
        super(world, sprite, path, speed);
        passableTiles.add("grass11");
    }

}
