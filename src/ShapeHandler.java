import fisica.FPoly;
import fisica.FWorld;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class ShapeHandler implements Runnable {

    // Minimum shape size.
    private int shapeSizeMin = 500;
    // Shape resolution.
    private int shapeRes = 200;

    // Variables for tracking occurrences in thread
    boolean searching;
    // Variables for monitoring shape creation
    FWorld world;
    boolean newShape;
    ArrayList<PVector> frame;
    PImage workingImage;
    int width;
    int height;

    // Default constructor
    ShapeHandler(FWorld world) {
        this.world = world;
        searching = false;
        newShape = false;
        frame = null;

        workingImage = new PImage();
    }

    // Functions for monitoring shape creation
    public boolean getIsNewShape() {
        return newShape;
    }
    public void setIsNewShape(boolean n) {
        newShape = n;
    }
    // Functions for creating shapes

    private void setShapeFrame(ArrayList<PVector> f){
        frame = f;
    }

    // Functions for tracking occurrences in thread
    public boolean getIsSearching() {
        return searching;
    }
    public void startSearch(PImage image) {
        searching = true;
        workingImage = image;
        width = workingImage.width;
        height = workingImage.height;
        new Thread(this).start();
    }
    private void endSearch() {
        searching = false;
    }

    // For finding shapes
    public void run() {

        ArrayList<PVector> newShape;
        newShape = this.lineSearch();

        // If there is a new shape
        if (newShape != null)
        {
            // load the new shape
            this.setShapeFrame(newShape);

            // Let main know
            this.setIsNewShape(true);

            // Get out quick
            this.endSearch();
            return;
        }

        // Let main know that searching has finished.
        this.endSearch();
    }

    /****************************************************************************************
     *  finderLineSearch():
     *    Search for hard lines within the image.
     *    Return array of vertecies.
     ****************************************************************************************/
    private ArrayList<PVector> lineSearch() {

        ArrayList<PVector> newShape = new ArrayList<>();

        // lines will show up true.
        boolean lines[][] = new boolean[width][height];

        // We'll go through each pixel and check if it is part of a line. We put it in an array.
        // This means that the pixle will be different from at least one other near it.
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
                    int colorNow = workingImage.get(x,y);
                    // Look around this pixel
                    ArrayList<PVector> ring = fetchRing(x, y);
                    for (int i = 0; i < 8; i++) {
                        int xPos = (int)ring.get(i).x;
                        int yPos = (int)ring.get(i).y;

                        // If they are different then we have found a line and are done.
                        if (colorNow != workingImage.get(xPos, yPos)) {
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
                    // we'll track position by x, y, and current recursion cycle.
                    PVector pos = new PVector(x,y,0);
                    // Trace and return the shape if there is one.
                    if (this.finderRecShape(newShape, lines, pos))
                    {
                        this.shapeSetResolution(newShape);
                        return newShape;
                    }
                }
            }
        }

        return null;
    }

    /****************************************************************************************
     *  finderRecShape():
     *    Search shape from boolean data matrix.
     *    Return shape found (true/false).
     ****************************************************************************************/
    private boolean finderRecShape(ArrayList<PVector> newShape, boolean lines[][], PVector start) {

        // Set up some variables
        boolean shaping = true;
        PVector next = start;

        System.out.println("Hit new possible shape structure...");
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
                        System.out.println("  Closing shape build. Fault: 1");
                        System.out.println("  Dumping data.");
                        return false;
                    }

                    // Exit the for loop
                    i = 8;
                }

                // If we returned to start of the shape
                else if (p.x == start.x && p.y == start.y)
                {
                    // Fits the size
                    if (newShape.size() > shapeSizeMin)
                    {
                        System.out.println("  Successfully created new shape.");
                        shapeSetResolution(newShape);
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

                // If this path was a dud based on current item
                else if (i == 7)
                {
                    // Go back a pixel
                    newShape.remove(newShape.size()-1);
                    if (newShape.size() != 0)
                        next = newShape.get(newShape.size()-1);
                    else
                    {
                        System.out.println("  Closing shape build. Fault: 2");
                        System.out.println("  Dumping data.");
                        return false;
                    }

                    // Exit the for loop
                    i = 8;
                }

            }

        }

        // Should something unexpected happen cont.
        System.out.println("  Closing Fault: 3");
        return false;

    }

    /****************************************************************************************
     *  shapeSetResolution():
     *    Take an image and remove all but every nth vertecy.
     *    No return.
     ****************************************************************************************/
    private void shapeSetResolution(ArrayList<PVector> newShape)
    {

        ArrayList<PVector> tempShape = new ArrayList<>();

        // Reduce resolution of shapes
        int i = 0;
        while (i < newShape.size())
        {
            tempShape.add(newShape.get(i));
            i += shapeRes;
        }

        // Put the new one back
        newShape = tempShape;

    }

    /****************************************************************************************
     *  pushToFrame():
     *    take an image and push it onto a physics frame that can then be added to the world.
     ****************************************************************************************/
    public void pushFrameToShape() {
        // Set up shape.
        FPoly shape;
        shape = new FPoly();
        shape.setNoStroke();
        int c = randomColor();
        shape.setDensity(50);
        shape.setRestitution((float)0.3);
        shape.setFriction((float)0.3);
        shape.setFill(c);

        // make the shape
        float xMin = frame.get(0).x;
        float xMax = xMin;
        float yMin = frame.get(0).y;
        float yMax = yMin;
        for (PVector v: frame) {
            shape.vertex(v.x, v.y);
            if (v.x < xMin)
                xMin = v.x;
            if (v.x > xMax)
                xMax = v.x;
            if (v.y < yMin)
                yMin = v.y;
            if (v.y > yMax)
                yMax = v.y;
        }

        // if a shape isn't here already
        if (world.getBody((xMin+xMax)/2, (yMin+yMax)/2) == null)
        {
            // Add the shape to the world family
            world.add(shape);
        }
    }

    /****************************************************************************************
     *  fetchRing():
     *    Get the coordinates of the pixels around a given pixel
     *    Return an array of coordinates.
     ****************************************************************************************/
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

    /****************************************************************************************
     *  randomColor():
     *    Select a random color from these random presets.
     *    Return the color.
     ****************************************************************************************/
    private int randomColor() {

        Random rand = new Random();
        int select = rand.nextInt(5);
        Color c[] = {new Color(255,0,0),     //red
                     new Color(255,157,0),   //orange
                     new Color(28,255,0),    //green
                     new Color(57,90,255),   //blue
                     new Color(167,0,219)};  //purple

        return c[select].getRGB();

    }
}
