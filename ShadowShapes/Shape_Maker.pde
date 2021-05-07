void makeShape(int shapePos[][], int shapeLoc, int xCent, int yCent) {
  
  for (int j = 0; j < total; j++) {
        
    if (freeSpace[j]) {
      
      PSLock[0][j] = xCent;
      PSLock[1][j] = yCent;
   
      PShape newShape = createShape();
      newShape.beginShape();
      newShape.fill(random(50, 255), random(50, 255), random(50, 255));
      newShape.noStroke();
  
      newShape.vertex(shapePos[0][0] - xCent, shapePos[1][0] - yCent);
      for(int i = 1; i < shapeLoc; i++) {        
        if (shapePos[0][i] != shapePos[0][i-1] && shapePos[1][i] != shapePos[1][i-1]) {
          newShape.vertex(shapePos[0][i] - xCent, shapePos[1][i] - yCent);
        }
      }
  
      newShape.endShape(CLOSE);
      
      shapeStack[j] = newShape;
            
      PSVert[j] = random(2.0, 8.0);
            
      PSHora[j] = random(-10.0, 10.0);
            
      freeSpace[j] = false;
            
      j = total;
            
    }
  }
    
}