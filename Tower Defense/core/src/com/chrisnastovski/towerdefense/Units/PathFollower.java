package com.chrisnastovski.towerdefense.Units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.chrisnastovski.towerdefense.Utilities.HexMath;

/**
 * Created by Chris on 6/15/16.
 */
public class PathFollower {
    Vector2[] path;
    public Sprite sprite;

    int curIndex = 0;
    int speed;

    public PathFollower(Sprite sprite, Vector2[] path, int speed) {
        this.path = path;
        this.sprite = sprite;
        this.sprite.setX(path[0].x);
        this.sprite.setY(path[0].y);
        this.speed = speed;

        for(int i =0; i < path.length; i++) {
            Vector2 hex = HexMath.hexToPixel((int) path[i].x, (int) path[i].y);
            path[i] = hex;
        }
    }

    public void update() {

        Vector2 goal = path[curIndex];
        double dist = Math.sqrt((sprite.getX()-goal.x)*(sprite.getX()-goal.x) + (sprite.getY()-goal.y)*(sprite.getY()-goal.y));
        if(dist < speed*2) curIndex++;
        curIndex = Math.min(curIndex, path.length-1);
        goal = path[curIndex];

        if(Math.abs(goal.x - sprite.getX()) > speed) {
            if (goal.x > sprite.getX()) {
                sprite.translateX(speed);
            } else {
                sprite.translateX(-speed);
            }
        }

        if(Math.abs(goal.y - sprite.getY()) > speed) {
            if (goal.y > sprite.getY()) {
                sprite.translateY(speed);
            } else {
                sprite.translateY(-speed);
            }
        }
    }
}
