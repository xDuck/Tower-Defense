package com.chrisnastovski.towerdefense.Utilities;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Chris on 6/17/16.
 */
public class HexMath {

    public static int hexWidth = 60, hexHeight = 60;

    public static Vector2 hexToPixel(int hc, int hr) {

        Vector2 pt = new Vector2();
        pt.x = hc*hexWidth;
        pt.y = (hr * ( hexHeight * 3 - 1)) / 4;//hr*hh - 0.25f* hr * hh - 0.25f*hr;
        if(hr%2 == 0)
            pt.x += 0.5f * hexWidth;

        return pt;

    }

    public static Vector2 pixelToHex(float px, float py) {
        System.out.println("Original: " + px + ", " + py);
        Vector2 pt = new Vector2();
        pt.x = (px / hexWidth);
        pt.y = ((4.0f*py) / (3.0f*hexHeight - 1));

        if(((int) pt.y)%2 == 0) {
            pt.y -= 0.25f;
        } else if( pt.y - (int) pt.y < 0.25) {
            pt.y -= 0.25;
        }


        if(((int) pt.y)%2 == 0) {
            pt.x -=0.5f;
        }

        System.out.println("Converted: " + pt.x + " " + pt.y);

        return pt;
    }
}
