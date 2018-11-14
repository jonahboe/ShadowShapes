public class ShapeFinder {
  
  // Variables for tracking occurances in thread
  boolean searching;
  // Variables for monitoring shape creation
  boolean newShape;
  ArrayList<PVector> frame;
  
  // Defult constructor
  ShapeFinder() {
    searching = false;
    newShape = false;
    frame = null;
  }
  
  // Functions for tracking occurances in thread
  boolean getIsSearching() {
    return searching;
  }
  void startSearch(PImage s) {
    searching = true;
    shadowsSF = s;
    thread("findShapes");
  }
  void endSearch() {
    searching = false;
  }
  
  // Functions for monitoring shape creation
  boolean getIsNewShape() {
    return newShape;
  }
  void setIsNewShape(boolean n) {
    newShape = n;
  }
  
  // Functions for creating shapes
  void setShapeFrame(ArrayList<PVector> f){
    frame = f;
  }
  
  // Take the frame and make a shape
  void pushFrameToShape() {
    // Set up shape.
    FPoly shape;
    shape = new FPoly();
    shape.setNoStroke();
    color c = randomColor();
    shape.setFill(red(c),green(c),blue(c));
    shape.setDensity(10);
    shape.setRestitution(0.5);
    shape.setFriction(0.3);
    
    // make the shape
    float xMin = frame.get(0).x;
    float xMax = xMin;
    float yMin = frame.get(0).y;
    float yMax = yMin;
    for (PVector v: frame) {
      shape.vertex(v.x, v.y);
      if (v.x < xMin)
        xMin = v.x;
      if (v.x > xMax)
        xMax = v.x;
      if (v.y < yMin)
        yMin = v.y;
      if (v.y > yMax)
        yMax = v.y;
    }
    
    // if a shape isn't here already
    if (world.getBody((xMin+xMax)/2, (yMin+yMax)/2) == null)
    {
      // Add the shape to the world family
      world.add(shape); 
    }
  }
  
}

void findShapes() {
  
  ArrayList<PVector> newShape;
  newShape = finderLineSearch();
  
  // If there is a new shape 
  if (newShape != null)
  {
    // load the new shape
    shapeFinder.setShapeFrame(newShape);
    // Let main know
    shapeFinder.setIsNewShape(true);
    
    // Get out quick
    shapeFinder.endSearch();
    return;
  }
  
  // Some test squares
  if (mousePressed) {
    newShape = new ArrayList<PVector>();
    newShape.add(new PVector(mouseX-25,mouseY-25));
    newShape.add(new PVector(mouseX+25,mouseY-25));
    newShape.add(new PVector(mouseX+25,mouseY+25));
    newShape.add(new PVector(mouseX-25,mouseY+25));
    
    // load the new shape
    shapeFinder.setShapeFrame(newShape);
    // Let main know
    shapeFinder.setIsNewShape(true);
    
    // Get out quick
    shapeFinder.endSearch();
    return;
  }
  
  // Let main know that searching has finished.
  shapeFinder.endSearch();
  
}
