import java.util.ArrayList;

import processing.core.PApplet;

public class Finder {

    PApplet window;
    int resolution;
    int maxChild;

    ArrayList<Point> boarder = new ArrayList<Point>();

    Finder(PApplet window, int resolution, int maxChild) {
        this.window = window;
        this.resolution = resolution;
        this.maxChild = maxChild;
    }

    public void findBoarder(Point me, int direction) {
        window.strokeWeight(3);
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

            if (window.get(next.x, next.y) == window.color(0)) {
                window.stroke(window.color(0, 255, 0));
                window.line(me.x, me.y, next.x, next.y);
                boarder.add(next);
            }
            else if (window.get(next.x, next.y) != window.color(255)) {
                window.stroke(window.color(255, 255, 0));
                window.line(me.x, me.y, next.x, next.y);
            }
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
