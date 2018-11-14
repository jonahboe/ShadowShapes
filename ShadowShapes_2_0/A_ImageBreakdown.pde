/****************************************************************************************
 *  getCam():
 *    Get an update from the camera.
 *    No return.
 ****************************************************************************************/
void getCam() {
  // Get the next image.
  if (cam.available() == true) {
    
    // Get new image from camera.
    cam.read();

    // **DISPLAY THIS MOMENT**
    if (displayMoment == 1) {
      image(cam, 0, 0);
    }
    
    // Filter the image to black and white. 
    cam.filter(THRESHOLD);
    
    // Now we want the shadows to be updated based on the new image.
    updateShadows();
  
    // **DISPLAY THIS MOMENT**
    if (displayMoment == 2) {
      image(cam, 0, 0);
    }
  }
  
}



/****************************************************************************************
 *  lineSearch():
 *    Search for hard lines within the image.
 *    No return.
 ****************************************************************************************/
void lineSearch() {
  
  // Set main string not to write to cam image.
  camRead = false;
  
  for (int y = 0; y < (height); y++) {
    for (int x = 0; x < (width); x++) {
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
        color colorNow = color(cam.get(x,y));
        // Only continue if it is white.
        if (colorNow == color(255)) {
          // Go get the 8 locations around the ring, and store them.
          int[][] here = getRing(x, y);
          // Compare each boardering pixel.
          for (int i = 0; i < 8; i++) {
            // If they are different then we have found a line and are done.
            if (colorNow != cam.get(here[i][0],here[i][1])) {
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
  }
  
  // Release restriction on camera writing.
  camRead = true;
  
}



/****************************************************************************************
 *  displayLines():
 *    Displays the lines found by the searcher.
 *    No return.
 ****************************************************************************************/
void displayLines() {
  
  // Create solors to help with display.
  color black = color(0);
  color white = color(255);
  
  // Go through each pixel.
  for (int y = 0; y < (height); y++) {
    for (int x = 0; x < (width); x++) {
      // If there is a line turn it white.
      if (lines[x][y])
        set(x, y, white);
      // Otherwise turn it black.
      else
        set(x, y, black);
    }
  }
  
}