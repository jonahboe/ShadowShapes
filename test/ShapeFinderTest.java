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
import data.BitImage;
import fisica.FWorld;
import fisica.Fisica;
import processing.core.PApplet;

public class ShapeFinderTest extends PApplet{

    // #region parameters

    int FINDER_RESOLUTION = 10;
    int FINDER_MAX_DEPTH = 800;

    FWorld world;
    ShapeFinder finder = new ShapeFinder(null, 10, 800, 2);

    // #endregion parameters

    int mode = 0;

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
        world.setGravity(0, 400);
        world.setEdges();
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
                break;
            case 2:
                background(255);
                break;
            case 3:
                if (!finder.isAlive()) {
                    if (finder.shape != null) {
                        if (world.getBody(finder.center.x, finder.center.y) == null) {
                            finder.shape.setFill(200);
                            world.add(finder.shape);
                        }
                        mode = 2;
                        break;
                    }
                    else {
                        BitImage image = new BitImage(this.get());
                        finder = new ShapeFinder(image, 10, 800, 2);
                        finder.start();
                    }
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