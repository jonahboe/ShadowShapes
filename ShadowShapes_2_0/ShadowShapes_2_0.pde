/****************************************************************************************
 *  SHADOW SHAPES
 *    By: Jonah Boe
 *    
 *  This is a program which uses a conected camera to recognize shadows within its
 *    viewing angle. These shadows are then traced using a triganomic process, in
 *    order to identify any closed spaces (or shapes) formed by the shadows. The
 *    shapes are then stored digitaly in virtual physics objects which can then
 *    interact with both eachother and the users shadow.
 *
 *  Any use or reproduction of the code provided in this project without written
 *    concent is prohibeted.
 *
 *  Enjoy!!! :)
 ****************************************************************************************/



/****************************************************************************************
 *  Prelim:
 *    Import needed libraries and define public variables.
 ****************************************************************************************/
 
/****************************************************************************************
 These are variables that you may want to adjust depending on personal prefferances.
 ****************************************************************************************/
// What threshold do you want for detecting white/black differances.
int thresh = 40;
// What are the minimum vertecies needed to consider something a shape.
int shapeMinSize = 40;
// Resolution of shapes.
int shapeRes = 5;
// Resolution of shadows.
int shadowRes = 10;

/****************************************************************************************
 These are variables that shouldn't need adjusting, but hey... Go crazy! ;)
 ****************************************************************************************/
// Library for using a camera.
import processing.video.*;

// Set camera object
Capture cam;
boolean camRead = true;

// Setup array of booleans for tracking lines on image.
boolean lines[][];

// This variable defines what point in the project to display. See "void keyPressed()"
//   for details.
int displayMoment = 4;



/****************************************************************************************
 *  Setup():
 *    Set up the display and any additional items.
 ****************************************************************************************/
void setup() {
  // Set up the program screen size.
  size(640, 480, P2D);
  smooth();
  stroke(0);
  strokeWeight(1);
  
  // Run the setup function for our world. See: "B_CreateShapes.cpp".
  worldSetup();

  // Initialize the camera.
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
  
  // Set up lineSet size
  lines = new boolean[width][height];
  
  // Center all shapes
  shapeMode(CENTER);
  
  // Start thread.
  //thread("mainThread");
  
}



/****************************************************************************************
 *  draw():
 *    Loop through the main functions of the program.
 *    No return.
 ****************************************************************************************/
void draw() {
  
  // Get new image content if cam is not in use.
  if (camRead) {
    getCam();
  }
  
  // **DISPLAY THIS MOMENT**
  if (displayMoment == 3) {
    displayLines();
  }
  
  // **DISPLAY THIS MOMENT**
  if (displayMoment == 4) {
    // Display backdrop.
    image(cam, 0, 0);
    // Display shapes and update.
    world.draw();
    world.step();
  }
  
}



/****************************************************************************************
 *  mainThread():
 *    Loop through the main functions of the shape detector.
 *    No return.
 ****************************************************************************************/
void mainThread() {
  // Run forever.
  while (true) {
  
    // Find the lines in the image.
    lineSearch();
    
    // Create the shapes.
    for (int y = 1; y < height - 1; y++) {
      for (int x = 1; x < width - 1; x++) {
        if (lines[x][y]) {
          makeShapes(x, y);
        }
      }
    }
    
  }
}



/****************************************************************************************
 *  theRing():                                             0 1 2
 *    Loop through the main functions of the program.      7 P 3
 *    Return the 8 locations around a given point P.       6 5 4
 ****************************************************************************************/
int[][] getRing(int x, int y) {
  
  int ring[][] = new int[8][2];
  
  ring[0][0] = x - 1; ring[0][1] = y - 1;
  ring[1][0] = x;     ring[1][1] = y - 1;
  ring[2][0] = x + 1; ring[2][1] = y - 1;
  ring[3][0] = x + 1; ring[3][1] = y;
  ring[4][0] = x + 1; ring[4][1] = y + 1;
  ring[5][0] = x;     ring[5][1] = y + 1;
  ring[6][0] = x - 1; ring[6][1] = y + 1;
  ring[7][0] = x - 1; ring[7][1] = y;
  
  return ring;
  
}



/****************************************************************************************
 *  keyPressed():                                             
 *    Sets the viewpoint up based on user input
 *    No return.
 ****************************************************************************************/
void keyPressed() {
  
  char myKey = key;
  
  switch (myKey) {
    // Display the final product.
    case '0':
      displayMoment = 0;
      break;
    // Display origional image.
    case '1':
      displayMoment = 1;
      break;
    // Display black and white image.
    case '2':
      displayMoment = 2;
      break;
    // Display lines found.
    case '3':
      displayMoment = 3;
      break;
    // Display shapes.
    case '4':
      displayMoment = 4;
      break;
  }
  
}



void mousePressed() {
  
  ArrayList<PVector> nat = new ArrayList<PVector>();
  
  PVector a = new PVector(mouseX,mouseY);
  PVector b = new PVector(mouseX+30,mouseY);
  PVector c = new PVector(mouseX+30,mouseY+30);
  PVector d = new PVector(mouseX,mouseY+30);
  
  nat.add(a);
  nat.add(b);
  nat.add(c);
  nat.add(d);
  
  world.add(addShape(nat));
  
}