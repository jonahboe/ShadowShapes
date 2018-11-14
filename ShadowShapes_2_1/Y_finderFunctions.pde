ArrayList<PVector> finderLineSearch() {
  
  ArrayList<PVector> newShape;
  newShape = new ArrayList<PVector>();
  
  // lines will show up true.
  boolean lines[][] = new boolean[width][height];
  
  // We'll go through each pixel and check if it is part of a line. We put it in an array.
  // This means that the pixle will be different from at least one other near it.
  int count = 0;
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
            count++;
          }
          // Otherwise it isn't a line and we need to continue.
          else {
            lines[x][y] = false;
          }
        }
      }
    }
  }
  println(count);
  
  // Now we want to use the lines we have found to look for shapes.
  // This will be done by following the lines in a recursive search.
  for (int y = 0; y < height; y++) {
    for (int x = 0; x < width; x++) {
      // If you hit a line.
      if (lines[x][y]) {
        // we'll track position by x, y, and current recursion cycle.
        PVector pos = new PVector(x,y,0);
        // Trace and return the shape if there is one.
        if (finderRecShape(newShape, lines, pos, pos))
          return newShape;
      }
    }
  }
  
  // Else return nothing.
  newShape = null;
  return newShape;
  
}



boolean finderRecShape(ArrayList<PVector> newShape, boolean lines[][], PVector pos, PVector start) {
  
  // We'll mark this pixel as checked
  lines[int(pos.x)][int(pos.y)] = false;
  
  // We'll check the 9 pixels around it one at a time.
  ArrayList<PVector> ring = fetchRing(int(pos.x), int(pos.y));
  for (int i = 0; i < 8; i++) {
    int xPos = int(ring.get(i).x);
    int yPos = int(ring.get(i).y);
    // If we hit a line...
    if (lines[xPos][yPos]) {
      // If we hit our start point...
      if (xPos == int(start.x) && yPos == int(start.y))
      {
        // If we are within the first two pixels just ignore it.
        if (pos.z < 3);
        // Otherwize if we are less then the desired size return false.
        else if (pos.z < shapeSizeMin)
          return false;
        // Otherwise we have compleated a shape!!
        else
          return true;
      }
      
      // Else, we will start a new cycle.
      PVector p = new PVector(xPos, yPos, pos.x+1);
      // If it returns true
      if (finderRecShape(newShape, lines, p, start)){
        // Add our position to the shape (if it is within the resolution)
        if (int(pos.z) % shapeRes == 0) 
          newShape.add(pos);
        // Tell the last cycle we are good.
        return true;
      }
      // Otherwise we'll keep searching
    }
  }
  
  // If all else fails. then there was nothing
  return false;
}
