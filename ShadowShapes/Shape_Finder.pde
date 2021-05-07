void findShape(int x, int y) {
  float ang = 0;
  boolean shapeIng = true;
  
  color RED = color(call, 0, 0);
  color GREEN = color(0, call, 0);
    
  int shapeLoc = 0;
  int shapePos[][] = new int[2][(width/2) * (height/2)];
  int fork = 0;
  int forks[] = new int[(width/2)*(height/2)];

  int xA;
  int yA;
  
  int xMax = 0;
  int xMin = 0;
  int yMax = 0;
  int yMin = 0;
  
  while (shapeIng) {
    
    // Set first pixel to green and each succesive one to red
    if (shapeLoc == 0) {
      backDrop.set(x, y, GREEN);
      xMax = x;
      xMin = x;
      yMax = y;
      yMin = y;
    }
    else {
      backDrop.set(x, y, RED);
      if (x < xMin)
        xMin = x;
      else if (x > xMax)
        xMax = x;
      if (y < yMin)
        yMin = y;
      else if (y > yMax)
        yMax = y;
    }
    
    // reset angle to 0 to keep from algebraic error
    ang = 0;
    
    // Check neighboring pixels for continuity
    for (int i = 0; i < 8; i++) {
      xA = int(cos(ang) * 1.7);
      yA = int(sin(ang) * 1.7);
      
      ang += PI/4;
      
      // If the next part is an unread piece of the shape
      if (red(backDrop.get(x + xA, y + yA)) > thresh) {
        
        // Check for any forks
        if (shapeLoc > 5) {
          for (int j = i+1; j < 8; j++) {
            int xF = int(cos(ang) * 1.7);
            int yF = int(sin(ang) * 1.7);
            if (red(backDrop.get(x + xF, y + yF)) > thresh) {
              if (fork + 1 > (width/2)*(height/2)) {
                shapeIng = false;
                i = 8;
                j = 8;
                return;
              }
              forks[fork] = shapeLoc;
              fork++;
              j = 8;
            }
            ang += PI/4;

          }
        }
        // Save point
        shapePos[0][shapeLoc] = x;
        shapePos[1][shapeLoc] = y;
        // Move to next pixel
        i = 8;
        x += xA;
        y += yA;
        
      }
      // If it is an already read peice of the shape
      if (green(backDrop.get(x + xA, y + yA)) == call && shapeLoc > 5) {
        if (shapeLoc > shapeMin) {
          makeShape(shapePos, shapeLoc, (xMin + xMax) / 2, (yMin + yMax) / 2);
        }
        else {
        }
        shapeIng = false;
      }
      //If there is no next peice
      else if (i == 7) {
        // Check for past forks.
        if (fork > 0) {
          fork--;
          // Go back to last valid location
          shapeLoc = forks[fork];
          x = shapePos[0][shapeLoc];
          y = shapePos[1][shapeLoc];
        }
        // Otherwise end the shape
        else {
          i = 8;
          shapeIng = false;
        }
      }
    }
    shapeLoc++;
  }
}
