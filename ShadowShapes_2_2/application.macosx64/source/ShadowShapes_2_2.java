import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 
import fisica.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ShadowShapes_2_2 extends PApplet {

/****************************************************************************************
 *  SHADOW SHAPES 2.2
 *    By: Jonah Boe
 *    
 *  This is a program which uses a conected camera to recognize shadows within its
 *    viewing angle. These shadows are then traced using a triganomic process, in
 *    order to identify any closed spaces (or shapes) formed by the shadows. The
 *    shapes are then stored as virtual physics objects which can then be interacted
 *    with.
 *
 *  Any use or reproduction of the code provided in this project without written
 *    concent is prohibeted.
 *
 *  Enjoy!!! :)
 ****************************************************************************************/

// Include the libraries for the cammera

Capture cam;

// Include fisica library

FWorld world;

// Our object for the shape finder
ShapeFinder shapeFinder;
// Image for shape creation
PImage shadowsSF = null;

// This will store the shadow image
PImage shadows = new PImage(1280, 720);
// This will hold the array of shadows.
ArrayList<FBox> shadowsPH = null;
// Resolution of the shadow blocks
int shadowRes = 25;

// Minimum shape size.
int shapeSizeMin = 500;
// Shape resolution.
int shapeRes = 200;



/****************************************************************************************
 *  Setup():
 *    Set up the display and any additional items.
 ****************************************************************************************/
public void setup() {
  // Set up the program screen size
  
  
  // Set up cammera
  String[] cameras = Capture.list();
  if (cameras.length == 0) {
    println("There are no cameras available for capture.");
    exit();
  } else {
    println("Available cameras:");
    for (int i = 0; i < cameras.length; i++) {
      println(cameras[i]);
    }
    cam = new Capture(this, cameras[0]);
    cam.start(); 
  }
  
  // Set up world
  Fisica.init(this);
  world = new FWorld();
  world.setGravity(0, 800);
  world.setEdges();
  world.remove(world.left);
  world.remove(world.right);
  world.remove(world.top);
  world.setEdgesRestitution(0.5f);
  
  // Set up our shape finder
  shapeFinder = new ShapeFinder();
  
  // Creat array lists for the shadows.
  shadowsPH = new ArrayList<FBox>();
  
  // We need to start with something to read
  shadows.loadPixels();
  for (int i = 0; i < shadows.pixels.length; i++) {
    shadows.pixels[i] = color(255); 
  }
  shadows.updatePixels();
  
}



/****************************************************************************************
 *  draw():
 *    Loop through the main functions of the program.
 *    No return.
 ****************************************************************************************/
public void draw() {
  
  // Get new image content if cam is not in use.
  if (cam.available()) {
    // Grab new image
    cam.read();
    cam.filter(THRESHOLD);
    shadows = mirrorimage(cam);
    image(shadows, 0, 0);
    
    // Update the shadow world
    updateShadows();
  }
  
  // If there isn't a surch currently going...
  if (!shapeFinder.getIsSearching()) {
    // And there is a new shape...
    if (shapeFinder.getIsNewShape()) {
      // Make a shape out of the frame.
      shapeFinder.pushFrameToShape();
      // And let the shapeFinder thread know we are good to go again.
      shapeFinder.setIsNewShape(false);
    }
    // Start a search.
    shapeFinder.startSearch(shadows);
  }
  
  // Update the shapes world
  world.draw();
  world.step();
  
}
/****************************************************************************************
 *  updateShadows():
 *    Update the physical structures that are placeholders for the shadows.
 *    No return.
 ****************************************************************************************/
public void updateShadows() {
  
  // Remove old shadows from world
  for (FBox s: shadowsPH) {
    world.remove(s);
  }
  
  // Kill the old shadow structures
  for (int i = shadowsPH.size() - 1; i >= 0; i--) {
    shadowsPH.remove(i);
  }
  
  // Create a new shadow structures
  FBox newShadow;
  for (int y = 0; y < width; y += shadowRes) {
    for (int x = 0; x < width; x += shadowRes) {
      if (shadows.get(x, y) == color(0)) {
        newShadow = new FBox(shadowRes, shadowRes);
        // Set "y" a little lower so it will be flush or lower than shadow tops
        newShadow.setPosition(x, y + shadowRes/2);
        newShadow.setStatic(true);
        newShadow.setNoFill();
        newShadow.setNoStroke();
        newShadow.setRestitution(0);
        shadowsPH.add(newShadow);
      }
    }
  }
  
  // Add new shadow structures to the world.
  for (FBox s: shadowsPH) {
    world.add(s);
  }
   
}
/****************************************************************************************
 *  ShapeFinder():
 *    A class for an abstract tool which can take care of shape creation.
 *
 *    This class relies on public function findShapes() (see below)
 ****************************************************************************************/
public class ShapeFinder {
  
  // Variables for tracking occurances in thread
  boolean searching;
  // Variables for monitoring shape creation
  boolean newShape;
  ArrayList<PVector> frame;
  
  // Defult constructor
  ShapeFinder() {
    searching = false;
    newShape = false;
    frame = null;
  }
  
  // Functions for tracking occurances in thread
  public boolean getIsSearching() {
    return searching;
  }
  public void startSearch(PImage s) {
    searching = true;
    shadowsSF = s;
    thread("findShapes");
  }
  public void endSearch() {
    searching = false;
  }
  
  // Functions for monitoring shape creation
  public boolean getIsNewShape() {
    return newShape;
  }
  public void setIsNewShape(boolean n) {
    newShape = n;
  }
  
  // Functions for creating shapes
  public void setShapeFrame(ArrayList<PVector> f){
    frame = f;
  }
  
  // Take the frame and make a shape
  public void pushFrameToShape() {
    // Set up shape.
    FPoly shape;
    shape = new FPoly();
    shape.setNoStroke();
    int c = randomColor();
    shape.setFill(red(c),green(c),blue(c));
    shape.setDensity(10);
    shape.setRestitution(0.5f);
    shape.setFriction(0.3f);
    
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
  
}



/****************************************************************************************
 *  findShapes():
 *    Go through all of the steps of finding and creating a shape.
 *    No return.
 ****************************************************************************************/
public void findShapes() {
  
  ArrayList<PVector> newShape;
  newShape = finderLineSearch();
  
  // If there is a new shape 
  if (newShape != null)
  {
    // load the new shape
    shapeFinder.setShapeFrame(newShape);
    
    // Let main know
    shapeFinder.setIsNewShape(true);

    // Get out quick
    shapeFinder.endSearch();
    return;
  }
  
  // Some test squares
  if (mousePressed) {
    // Make shape
    newShape = new ArrayList<PVector>();
    newShape.add(new PVector(mouseX-25,mouseY-25));
    newShape.add(new PVector(mouseX+25,mouseY-25));
    newShape.add(new PVector(mouseX+25,mouseY+25));
    newShape.add(new PVector(mouseX-25,mouseY+25));
    
    // load the new shape
    shapeFinder.setShapeFrame(newShape);
    // Let main know
    shapeFinder.setIsNewShape(true);
    
    // Get out quick
    shapeFinder.endSearch();
    return;
  }
  
  // Let main know that searching has finished.
  shapeFinder.endSearch();
  
}
/****************************************************************************************
 *  finderLineSearch():
 *    Search for hard lines within the image.
 *    Return array of vertecies.
 ****************************************************************************************/
public ArrayList<PVector> finderLineSearch() {
  
  ArrayList<PVector> newShape;
  newShape = new ArrayList<PVector>();
  
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
      // We want the right and left collums to just be false.
      else if (x < 2 || x > (width - 3)) {
        lines[x][y] = false;
      }
      // We only need to look for shapes in the main part of the image.
      // We will compare each pixel to those around it.
      else {
        // Set up a variable to hold the color of the current pixel.
        int colorNow = shadowsSF.get(x,y);
        // Look around this pixel
        ArrayList<PVector> ring = fetchRing(x, y);
        for (int i = 0; i < 8; i++) {
          int xPos = PApplet.parseInt(ring.get(i).x);
          int yPos = PApplet.parseInt(ring.get(i).y);
            
          // If they are different then we have found a line and are done.
          if (colorNow != shadowsSF.get(xPos, yPos)) {
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
        if (finderRecShape(newShape, lines, pos))
        {
          shapeSetResolution(newShape);
          return newShape;
        }
      }
    }
  }
  
  return null;
}



/****************************************************************************************
 *  finderRecShape():
 *    Search shape frome boolean data matrix.
 *    Return shape found (true/false).
 ****************************************************************************************/
public boolean finderRecShape(ArrayList<PVector> newShape, boolean lines[][], PVector start) {
 
  // Set up some variables
  boolean shaping = true;
  PVector next = start;
  
  println("Hit shape...");
  newShape.add(next);
  
  while(shaping) {
    
    // Set this location to false so it wont be searched again
    lines[PApplet.parseInt(next.x)][PApplet.parseInt(next.y)] = false;
    
    // Get the loop around our point
    ArrayList<PVector> ring = fetchRing(PApplet.parseInt(next.x), PApplet.parseInt(next.y));
    for (int i = PApplet.parseInt(next.z); i < 8; i++) {
      PVector p = ring.get(i);
      
      // If this path was a dud based on last item
      if (PApplet.parseInt(next.z) == 7)
      {
        // Go back a pixel if posible
        newShape.remove(newShape.size()-1);
        if (newShape.size()-1 != 0)
          next = newShape.get(newShape.size()-1);
        else
        {
          println("  Closing Fualt: 1");
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
          println("  Success");
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
      else if (lines[PApplet.parseInt(p.x)][PApplet.parseInt(p.y)])
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
          println("  Closing Fault: 2");
          return false;
        }
        
        // Exit the for loop
        i = 8;
      }
      
    }
    
  }
  
  // Should something unexpected happen cont.
  println("  Closing Fault: 3");
  return false;
  
}



/****************************************************************************************
 *  shapeSetResolution():
 *    Take an image and remove all but every nth vertecy.
 *    No return.
 ****************************************************************************************/
public void shapeSetResolution(ArrayList<PVector> newShape)
{

  ArrayList<PVector> tempShape = new ArrayList<PVector>();
  
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
 *  mirrorimage():
 *    Take an image and mirror it.
 *    Return the mirrored image.
 ****************************************************************************************/
public PImage mirrorimage(PImage initial) {
  
  // Set up our bounds
  int this_x = initial.width;
  int this_y = initial.height;
  
  // Make a new image of the same size
  PImage mirror = new PImage(this_x, this_y);
  
  // mirror the image
  for (int y = 0; y < this_y; y++) {
    for (int x = 0; x < this_x; x++) {
      mirror.set(this_x - 1 - x, y, initial.get(x, y));
    } 
  }
  
  // return the image
  return mirror;
  
}



/****************************************************************************************
 *  randomColor():
 *    Select a random color from these random presets.
 *    Return the color.
 ****************************************************************************************/
public int randomColor() {
  
  randomSeed(millis());
  int select = floor(random(0,5));
  int c[] = {color(255,0,0),     //red
               color(255,157,0),   //orange
               color(28,255,0),    //green
               color(57,90,255),   //blue
               color(167,0,219)};  //purple
                
  return c[select];
  
}



/****************************************************************************************
 *  fetchRing():
 *    Get the cordinates of the pixles around a given pixel
 *    Return an array of cordinates.
 ****************************************************************************************/
public ArrayList<PVector> fetchRing(int x, int y)
{
  
  ArrayList<PVector> l = new ArrayList<PVector>();
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
  public void settings() {  size(1280, 720, P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ShadowShapes_2_2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
