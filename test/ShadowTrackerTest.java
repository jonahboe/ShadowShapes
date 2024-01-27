import processing.core.PApplet;
import fisica.*;

public class ShadowTrackerTest extends PApplet{

    FWorld world;
    ShadowTracker tracker;

    public static void main(String[] args) {
        PApplet.main("ShadowTrackerTest");
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
        noStroke();
        frameRate(20);

        tracker = new ShadowTracker(5, 300);

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
        background(255);
        fill(0);
        rect(mouseX-50, mouseY-50, 100, 100);

        tracker.trackShadows(this.getGraphics(), true);
        tracker.updateCollisions(world);

        world.step();
        world.draw(this);
    }

    /****************************************************************************************
     *  keyPressed():
     *    Loop through the main functions of the program.
     *    No return.
     ****************************************************************************************/
    @Override
    public void mousePressed() {
        
    }
}
