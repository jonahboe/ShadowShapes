/****************************************************************************************
 *  finderLineSearch():
 *    Search for hard lines within the image.
 *    Return array of vertecies.
 ****************************************************************************************/
ArrayList<PVector> finderLineSearch() {
  
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
        color colorNow = shadowsSF.get(x,y);
        // Look around this pixel
        ArrayList<PVector> ring = fetchRing(x, y);
        for (int i = 0; i < 8; i++) {
          int xPos = int(ring.get(i).x);
          int yPos = int(ring.get(i).y);
            
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
boolean finderRecShape(ArrayList<PVector> newShape, boolean lines[][], PVector start) {
 
  // Set up some variables
  boolean shaping = true;
  PVector next = start;
  
  println("Hit shape...");
  newShape.add(next);
  
  while(shaping) {
    
    // Set this location to false so it wont be searched again
    lines[int(next.x)][int(next.y)] = false;
    
    // Get the loop around our point
    ArrayList<PVector> ring = fetchRing(int(next.x), int(next.y));
    for (int i = int(next.z); i < 8; i++) {
      PVector p = ring.get(i);
      
      // If this path was a dud based on last item
      if (int(next.z) == 7)
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
      else if (lines[int(p.x)][int(p.y)])
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
void shapeSetResolution(ArrayList<PVector> newShape)
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
