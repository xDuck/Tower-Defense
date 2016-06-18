package com.chrisnastovski.towerdefense.Utilities;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Chris on 6/17/16.
 */
public class HexMath {
    public static Vector2 hexToPixel(int hw, int hh, int hc, int hr) {

        Vector2 pt = new Vector2();
        pt.x = hc*hw;
        pt.y = (hr * ( hh * 3 - 1)) / 4;//hr*hh - 0.25f* hr * hh - 0.25f*hr;
        if(hr%2 == 0)
            pt.x += 0.5f * hw;

        return pt;

    }

    public static Vector2 pixelToHex(int hw, int hh, float px, float py) {
        System.out.println("Original: " + px + ", " + py);
        Vector2 pt = new Vector2();
        pt.x = (px / hw);
        pt.y = ((4.0f*py) / (3.0f*hw - 1));

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
