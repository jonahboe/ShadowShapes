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
import processing.core.PApplet;

public class FinderTest extends PApplet{

    int mode = 0;
    Point startPoint = new Point(0,0);

    public static void main(String[] args) {
        PApplet.main("FinderTest");
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
        // Setup the camera
        background(255);
        noStroke();
        frameRate(20);
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
                if (mousePressed) {
                    startPoint = new Point(mouseX, mouseY);
                    fill(color(255,0,0));
                    ellipse(mouseX, mouseY, 5, 5);
                }
                break;
            case 2:
                background(255);
                mode = 0;
                break;
            case 3:
                Finder finder = new Finder(this, 10, 80);
                finder.findBoarder(startPoint, 0);
                stroke(color(255,0,255));
                for (int i = 0; i < finder.boarder.size() - 1; i++) {
                    line(finder.boarder.get(i).x,finder.boarder.get(i).y,
                         finder.boarder.get(i+1).x,finder.boarder.get(i+1).y);
                }
                line(finder.boarder.get(0).x,finder.boarder.get(0).y,
                     finder.boarder.get(finder.boarder.size() - 1).x,finder.boarder.get(finder.boarder.size() - 1).y);
                noStroke();
                mode = 0;
                break;
            default:
                break;
        }
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