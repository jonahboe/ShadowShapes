package Pixel;

import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;

public class PixelHandler {

    public static final int BLACK = -16777216;
    public static final int WHITE = -1;

    public boolean checkIsHead(PImage image, int x, int y) {
        Position[] p = fetchRing(x, y);
        for (int i = 0; i < 4; i++) {
            if (image.get(p[i].x, p[i].y) == BLACK)
                return false;
        }
        return true;
    }

    public static Position[] fetchRing(int x, int y)
    {
        Position[] p = new Position[8];
        p[0] = new Position(x-1,y-1);
        p[1] = new Position(x,y-1);
        p[2] = new Position(x+1,y-1);
        p[3] = new Position(x+1,y);
        p[4] = new Position(x+1,y+1);
        p[5] = new Position(x,y+1);
        p[6] = new Position(x-1,y+1);
        p[7] = new Position(x-1,y);
        return p;
    }

}
