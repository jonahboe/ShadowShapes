/**************************************************************************
 * By: Jonah Boe 
 * 
 * This is a program to test the difference in speed between a trig loop
 * around a central pixel vs. an array "loop" around a central pixel.
 * 
 * Each version was tested with the other comented out in the draw loop.
 * The system used: Mackbook Pro late 2015, 2.5 GHz Intel Core i7.
 * Results:
 *  Trig: ~750 mills
 *  Array: ~450 mills
 **************************************************************************/

void setup() {
  size(1000,500);
}

void draw() {
  //searchTrig();
  searchWrap();
  println(millis());
  exit();
}

void searchTrig() {
  for (int y = 1; y < height - 1; y++) {
    for (int x = 1; x < width - 1; x++) {
      for (float ang = 0; ang < TWO_PI; ang += PI/4) {
        int xPos = int(cos(ang) * 1.7 + x);
        int yPos = int(sin(ang) * 1.7 + y);
        color c = get(xPos,yPos);
      }
    }
  }
}


void searchWrap() {
  for (int y = 1; y < height - 1; y++) {
    for (int x = 1; x < width - 1; x++) {
      ArrayList<PVector> l = wrap(x, y);
      for (int i = 0; i < 8; i++) {
        color c = get(int(l.get(i).x), int(l.get(i).y));
      }
    }
  }
}

ArrayList<PVector> wrap(int x, int y)
{
  ArrayList<PVector> l = new ArrayList<PVector>();
  l.add(new PVector(x-1,y-1));
  l.add(new PVector(x,y-1));
  l.add(new PVector(x+1,y-1));
  l.add(new PVector(x+1,y));
  l.add(new PVector(x+1,y+1));
  l.add(new PVector(x,y+1));
  l.add(new PVector(x-1,y+1));
  l.add(new PVector(x-1,y));
  return l; 
}
