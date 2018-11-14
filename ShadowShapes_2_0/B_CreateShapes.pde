/****************************************************************************************
 *  Lets include some more libraries for the physics shapes.
 ****************************************************************************************/
// Include fisica library
import fisica.*;

// We need a reference to our world, shadows, and shapes.
FWorld world;

// This will hold the array of shadows.
ArrayList<FBox> shadows;



/****************************************************************************************
 *  box2dSetup():
 *    Create the shapes in the array.
 *    No return.
 ****************************************************************************************/
void worldSetup() {
  
  // Initialize initalize physics and create the world.
  Fisica.init(this);
  world = new FWorld();
  
  // Set up the world.
  world.setGravity(0, 800);
  world.setEdges();
  world.remove(world.left);
  world.remove(world.right);
  world.remove(world.top);
  world.setEdgesRestitution(0.5);

  // Creat array lists for the shadows.
  shadows = new ArrayList<FBox>();
  
}
 


/****************************************************************************************
 *  makeShapes():
 *    Create the shapes in the array.
 *    No return.
 ****************************************************************************************/
void makeShapes(int x, int y) {
  
  int xStart = 0;
  int yStart = 0;
  
  float ang = 0;
  boolean shapeIng = true;
  
  // What array are we at in the shape.
  int shapeLoc = 0;
  // Set of saved positions.
  PVector shapePos[] = new PVector[(width/2) * (height/2)];
  // What fork are we at.
  int fork = 0;
  // Array of indexes where forks occured in "shapePos".
  int forks[] = new int[(width/2)*(height/2)];
  
  // Minimum and maximum values found. will help locate center
  PVector Max = new PVector(0, 0);
  PVector Min = new PVector(0, 0);

  while (shapeIng) {
    
    // Set first pixel to read.
    if (shapeLoc == 0) {
      xStart = x;
      yStart = y;
      
      // start the center point.
      Max.x = x;
      Max.y = y;
      Max.x = x;
      Min.y = y;
    }
    
    // Move the center point
    else {
      if (x < Min.x)
        Min.x = x;
      else if (x > Max.x)
        Max.x = x;
      if (y < Min.y)
        Min.y = y;
      else if (y > Max.y)
        Max.y = y;
    }
    
    // Remove frome list of items to check
    lines[x][y] = false;
    
    // Reset angle to 0 to keep from algebraic error
    ang = 0;
    
    // Check neighboring pixels for continuity
    for (int i = 0; i < 8; i++) {
      
      // variable for spining around one pixel.
      int xA;
      int yA;
      
      xA = int(cos(ang) * 1.7);
      yA = int(sin(ang) * 1.7);
      
      ang += PI/4;
      // If the next part is an unread piece of the shape
      if (lines[x + xA][y + yA]) {
        // Check for any forks if past the first part.
        if (shapeLoc > 5) {
          for (int j = i+1; j < 8; j++) {
            int xF = int(cos(ang) * 1.7);
            int yF = int(sin(ang) * 1.7);
            if (lines[x + xF][y + yF]) {
              // If the sahpe is getting out of hand.
              if (fork + 1 > (width/2)*(height/2)) {
                // Just kill it.
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
        shapePos[shapeLoc].x = x;
        shapePos[shapeLoc].y = y;
        // Move to next pixel
        i = 8;
        x += xA;
        y += yA;  
      }
      // If it is the starting point of the shape.
      if (x + xA == xStart && y + yA == yStart) {
        // If the shape is over a certain size.
        if (shapeLoc > shapeMinSize) {
          // Center point
          float xCenter = (Min.x + Max.x)/2;
          float yCenter = (Min.y + Max.y)/2;
          // Create new shape framework.
          ArrayList<PVector> frame = new ArrayList<PVector>();
          for(int tic = 0; tic < shapeLoc/shapeRes; tic++) {
            PVector v = new PVector(shapePos[tic*shapeRes].x,shapePos[tic*shapeRes].y);
            frame.add(v);
          }
          
          // If there is not a shape here already, then create one.
          if (world.getBody(xCenter, yCenter) == null) {
            world.add(addShape(frame));
          }
          
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
          x = int(shapePos[shapeLoc].x);
          y = int(shapePos[shapeLoc].y);
        }
        // Otherwise end the shape
        else {
          i = 8;
          shapeIng = false;
        }
      }
    }
    // Move to the next part of the shape.
    shapeLoc++;
  }
 
}



/****************************************************************************************
 *  updateShadows():
 *    Causes the shadows to update as physical objects.
 *    No return.
 ****************************************************************************************/
void updateShadows() {
  
  // Kill the old shadows.
  for (FBox s: shadows) {
    world.remove(s);
  }
  // Kill the old shadow structure
  for (int i = shadows.size()-1; i >= 0; i--) {
    shadows.remove(i);
  }
  
  // Create a new shadow structure, with intervals of 10.
  for (int y = 0; y < width; y += shadowRes) {
    for (int x = 0; x < width; x += shadowRes) {
      if (cam.get(x, y) == color(0)) {
        shadows.add(addShadow(x, y));
      }
    }
  }
  
  // Add new shadow structures to the world.
  for (FBox s: shadows) {
    world.add(s);
  }
  
}