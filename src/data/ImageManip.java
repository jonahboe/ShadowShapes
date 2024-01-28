package data;

import java.lang.Math;

import data.BitImage.color;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class ImageManip {

    /****************************************************************************************
     *  mirrorImage():
     *    Take an image and flip it horizontally.
     ****************************************************************************************/
    public static PImage mirrorImage(PImage a) {
        // Set up our bounds
        int this_x = a.width;
        int this_y = a.height;

        // Make a new image of the same size
        PImage mirror = new PImage(this_x, this_y);

        // mirror the image
        for (int y = 0; y < this_y; y++) {
            for (int x = 0; x < this_x; x++) {
                mirror.set(this_x - 1 - x, y, a.get(x, y));
            }
        }

        // return the image
        return mirror;
    }

    public static PImage diff(PApplet app, PImage a, PImage b) {
        PImage out = new PImage(a.width, a.height);
        for (int i = 0; i < a.pixels.length; i++) {
            int ar = (a.pixels[i] >> 16) & 0xFF;
            int ag = (a.pixels[i] >> 8) & 0xFF;
            int ab = a.pixels[i] & 0xFF;

            int br = (b.pixels[i] >> 16) & 0xFF;
            int bg = (b.pixels[i] >> 8) & 0xFF;
            int bb = b.pixels[i] & 0xFF;
            // Compute the difference of the red, green, and blue values
            int diffR = Math.abs(ar - br);
            int diffG = Math.abs(ag - bg);
            int diffB = Math.abs(ab - bb);
            // Render the difference image to the screen
            if (diffR < 50 && diffG < 50 && diffB < 50) {
                out.pixels[i] = app.color(255);
            }
            else {
                out.pixels[i] = app.color(0);
            }
        }
        return out;
    }

    
}
