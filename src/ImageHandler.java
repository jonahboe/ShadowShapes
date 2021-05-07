import processing.core.PImage;

public class ImageHandler {

    /****************************************************************************************
     *  mirrorImage():
     *    Take an image and flip it horizontally.
     ****************************************************************************************/
    public PImage mirrorImage(PImage initial) {
        // Set up our bounds
        int this_x = initial.width;
        int this_y = initial.height;

        // Make a new image of the same size
        PImage mirror = new PImage(this_x, this_y);

        // mirror the image
        for (int y = 0; y < this_y; y++) {
            for (int x = 0; x < this_x; x++) {
                mirror.set(this_x - 1 - x, y, initial.get(x, y));
            }
        }

        // return the image
        return mirror;
    }
}
