int thresh = 200;
int call = 199;

void setup() {
  
  size(400, 400);
  background(0);
  
  stroke(255);
  strokeWeight(1);
  fill(0);
  noSmooth();
  
  rect(200, 200, 100, 100);
  
  line(20,20,30,20);
    line(30,20, 40,50);
    line(40,50,15,40);
    line(15,40,20,20);
    line(30,20,49,20);
    
  line(20,200,20,300);
  
  ellipse(300,100,150,150);
  
}


void draw() {}



void keyPressed() {
  
  println("Searching for new shapes...");
  for (int y = 1; y < height - 1; y++) {
    for (int x = 1; x < width - 1; x++) {
      if (red(get(x, y)) != call && green(get(x, y)) != call && blue(get(x, y)) > thresh) {
        println("  Found shape at: " + x + ", " + y);
        println("    Color: " + red(get(x, y)) + ", " + green(get(x, y)) + ", " + blue(get(x, y)) + ", ");
        findShape(x, y);
      }
    } 
  }
  println("Done searching shapes.\n");
  
}
  
  
 
void findShape(int x, int y) {
  
  float ang = 0;
  boolean shapeIng = true;
  
  color RED = color(call, 0, 0);
  color GREEN = color(0, call, 0);
    
  int shapeLoc = 0;
  int shapePos[][] = new int[2][(width/4)*(height/4)];
  int fork = 0;
  int forks[] = new int[(width/4)*(height/4)];

  int xA;
  int yA;
  
  println("    Loading shape...");
  while (shapeIng) {
    
    // Set first pixel to green and each succesive one to red
    if (shapeLoc == 0)
      set(x, y, GREEN);
    else
      set(x, y, RED);
    
    // reset angle to 0 to keep from algebraic error
    ang = 0;
    
    // Sheck neighboring pixels for continuity
    for (int i = 0; i < 8; i++) {
      xA = int(cos(ang) * 1.7);
      yA = int(sin(ang) * 1.7);
      
      ang += PI/4;
      
      // If the next part is an unread piece of the shape
      if (red(get(x + xA, y + yA)) > thresh) {
        
        // Check for any forks
        if (shapeLoc > 5) {
          for (int j = i+1; j < 8; j++) {
            int xF = int(cos(ang) * 1.7);
            int yF = int(sin(ang) * 1.7);
            if (red(get(x + xF, y + yF)) > thresh) {
              forks[fork] = shapeLoc;
              fork++;
              j = 8;
              println("      Found/Bend Fork at: " + x + ", " + y);
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
      if (green(get(x + xA, y + yA)) == call && shapeLoc > 5) {
        println("    Shape successfully compleated");
        makeShape(shapePos, shapeLoc);
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
          println("    No true shape found");
        }
      }
    }
    shapeLoc++;
  }
  
}



void makeShape(int shapePos[][], int shapeLoc) {
  
  print("    Drawing shape: ");
  
  PShape myShape;  // The PShape object
  myShape = createShape();
  myShape.beginShape();
  myShape.fill(255, 0, 180);
  myShape.noStroke();
  
  for(int i = 0; i < shapeLoc; i++) {
    myShape.vertex(shapePos[0][i], shapePos[1][i]);
  }
  
  myShape.endShape(CLOSE);
  shape(myShape, 0, 0);
  
}



void mousePressed() {
  println(red(get(mouseX, mouseY)));
}