import fisica.FPoly;
import fisica.FWorld;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
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
    private final int width;
    private final int height;
    private List<FPoly> newShapes;

    /**
     * Constructor.
     *
     * @param applet The applet controlling this handler.
     * @param world The physics world being drawn to.
     * @param imageResolution How much to scale the image down before searching.
     * @param numberOfVerticesMin The minimum number of points a shape must have.
     */
    ShapeHandler(ShadowShapes applet, FWorld world, float imageResolution, int numberOfVerticesMin) {
        this.applet = applet;
        this.world = world;
        this.imageResolution = imageResolution;
        this.numberOfVerticesMin = numberOfVerticesMin;
        this.width = 0;
        this.height = 0;
    }

    /**
     * This is where our thread will start.
     */
    @Override
    public final void run() {
        while(true) {
            try {
                Thread.sleep(200);
                setImage(applet.getImage());
                lineSearch();
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
     * Add shapes into the physics world.
     */
    public synchronized void addShapes() {
        // Lets add our new shapes
        if (newShapes != null) {
            for (FPoly shape : newShapes)
                world.add(shape);
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
     * Search for hard lines within the image.
     *
     * @return An array of vertices.
     */
    private List<PVector> lineSearch() {

        List<PVector> newFrame = new ArrayList<>();

        // lines will show up true.
        boolean lines[][] = new boolean[width][height];

        // We'll go through each pixel and check if it is part of a line. We put it in an array.
        // This means that the pixel will be different from at least one other near it.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // We want the first and last rows to just be false.
                if (y < 2 || y > (height - 3)) {
                    lines[x][y] = false;
                }
                // We want the right and left columns to just be false.
                else if (x < 2 || x > (width - 3)) {
                    lines[x][y] = false;
                }
                // We only need to look for shapes in the main part of the image.
                // We will compare each pixel to those around it.
                else {
                    // Set up a variable to hold the color of the current pixel.
                    int colorNow = image.get(x,y);
                    // Look around this pixel
                    ArrayList<PVector> ring = fetchRing(x, y);
                    for (int i = 0; i < 8; i++) {
                        int xPos = (int)ring.get(i).x;
                        int yPos = (int)ring.get(i).y;

                        // If they are different then we have found a line and are done.
                        if (colorNow != image.get(xPos, yPos)) {
                            // It is true, now bail.
                            lines[x][y] = true;
                            i = 8;
                        }
                        // Otherwise it isn't a line and we need to continue.
                        else {
                            lines[x][y] = false;
                        }
                    }
                }
            }
        }

        // Now we want to use the lines we have found to look for shapes.
        // This will be done by following the lines in a recursive search.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // If you hit a line.
                if (lines[x][y]) {
                    // we'll track position by x, y, and current cycle.
                    PVector pos = new PVector(x,y,0);
                    // Trace and return the shape if there is one.
                    if (this.finderRecShape(newFrame, lines, pos))
                    {
                        FPoly shape = pushFrameToShape(newFrame);
                        Color c = randomColor();
                        shape.setFill(c.getRed(), c.getGreen(), c.getBlue());
                        newShapes.add(shape);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Search shape from boolean data matrix.
     *
     * @return The if a shape was found.
     */
    private boolean finderRecShape(List<PVector> newShape, boolean lines[][], PVector start) {

        // Set up some variables
        boolean shaping = true;
        PVector next = start;

        newShape.add(next);

        while(shaping) {

            // Set this location to false so it wont be searched again
            lines[(int)next.x][(int)next.y] = false;

            // Get the loop around our point
            ArrayList<PVector> ring = fetchRing((int)next.x, (int)next.y);
            for (int i = (int)next.z; i < 8; i++) {
                PVector p = ring.get(i);

                // If this path was a dud based on last item
                if ((int)next.z == 7)
                {
                    // Go back a pixel if possible
                    newShape.remove(newShape.size()-1);
                    if (newShape.size()-1 != 0)
                        next = newShape.get(newShape.size()-1);
                    else
                    {
                        return false;
                    }

                    // Exit the for loop
                    i = 8;
                }

                // If we returned to start of the shape
                else if (p.x == start.x && p.y == start.y)
                {
                    // Fits the size
                    if (newShape.size() > numberOfVerticesMin)
                    {
                        System.out.println("Found shape");
                        return true;
                    }

                    // Else we are on a bad path
                    else
                    {
                        // Go back a pixel
                        newShape.remove(newShape.size()-1);
                        next = newShape.get(newShape.size()-1);
                        // Exit the for loop
                        i = 8;
                    }
                }

                // Otherwise just continue continue
                else if (lines[(int)p.x][(int)p.y])
                {
                    // Set "z" to where we where on the ring.
                    newShape.get(newShape.size()-1).z = i;
                    // Add new shape and advance
                    newShape.add(new PVector(p.x, p.y, 0));
                    next = newShape.get(newShape.size()-1);
                    i = 8;
                }

                // If this path was dad based on current item
                else if (i == 7)
                {
                    // Go back a pixel
                    newShape.remove(newShape.size()-1);
                    if (newShape.size() != 0)
                        next = newShape.get(newShape.size()-1);
                    else
                    {
                        return false;
                    }

                    // Exit the for loop
                    break;
                }
            }
        }

        // Should something unexpected happen cont.
        return false;

    }

    /**
     * Take an image and push it onto a physics frame that can then be added to the world.
     *
     * @param frame The list of point that make up the new shape.
     *
     * @return The new shape.
     */
    public FPoly pushFrameToShape(List<PVector> frame) {
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
     * Get the coordinates of the pixels around a given point.
     *
     * @param x Coordinate of point.
     * @param y Coordinate of point.
     * @return An array list of points around the location.
     */
    private ArrayList<PVector> fetchRing(int x, int y)
    {
        ArrayList<PVector> l = new ArrayList<>();
        l.add(new PVector(x-1,y-1));
        l.add(new PVector(x,y-1));
        l.add(new PVector(x+1,y-1));
        l.add(new PVector(x+1,y));
        l.add(new PVector(x+1,y+1));
        l.add(new PVector(x,y+1));
        l.add(new PVector(x-1,y+1));
        l.add(new PVector(x-1,y));
        return l;
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
