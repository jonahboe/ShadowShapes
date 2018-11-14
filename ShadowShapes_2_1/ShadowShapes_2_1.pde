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
int shapeSizeMin = 50;
// Shape resolution.
int shapeRes = 10;





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




PImage mirrorimage(PImage initial) {
  
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






void updateShadows() {
  
  // Remove old shadows from world
  for (FBox s: shadowsPH) {
    world.remove(s);
  }
  
  // Kill the old shadow structures
  for (int i = shadowsPH.size()-1; i >= 0; i--) {
    shadowsPH.remove(i);
  }
  
  // Create a new shadow structures
  FBox newShadow;
  for (int y = 0; y < width; y += shadowRes) {
    for (int x = 0; x < width; x += shadowRes) {
      if (shadows.get(x, y) == color(0)) {
        newShadow = new FBox(shadowRes, shadowRes);
        newShadow.setPosition(x, y);
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
