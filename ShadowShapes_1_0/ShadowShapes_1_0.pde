import processing.video.*;
Capture video;

import gab.opencv.*;
OpenCV opencv;
PImage backDrop;
PImage current;
boolean removeCurrent = false;



/******************************************************************************
*************** THESE VARIABLES ARE HERE TO BE ADJUSTED BY USER ***************
******************************************************************************/

// Line presance threshhold
int thresh = 200;
// Color value setting (Has to be at least 1 less than thresh & less than 200)
int call = 199;
// Smallest shape allowed
int shapeMin = 200;
// Show shapefinding in real-time?
boolean showBackDrop = true;
// Number of shapes allowed
int total = 30;

/******************************************************************************
*******************************************************************************
******************************************************************************/



// Shape variables
PShape shapeStack[] = new PShape[total];
float PSLock[][] = new float[2][total];
float PSVert[] = new float[total];
float PSHora[] = new float[total];
boolean freeSpace[] = new boolean[total];



void setup() {
  fullScreen();
  noCursor();
  
  println("Setting up video...");
  video = new Capture(this, width, height);
  video.start();
  opencv = new OpenCV(this, video);
  println("Done\t");
  
  // As the first global variable checked this has to be pre-cleared.
  for (int i = 0; i < total; i++) {
    freeSpace[i] = true;
  }
  
  // Clear backdrop at start.
  backDrop = createImage(width, height, RGB);
  
  thread("finderMain");
  
}



void draw() {
  
  background(0);
 
  if (showBackDrop) image(backDrop, 0, 0);
    
  for (int i = 0; i < total; i++) {
    if (!freeSpace[i]) {
      shape(shapeStack[i], PSLock[0][i], PSLock[1][i]);
    
      PSLock[0][i] += PSHora[i];
    
      shapeStack[i].rotate(PSHora[i]/20);
    
      PSVert[i] -= 0.8;
      PSLock[1][i] -= PSVert[i];
      
      if (PSLock[1][i] > height + 40) {
        freeSpace[i] = true;
      }
    }
  }
  
  if (removeCurrent) {
    
    loadPixels();
    backDrop.loadPixels();
    for (int p = 0; p < pixels.length; p++) {
      if (pixels[p] != color(0, 0, 0)) {
        backDrop.pixels[p] = color(call, 0, 0);
      }
    }
    backDrop.updatePixels();
    removeCurrent = false;
    
  }
      
}