/****************************************************************************************
 *  SHADOW SHAPES 2.3
 *    By: Jonah Boe
 *
 *  This is a program which uses a connected camera to recognize shadows within its
 *    viewing angle. These shadows are then traced in order to identify any closed
 *    spaces (or shapes) formed by the shadows. The shapes are then stored as virtual
 *    physics objects which can then be interacted with.
 *
 *  Any use or reproduction of the code provided in this project for personal gain
 *    without written consent is prohibited.
 *
 *  Enjoy!!! :)
 ****************************************************************************************/
import fisica.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

public class ShadowShapes extends PApplet{

    // We are going to need a camera
    private static Capture myCamera;
    // This will be our world of physics
    private static FWorld world;
    // Our object for the shape finder
    private static ShapeHandler shapeHandler;
    // This will handle shadows interacting with shapes
    private static ShadowHandler shadowHandler;
    // This will store the shadow image
    private static PImage shadows;


    public static void main(String[] args) {
        PApplet.main("ShadowShapes");
    }

    /****************************************************************************************
     *  Settings():
     *    Set up the display and framerate. Framerate can be higher, but this works fastest.
     ****************************************************************************************/
    public void settings(){
        size(1280,720);
    }

    /****************************************************************************************
     *  Setup():
     *    Set up the display and any additional items.
     ****************************************************************************************/
    public void setup() {
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
            myCamera = new Capture(this, cameras[0]);
            myCamera.start();
        }

        // Setup the physics world
        Fisica.init(this);
        world = new FWorld();
        world.setGravity(0, 1000);
        world.setEdges();
        world.remove(world.left);
        world.remove(world.right);
        world.remove(world.top);
        world.setEdgesRestitution((float)0.5);

        // Set up our shape finder
        shapeHandler = new ShapeHandler(world);

        // Setup the shadow handler
        shadowHandler = new ShadowHandler(world);

        // Setup our image for the current image being worked on
        shadows = new PImage(1280, 720);
    }

    /****************************************************************************************
     *  draw():
     *    Loop through the main functions of the program.
     *    No return.
     ****************************************************************************************/
    public void draw() {
        // Get new image content if cam is not in use.
        if (myCamera.available()) {
            // Grab new image
            myCamera.read();
            myCamera.filter(THRESHOLD);
            shadows = new ImageHandler().mirrorImage(myCamera);
            image(shadows, 0, 0);
            // Update where the shadows are located
            shadowHandler.updateShadows(shadows);
        }

        // If there isn't a search currently going...
        if (!shapeHandler.getIsSearching()) {
            // And there is a new shape...
            if (shapeHandler.getIsNewShape()) {
                // Make a shape out of the frame.
                shapeHandler.pushFrameToShape();
                // And let the shapeHandler thread know we are good to go again.
                shapeHandler.setIsNewShape(false);
            }
            // Start a search.
            shapeHandler.startSearch(shadows);
        }

        // Update the shapes world
        world.draw();
        world.step();
    }

}