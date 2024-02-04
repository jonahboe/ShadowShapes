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
import java.util.Random;

import data.BitImage;
import data.ImageManip;
import fisica.FWorld;
import fisica.Fisica;
import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

public class ShadowShapes extends PApplet{

    int FINDER_RESOLUTION = 10;
    int FINDER_MAX_DEPTH = 800;
    int MIN_SHAPE_SIZE = 10;

    private Capture video;
    private FWorld world;
    private ShapeFinder finder = new ShapeFinder(null, FINDER_RESOLUTION, FINDER_MAX_DEPTH, MIN_SHAPE_SIZE);

    private PImage initImage;
    private PImage image;

    private Random rand = new Random();

    public static void main(String[] args) {
        PApplet.main("ShadowShapes");
    }

    /****************************************************************************************
     *  Settings():
     *    Set up the display and framerate. Framerate can be higher, but this works fastest.
     ****************************************************************************************/
    @Override
    public void settings(){
        size(1280,720);
    }

    /****************************************************************************************
     *  Setup():
     *    Set up the display and any additional items.
     ****************************************************************************************/
    @Override
    public void setup() {
        // Processing setup
        noStroke();

        // Video setup
        String[] cameras = Capture.list();
        if (cameras.length == 0) {
            println("There are no cameras available for capture.");
            exit();
        } else {
            println("Available cameras:");
            for (int i = 0; i < cameras.length; i++) {
                println(i + ": " + cameras[i]);
            }
        }
        video = new Capture(this, width, height, cameras[0]);
        video.start();   
        while (!video.available());
        video.read();
        initImage = video.get(); // We want only a copy of this, not the refferance.

        // Fisica setup
        Fisica.init(this);
        world = new FWorld();
        world.setGravity(0, 200);
        world.setEdges();
        world.remove(world.top);
        world.remove(world.left);
        world.remove(world.right);
    }

    /****************************************************************************************
     *  draw():
     *    Loop through the main functions of the program.
     *    No return.
     ****************************************************************************************/
    @Override
    public void draw() {
        // Video actions
        if (video.available()) {
            video.read();
            image = ImageManip.diff(this, video, initImage);
            image = ImageManip.mirrorImage(image);

            // Update the finder image
            if(finder.isAlive())
            {
                finder.stopThread = true;
                try
                {
                    finder.join();
                }
                catch (InterruptedException e)
                {
                    // Do nothing
                }
                BitImage bitImage = new BitImage(image);
                finder = new ShapeFinder(bitImage, FINDER_RESOLUTION, FINDER_MAX_DEPTH, MIN_SHAPE_SIZE);
                finder.start();
            }
        }
        image(image, 0, 0, width, height);

        // Fisica action
        world.step();
        world.draw(this);
        

        // Finder action
        if (!finder.isAlive()) {
            if (finder.shape != null && 
                image.get(finder.center.x, finder.center.y) == this.color(255) &&
                world.getBody(finder.center.x, finder.center.y) == null) {
                int[] c = randomColor();
                finder.shape.setFill(c[0],c[1],c[2]);
                world.add(finder.shape);
            }
            BitImage bitImage = new BitImage(this.getGraphics());
            finder = new ShapeFinder(bitImage, FINDER_RESOLUTION, FINDER_MAX_DEPTH, MIN_SHAPE_SIZE);
            finder.start();
        }
    }

    @Override
    public void keyPressed() {
        if (key == ' ') {
            while (!video.available());
            video.read();
            initImage = video.get();
        }
    }

    /****************************************************************************************
     *  randomColor():
     *    Select a random color from these random presets.
     *    Return the color.
     ****************************************************************************************/
    private int[] randomColor() {
        int select = rand.nextInt(5);
        int c[][] = {{255,0,0},     //red
                     {255,157,0},   //orange
                     {28,255,0},    //green
                     {57,90,255},   //blue
                     {167,0,219}};  //purple

        return c[select];
    }
}