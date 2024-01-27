import java.util.ArrayList;
import java.util.Random;

import data.BitImage;
import data.Point;
import fisica.FPoly;

public class ShapeFinder extends Thread{

    public FPoly shape = null;
    public Point center;

    private BitImage window;
    private int resolution;
    private int maxDepth;
    private Random rand = new Random();

    ArrayList<Point> boarder;

    ShapeFinder(BitImage window, int resolution, int maxDepth) {
        this.window = window;
        this.resolution = resolution;
        this.maxDepth = maxDepth;
    }

    public void run() {
        // Try to find a shape at some random point.
        while(true)
        {
            Point startPoint = new Point(rand.nextInt(window.width), rand.nextInt(window.height));
            boarder = new ArrayList<Point>();
            if (findShape(startPoint, 0, 0) == 0 && boarder.size() > 2) {
                // If we find something, set the shape and return
                shape = new FPoly();
                shape.setNoStroke();
                shape.setDensity(50);
                shape.setRestitution((float)0.3);
                shape.setFriction((float)0.3);
                int xMin = window.width;
                int xMax = 0;
                int yMin = window.height;
                int yMax = 0;
                for (Point point : boarder) {
                    shape.vertex(point.x, point.y);
                    xMin = xMin > point.x ? point.x : xMin;
                    xMax = xMax < point.x ? point.x : xMax;
                    yMin = yMin > point.y ? point.y : yMin;
                    yMax = yMax < point.y ? point.y : yMax;
                }
                center = new Point((xMin + xMax)/2, (yMin + yMax)/2);
                return;
            }
        }
    }

    public int findShape(Point me, int direction, int count) {
        // Check if we have gone to far.
        if (count > maxDepth) {
            boarder = new ArrayList<Point>();
            return -1;
        }

        for (int i = 0; i < 2; i++){
            Point next = new Point(0, 0);
            switch (direction) {
                // Left
                case 0:
                    next = new Point(me.x - resolution, me.y);
                    break;
                // Down
                case 1:
                    next = new Point(me.x, me.y + resolution);
                    break;
                // Right
                case 2:
                    next = new Point(me.x + resolution, me.y);
                    break;
                // Down
                case 3:
                    next = new Point(me.x, me.y - resolution);
                    break;
            }
            // If we are touching the boarder, this is not a valid shape.
            if (next.x < 0 || next.x >= window.width || next.y < 0 || next.y >= window.height) {
                return -1;
            }
            // If the next pixel is black (shadow), then add that pixel the the boarder.
            else if (window.get(next.x, next.y) == BitImage.status.edge) {
                window.line(me.x, me.y, next.x, next.y);
                boarder.add(next);
            }
            // If the next pixel is not white, then end this branch.
            else if (window.get(next.x, next.y) == BitImage.status.searched) {
                window.line(me.x, me.y, next.x, next.y);
            }
            // Otherwise, do a recursive check to the left and then right.
            else {
                window.line(me.x, me.y, next.x, next.y);
                int newDirection = (direction + 1) % 4;
                if (findShape(next, newDirection, count + 1) == -1) {
                    return -1;
                }
            }
            direction = (direction + 2) % 4;
        }

        return 0;
    }
}
