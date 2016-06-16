package com.chrisnastovski.towerdefense;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Chris on 6/15/16.
 */
public class PathFollower {
    Vector2[] path;
    int curIndex = 0;
    int speed;
    float x,y;

    public PathFollower() {

    }

    public PathFollower(Vector2[] path, int speed, Sprite sprite) {
        this.path = path;
        this.x = path[0].x;
        this.y = path[0].y;
        this.speed = speed;
    }

    public void update() {

        Vector2 goal = path[curIndex];
        double dist = Math.sqrt((x-goal.x)*(x-goal.x) + (y-goal.y)*(y-goal.y));
        if(dist < speed*2) curIndex++;
        curIndex = Math.min(curIndex, path.length-1);
        goal = path[curIndex];

        if(Math.abs(goal.x - x) > speed) {
            if (goal.x > x) {
                x+=speed;
            } else {
                x+=-speed;
            }
        }

        if(Math.abs(goal.y - y) > speed) {
            if (goal.y > y) {
                y+=speed;
            } else {
                y+=-speed;
            }
        }
    }
}
