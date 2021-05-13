import processing.core.PImage;

/**
 * Class for handling some additional static methods and variables.
 */
public class ImageHandler {

    /**
     * Take an image and flip it horizontally.
     *
     * @param image The image to be mirrored.
     *
     * @return The mirrored version of the image.
     */
    public static PImage mirrorImage(PImage image) {
        // Set up our bounds
        int this_x = image.width;
        int this_y = image.height;

        // Make a new image of the same size
        PImage mirror = new PImage(this_x, this_y);

        // mirror the image
        for (int y = 0; y < this_y; y++) {
            for (int x = 0; x < this_x; x++) {
                mirror.set(this_x - 1 - x, y, image.get(x, y));
            }
        }

        // return the image
        return mirror;
    }

}
