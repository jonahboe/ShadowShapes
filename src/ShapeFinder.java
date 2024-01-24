import java.util.ArrayList;

import processing.core.PGraphics;

public class ShapeFinder {

    PGraphics window;
    int resolution;
    int maxDepth;

    ArrayList<Point> boarder = new ArrayList<Point>();

    ShapeFinder(PGraphics window, int resolution, int maxDepth) {
        this.window = window;
        this.resolution = resolution;
        this.maxDepth = maxDepth;
    }

    public void findBoarder(Point me, int direction) {
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
            // If the next pixel is black (shadow), then add that pixel the the boarder.
            if (window.get(next.x, next.y) == window.color(0)) {
                window.stroke(window.color(0, 255, 0));
                window.line(me.x, me.y, next.x, next.y);
                boarder.add(next);
            }
            // If the next pixel is not white, then end this branch.
            else if (window.get(next.x, next.y) != window.color(255)) {
                window.stroke(window.color(255, 255, 0));
                window.line(me.x, me.y, next.x, next.y);
            }
            // Otherwise, do a recursive check to the left and then right.
            else {
                window.stroke(window.color(0, 0, 255));
                window.line(me.x, me.y, next.x, next.y);
                int newDirection = (direction + 1) % 4;
                findBoarder(next, newDirection);
            }

            direction = (direction + 2) % 4;
        }

        window.noStroke();
    }
}
