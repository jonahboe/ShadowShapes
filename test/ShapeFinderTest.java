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
import fisica.FCircle;
import fisica.FWorld;
import fisica.Fisica;
import processing.core.PApplet;

public class ShapeFinderTest extends PApplet{

    // #region parameters

    int FINDER_RESOLUTION = 10;
    int FINDER_MAX_DEPTH = 20;

    FWorld world;

    // #endregion parameters

    int mode = 0;
    Point startPoint = new Point(0,0);

    public static void main(String[] args) {
        PApplet.main("ShapeFinderTest");
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
        background(255);
        noStroke();

        Fisica.init(this);
        world = new FWorld();
        world.setGravity(0, 200);
        world.setEdges();
        for (int i = 0; i < 10; i++) {
            FCircle c = new FCircle(40);
            c.setNoStroke();
            c.setFill(255,0,0);
            c.setPosition(640, 360);
            c.setVelocity(0, 400);
            c.setRestitution((float)0.3);
            c.setDamping(0);
            world.add(c);
        }
    }

    /****************************************************************************************
     *  draw():
     *    Loop through the main functions of the program.
     *    No return.
     ****************************************************************************************/
    @Override
    public void draw() {
        switch(mode){
            case 0:
                if (mousePressed) {
                    fill(0);
                    ellipse(mouseX, mouseY, 80, 80);
                }
                break;
            case 1:
                background(255);
                break;
            case 2:
                background(255);
                mode = 0;
                break;
            case 3:
                ShapeFinder finder = new ShapeFinder(this.getGraphics(), 10, 800, false, true);
                finder.start();

                while (finder.isAlive()) {
                    // Do nothing
                }
                if (finder.shape != null) {
                    finder.shape.setFill(200);
                    world.add(finder.shape);
                    mode = 1;
                }
                else {
                    mode = 0;
                }
                break;
            default:
                break;
        }

        world.step();
        world.draw(this);
    }

    @Override
    public void keyPressed() {
        switch (key) {
            case 'd':
                mode = 0;
                break;
            case 'p':
                mode = 1;
                break;
            case 'r':
                mode = 2;
                break;
            case ' ':
                mode = 3;
                break;
        }
    }

}