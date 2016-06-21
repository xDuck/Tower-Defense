package com.chrisnastovski.towerdefense.Units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.chrisnastovski.towerdefense.Screens.WorldTesting;
import com.chrisnastovski.towerdefense.Utilities.SquareMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Chris on 6/15/16.
 */
public class PathFollower {
    Vector2[] path;
    public Sprite sprite;
    WorldTesting world;

    boolean canReach = false;

    ArrayList<String> passableTiles = new ArrayList<String>();

    int curIndex = 0;
    int speed;

    public PathFollower(WorldTesting world, Sprite sprite, Vector2[] path, int speed) {
        passableTiles.add("grass05");

        this.path = path;
        this.sprite = sprite;
        this.speed = speed;
        this.world = world;

        Vector2 start = SquareMath.squareToCenterPixel((int)path[0].x, (int)path[0].y);
        this.sprite.setX(start.x - this.sprite.getWidth()/2);
        this.sprite.setY(start.y - this.sprite.getHeight()/2);

        buildPath((int)path[0].y, (int)path[0].x);
    }

    public void updatePath() {
        if(path == null) return;

        Vector2 pt = SquareMath.pixelToSquare(sprite.getX(), sprite.getY());
        buildPath((int)pt.y, (int)pt.x);
    }

    public void buildPath(int sr, int sc) {

        curIndex = 1;

        // Reset path to Start, End format
        Vector2 end = path[ path.length - 1 ];
        path = new Vector2[2];
        path[0] = new Vector2(sc, sr);
        path[1] = end;

        // Create steps grid
        int[][] map = new int[world.terrain.length][world.terrain[0].length];
        for(int i =0; i < map.length; i++)
            for(int j = 0; j < map[i].length; j++)
                map[i][j] = -1;

        // Queues
        Queue<Integer> quer = new LinkedList<Integer>();
        Queue<Integer> quec = new LinkedList<Integer>();

        // Add start points
        quer.add(sr);
        quec.add(sc);
        map[sr][sc] = 0;


        int[] dc = {0, 0,  1, -1};
        int[] dr = {1, -1, 0, 0};

        // Breadth first Shortest Path
        while(!quer.isEmpty()) {
            int c = quec.poll();
            int r = quer.poll();

            for(int i = 0; i < dc.length; i++) {
                for (i = 0; i < dr.length; i++) {
                    try {
                        int tc = c + dc[i];
                        int tr = r + dr[i];

                        String tile = world.terrain[tr][tc].getTile();
                        System.out.println(tile);
                        if(map[tr][tc] != -1 || map[tr][tc] > map[r][c] + 1) continue;

                        // Make sure tile is clear
                        if(passableTiles.contains(tile)) {
                            quer.add(tr);
                            quec.add(tc);
                            map[tr][tc] = map[r][c] + 1;
                        }

                    } catch (Exception e) {

                    }
                }
            }
        }

        // Print map
        for(int i = map.length-1; i >= 0; i--) {
            for (int j = 0; j < map[0].length; j++) {
                if(i == path[1].y && path[1].x == j)
                    System.out.print("E\t");
                System.out.print(map[i][j] + "\t");
            }
            System.out.println();
        }

        //  Check if able to reach
        if(map[(int) path[1].y][(int) path[1].x] == -1) {
            canReach = false;
        } else {

            // Build list of tiles to visit
            int ssr = (int) path[1].y, ssc = (int) path[1].x;
            int steps = map[ssr][ssc];
            path = new Vector2[steps+1];
            path[0] = new Vector2(ssc, ssr);
            for(int i = 1; i < steps + 1; i++) {
                path[i] = findNearbyMin(map, (int) path[i-1].y, (int) path[i - 1].x);
                System.out.println(path[i]);
            }

            System.out.println(Arrays.toString(path));

            // Reverse it
            for(int i = 0; i < path.length / 2; i++) {
                Vector2 temp = path[i];
                path[i] = path[path.length - i - 1];
                path[path.length - i - 1] = temp;
            }

            canReach = true;
            System.out.println(Arrays.toString(path));
        }
    }

    private Vector2 findNearbyMin(int[][] map, int r, int c) {
        Vector2 pt = new Vector2();
        int[] dc = {0, 0,  1, -1};
        int[] dr = {1, -1, 0, 0};

        int min = map[r][c];
        System.out.println("Starting at " + r + ", " + c + " = " + min);

        for(int i = 0; i < dc.length; i++) {
            for (i = 0; i < dr.length; i++) {
                try {
                    int tc = c + dc[i];
                    int tr = r + dr[i];

                    System.out.println("\tTrying " + tr + ", " + tc + " = " + map[tr][tc]);

                    if (map[tr][tc] < min && map[tr][tc] != -1) {
                        pt.x = tc;
                        pt.y = tr;
                        min = map[tr][tc];
                        System.out.println("\tfound min");
                    }

                } catch (Exception e) {

                }
            }
        }

        return pt;
    }

    public void update() {

        if(!canReach || path == null) return;

        Vector2 goal = SquareMath.squareToCenterPixel( path[curIndex].x, path[curIndex].y );
        goal.x -= sprite.getWidth() / 2;
        goal.y -= sprite.getHeight() / 2;

        Vector2 current = new Vector2( sprite.getX(), sprite.getY() );

        double dist = Math.sqrt((current.x-goal.x)*(current.x-goal.x) + (current.y-goal.y)*(current.y-goal.y));
        if(dist < speed*2) {
            curIndex++;
            System.out.println("Made it to index " + (curIndex-1) + "(" + path[curIndex-1].x + ", " + path[curIndex-1].y + ")");
        }

        if(curIndex == path.length) {
            path = null;
            return;
        }
        goal = SquareMath.squareToCenterPixel( path[curIndex].x, path[curIndex].y );
        goal.x -= sprite.getWidth() / 2;
        goal.y -= sprite.getHeight() / 2;

        if(Math.abs(goal.x - current.x) > speed) {
            if (goal.x > current.x) {
                sprite.translateX(speed);
            } else if (goal.x < current.x) {
                sprite.translateX(-speed);
            }
        }

        if(Math.abs(goal.y - current.y) > speed) {
            if (goal.y > current.y) {
                sprite.translateY(speed);
            } else if (goal.y < current.y){
                sprite.translateY(-speed);
            }
        }
    }
}
