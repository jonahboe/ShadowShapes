package data;

import processing.core.PGraphics;
import processing.core.PImage;

public class BitImage {

    public enum color {black, white, other}

    private color[][] image;
    public int width;
    public int height;

    // The order of scanning a 2d array is improved in this order.
    public BitImage(PGraphics graphics) {
        this.width = graphics.width;
        this.height = graphics.height;
        this.image = new color[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (graphics.get(x,y) == graphics.color(0)) {
                    this.image[y][x] = color.black;
                }
                else {
                    this.image[y][x] = color.white;
                }
            }
        }
    }

    public BitImage(PImage image) {
        this.width = image.width;
        this.height = image.height;
        this.image = new color[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (image.get(x,y) == 0x000000) {
                    this.image[y][x] = color.black;
                }
                else {
                    this.image[y][x] = color.white;
                }
            }
        }
    }

    public color get(int x, int y) {
        return image[y][x];
    }

    public void line(int xa, int ya, int xb, int yb) {
        image[ya][xa] = color.other;
        image[yb][xb] = color.other;
    }
}
