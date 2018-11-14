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
import processing.video.*;
Capture cam;

// Include fisica library
import fisica.*;
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
void setup() {
  // Set up the program screen size
  size(1280, 720, P2D);
  
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
  world.setEdgesRestitution(0.5);
  
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
void draw() {
  
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
