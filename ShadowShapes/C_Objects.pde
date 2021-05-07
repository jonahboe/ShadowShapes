FPoly addShape(ArrayList<PVector> frame) {
  
  // Set up shape.
  FPoly shape;
  shape = new FPoly();
  shape.setNoStroke();
  
  // These will be our colors.
  float r = 0;
  float g = 0;
  float b = 0;
  
  // We want random colors, but not too white.
  r = random(0, 230);
  g = random(0, 230);
  if (r < 50 || g < 50) {
    b = random(100, 230);
  }
    
  shape.setFill(r, g, b);
  shape.setDensity(10);
  shape.setRestitution(0.5);
  shape.setFriction(0.3);
  
  // Add the vertecies.
  for (PVector v: frame) {
    shape.vertex(v.x, v.y);
  }
  
  // Return the shape.
  return shape;
  
}



FBox addShadow(int x, int y) {
  
  int res = shadowRes / 2;
  
  FBox shadow = new FBox(shadowRes, shadowRes);
  shadow.setPosition(x-res, y-res);
  shadow.setStatic(true);
  shadow.setFill(0);
  shadow.setNoStroke();
  shadow.setRestitution(0);
  
  return shadow;
  
}