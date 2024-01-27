package data;

import processing.core.PGraphics;
import processing.core.PImage;

public class BitImage {

    public enum status {searched, edge, unsearched}

    private status[][] image;
    public int width;
    public int height;

    // The order of scanning a 2d array is improved in this order.
    public BitImage(PGraphics graphics) {
        this.width = graphics.width;
        this.height = graphics.height;
        this.image = new status[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (graphics.get(x,y) == graphics.color(255)) {
                    this.image[y][x] = status.unsearched;
                }
                else {
                    this.image[y][x] = status.edge;
                }
            }
        }
    }

    public status get(int x, int y) {
        return image[y][x];
    }

    public void line(int xa, int ya, int xb, int yb) {
        image[ya][xa] = status.searched;
        image[yb][xb] = status.searched;
    }
}
