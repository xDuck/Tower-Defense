package com.chrisnastovski.towerdefense.Utilities;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Chris on 6/17/16.
 */
public class SquareMath {

    public static int size = 60;

    public static Vector2 squareToPixel(float sc, float sr) {

        Vector2 pt = new Vector2();
        pt.x = sc*size;
        pt.y = sr*size;

        return pt;

    }

    public static Vector2 pixelToSquare(float px, float py) {
        Vector2 pt = new Vector2();
        pt.x = px/size;
        pt.y = py/size;
        return pt;
    }

    public static Vector2 squareToCenterPixel(float px, float py) {
        Vector2 pt = new Vector2();
        pt.x = px*size + size/2;
        pt.y = py*size + size/2;
        return pt;
    }
}
