import Pixel.Position;
import Pixel.PixelTools;
import Pixel.SmartPosition;
import fisica.FPoly;
import fisica.FWorld;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Class for handling the creation and destruction of shapes.
 */
public class ShapeHandler implements Runnable {

    // Applet running
    private final ShadowShapes applet;
    // Physics world
    private final FWorld world;
    // Minimum number of points it takes for a shape to be valid.
    private final int numberOfVerticesMin;
    // Size ratio of images.
    private final float imageResolution;

    // Variables for monitoring shape creation
    private PImage image;
    private boolean[][] field;
    private final int width;
    private final int height;
    private ArrayList<FPoly> shapeSet;

    /**
     * Constructor.
     *
     * @param applet The applet controlling this handler.
     * @param world The physics world being drawn to.
     * @param imageResolution How much to scale the image down before searching.
     * @param numberOfVerticesMin The minimum number of points a shape must have.
     */
    public ShapeHandler(ShadowShapes applet, FWorld world, float imageResolution, int numberOfVerticesMin) {
        this.applet = applet;
        this.world = world;
        this.imageResolution = imageResolution;
        this.numberOfVerticesMin = numberOfVerticesMin;
        this.width = applet.width;
        this.height = applet.height;
        this.field = new boolean[this.width][this.height];
    }

    /**
     * This is where our thread will start.
     */
    @Override
    public final void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                setImage(applet.getImage());
                updateShapes();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            finally {
                // Lets add our new shapes
                addShapes();
            }
        }
    }

    /**
     * Set the image currently being searched.
     */
    public synchronized void setImage(PImage image) {
        // Now we can store it
        this.image = image;
    }

    /**
     * Execute all tasks for updating shapes.
     */
    private void updateShapes() {
        fieldImage(image);
        ArrayList<LinkedList<SmartPosition>> frame = findFrames();
    }

    /**
     * Maps the image onto the boolean field.
     *
     * @param image The old image.
     */
    private void fieldImage(PImage image) {
        // Go through every pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Set the new pixel white
                field[x][y] = false;
                // We nee a 1 pixel buffer around the edge
                if(x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    field[x][y] = false;
                    continue;
                }
                // Given the color of our pixel
                int color = image.get(x, y);
                Position[] neighbors = PixelTools.fetchRing(x,y);
                // If a neighboring pixel is not the same, set the new pixel dark
                for (int i = 0; i < 8; i++) {
                    if (image.get(neighbors[i].x, neighbors[i].y) != color) {
                        field[x][y] = true;
                        continue;
                    }
                }
            }
        }
    }

    ArrayList<LinkedList<SmartPosition>> findFrames() {
        // A set of frames that could be shapes
        ArrayList<LinkedList<SmartPosition>> frames = new ArrayList<>();
        // Go through every pixel
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                // If we have hit a possible frame
                if (field[x][y]) {
                    // Create a new frame and add the head
                    LinkedList<SmartPosition> frame = new LinkedList<>();
                    field[x][y] = false;
                    frame.push(new SmartPosition(x, y, PixelTools.fetchRing(x, y),0));
                    // Go until even the head is removed
                    while (!frame.isEmpty()) {
                        // If there are more neighbors to check
                        SmartPosition pixel = frame.peek();
                        if (pixel.advance()) {
                            // Get the next neighbors position
                            int neighborX = pixel.neighbors[pixel.current].x;
                            int neighborY = pixel.neighbors[pixel.current].y;
                            // If the neighbor pixel is also the first pixel then add the new shape
                            if (neighborX == x && neighborY == y && frame.size() >= numberOfVerticesMin) {
                                System.out.println("f");
                                frames.add(frame);
                                break;
                            }
                            // If we can advance to another pixel
                            if (field[neighborX][neighborY]) {
                                // Then create a new smart pixel and add it
                                field[neighborX][neighborY] = false;
                                frame.push(new SmartPosition(neighborX, neighborY,
                                        PixelTools.fetchRing(neighborX, neighborY),
                                        (pixel.current + 4) % 8));
                            }
                        }
                        // Otherwise, this pixel is useless
                        else {
                            frame.pop();
                        }
                    }
                }
            }
        }
        return frames;
    }

    /**
     * Take an image and push it onto a physics frame that can then be added to the world.
     *
     * @param frame The list of point that make up the new shape.
     *
     * @return The new shape.
     */
    private FPoly pushFrameToShape(List<PVector> frame) {
        // Set up shape.
        FPoly shape;
        shape = new FPoly();
        shape.setNoStroke();
        shape.setDensity(50);
        shape.setRestitution((float)0.3);
        shape.setFriction((float)0.3);

        // make the shape
        float xMin = frame.get(0).x;
        float xMax = xMin;
        float yMin = frame.get(0).y;
        float yMax = yMin;
        for (PVector v: frame) {
            float xx = v.x;
            float yy = v.y;
            shape.vertex(xx, yy);
            if (v.x < xMin)
                xMin = v.x;
            if (v.x > xMax)
                xMax = v.x;
            if (v.y < yMin)
                yMin = v.y;
            if (v.y > yMax)
                yMax = v.y;
        }
        return shape;
    }

    /**
     * Add new shape structures to the world.
     */
    private synchronized void addShapes() {
        if (shapeSet != null) {
            for (FPoly p : shapeSet) {
                world.add(p);
            }
        }
    }

    /**
     * Select a random color from these random presets.
     *
     * @return The generated color.
     */
    private Color randomColor() {
        Random rand = new Random();
        int select = rand.nextInt(5);
        Color[] c = {new Color(255,0,0),     //red
                     new Color(255,157,0),   //orange
                     new Color(28,255,0),    //green
                     new Color(57,90,255),   //blue
                     new Color(167,0,219)};  //purple

        return c[select];
    }
}
