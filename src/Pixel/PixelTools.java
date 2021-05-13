package Pixel;

public class PixelTools {

    public static final int BLACK = -16777216;
    public static final int WHITE = -1;

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
