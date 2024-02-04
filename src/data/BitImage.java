package data;

import processing.core.PGraphics;
import processing.core.PImage;

public class BitImage {

    public enum bitColor {black, white, other}

    private bitColor[][] image;
    public int width;
    public int height;

    // The order of scanning a 2d array is improved in this order.
    public BitImage(PGraphics graphics) {
        this.width = graphics.width;
        this.height = graphics.height;
        this.image = new bitColor[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (graphics.get(x,y) == graphics.color(0)) {
                    this.image[y][x] = bitColor.black;
                }
                else {
                    this.image[y][x] = bitColor.white;
                }
            }
        }
    }

    public BitImage(PImage img) {
        this.width = img.width;
        this.height = img.height;
        this.image = new bitColor[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (img.get(x,y) == -16777216) { // This is black
                    this.image[y][x] = bitColor.black;
                }
                else {
                    this.image[y][x] = bitColor.white;
                }
            }
        }
    }

    public bitColor get(int x, int y) {
        return image[y][x];
    }

    public void line(int xa, int ya, int xb, int yb) {
        image[ya][xa] = bitColor.other;
        image[yb][xb] = bitColor.other;
    }
}
