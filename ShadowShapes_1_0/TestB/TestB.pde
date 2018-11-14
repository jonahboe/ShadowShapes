int total = 20;
PShape square[] = new PShape[total];
float PSLock[][] = new float[2][total];
float PSVert[] = new float[total];
float PSHora[] = new float[total];
boolean freeSpace[] = new boolean[total];

void setup() {
  
 size(800,400); 
 
 for (int i = 0; i < total; i++) {
   freeSpace[i] = true;
 }
  
}



void draw() {
  
  background(0);
  
  for (int i = 0; i < total; i++) {
    if (!freeSpace[i]) {
      shape(square[i], PSLock[0][i], PSLock[1][i]);
    
      PSLock[0][i] += PSHora[i];
    
      square[i].rotate(PSHora[i]/6);
    
      PSVert[i] -= 0.1;
      PSLock[1][i] -= PSVert[i];
      
      if (PSLock[1][i] > height + 40) {
        freeSpace[i] = true;
      }
    }
  }
  
}



void mousePressed() {
  
  fill(255);
  
  for (int i = 0; i < total; i++) {
    if (freeSpace[i]) {
      square[i] = createShape(RECT, -15, -15, 30, 30);
      PSLock[0][i] = mouseX;
      PSLock[1][i] = mouseY;
  
      PSVert[i] = random(2.0, 8.0);
      PSHora[i] = random(-3.0, 3.0);
      
      freeSpace[i] = false;
      
      i = total;
    }
  }
  
}