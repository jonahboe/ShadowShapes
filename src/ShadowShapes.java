/*
 SHADOW SHAPES 3.0
 By: Jonah Boe

 This is a program which uses a camera to recognize shadows. These shadows are then
 traced in order to identify any closed spaces (or shapes) formed by the shadows.
 The shapes are then stored as virtual physics objects which can then be interacted
 with.

 Any use or reproduction of the code provided in this project for personal gain and
 without written consent is prohibited.

 Enjoy!!! :)
 */

import fisica.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

/**
 * Main class.
 */
public class ShadowShapes extends PApplet {

    // Some things we might want to adjust later
    private static final int SHAPE_RESOLUTION = 40;
    private static final int SHAPE_MIN_VERTICES = 500;
    private static final int SHADOW_RESOLUTION = 25;
    
    // We are going to need a camera
    private static Capture myCamera;
    // This will be our world of physics
    private static FWorld world;
    // Our object for the shape finder
    private static ShapeHandler shapeHandler;
    // This will handle shadows interacting with shapes
    private static ShadowHandler shadowHandler;
    // This will store the shadow image
    private static PImage mirroredImage;

    /**
     * Method for this class and starting the application.
     *
     * @param args Arguments passed in. Should be none.
     */
    public static void main(String[] args) {
        PApplet.main("ShadowShapes");
    }

    /**
     * Set up the display and framerate. Framerate can be higher, but this works fastest.
     */
    @Override
    public void settings(){
        //fullScreen();
        size(1280,720);
    }

    /**
     * Set up the display and any additional items.
     */
    @Override
    public void setup() {
        // Set the frame rate for development
        frameRate(20);

        // Setup the camera
        String[] cameras = Capture.list();
        if (cameras.length == 0) {
            println("There are no cameras available for capture.");
            exit();
        } else {
            println("Available cameras:");
            for (int i = 0; i < cameras.length; i++) {
                System.out.println(cameras[i]);
            }
            myCamera = new Capture(this, width, height, cameras[0]);
            myCamera.start();
        }

        // Setup the physics world
        Fisica.init(this);
        world = new FWorld();
        world.setGravity(0, 1000);
        world.add(world.bottom);
        world.setEdgesRestitution((float)0.5);

        // Setup our image for the current image being worked on
        mirroredImage = new PImage(width, height);

        // Set up our shape finder
        shapeHandler = new ShapeHandler(this, world, SHAPE_RESOLUTION, SHAPE_MIN_VERTICES);
        Thread shapeHandlerThread = new Thread(shapeHandler);
        shapeHandlerThread.start();

        // Setup the shadow handler
        shadowHandler = new ShadowHandler(this, world, SHADOW_RESOLUTION);
        Thread shadowHandlerThread = new Thread(shadowHandler);
        shadowHandlerThread.start();
    }

    /**
     * Loop through the main functions of the program.
     */
    @Override
    public void draw() {
        // Get new image content if cam is ready.
        if (myCamera.available()) {
            // Grab new image
            myCamera.read();
            myCamera.filter(THRESHOLD, (float) 0.5);
            setImage(ImageTools.mirrorImage(myCamera));
            drawImage();
        }

        // Update the shapes world
        updateWorld();
    }

    /**
     * Advance all of the shapes and then draw them.
     */
    public synchronized void updateWorld() {
        try {
            world.draw();
            world.step();
        }
        catch (AssertionError e) {
            // Assertion error may occur if shape is invalid, but won't cause problems
        }
    }

    /**
     * Draw the mirrored image from the capture.
     */
    public synchronized void drawImage() {
        image(mirroredImage, 0, 0);
    }
    /**
     * Gets a copy of the mirrored image capture.
     *
     * @return PImage containing a copy of the mirrored Image.
     */
    public synchronized PImage getImage() {
        return mirroredImage.copy();
    }

    /**
     * Set the mirrored image.
     */
    public synchronized void setImage(PImage image) {
        mirroredImage = image;
    }

}