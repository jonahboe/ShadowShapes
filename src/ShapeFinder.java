import java.util.ArrayList;

import processing.core.PGraphics;

public class ShapeFinder {

    PGraphics window;
    int resolution;
    int maxDepth;
    boolean render;

    ArrayList<Point> boarder = new ArrayList<Point>();

    ShapeFinder(PGraphics window, int resolution, int maxDepth, boolean render) {
        this.window = window;
        this.resolution = resolution;
        this.maxDepth = maxDepth;
        this.render = render;
    }

    public int findShape(Point me, int direction, int count) {
        // Check if we have gone to far.
        if (count > maxDepth) {
            boarder = new ArrayList<Point>();
            return -1;
        }

        window.strokeWeight(1);
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
            else if (window.get(next.x, next.y) == window.color(0)) {
                if (render) {
                    window.stroke(window.color(0, 255, 0));
                    window.line(me.x, me.y, next.x, next.y);
                }
                boarder.add(next);
            }
            // If the next pixel is not white, then end this branch.
            else if (window.get(next.x, next.y) != window.color(255)) {
                if (render) {
                    window.stroke(window.color(255, 255, 0));
                    window.line(me.x, me.y, next.x, next.y);
                }
            }
            // Otherwise, do a recursive check to the left and then right.
            else {
                if (render) {
                    window.stroke(window.color(0, 0, 255));
                    window.line(me.x, me.y, next.x, next.y);
                }
                int newDirection = (direction + 1) % 4;
                if (findShape(next, newDirection, count + 1) == -1) {
                    return -1;
                }
            }
            direction = (direction + 2) % 4;
        }

        window.noStroke();

        return 0;
    }
}
