/****************************************************************************************
 *  mirrorimage():
 *    Take an image and mirror it.
 *    Return the mirrored image.
 ****************************************************************************************/
PImage mirrorimage(PImage initial) {
  
  // Set up our bounds
  int this_x = initial.width;
  int this_y = initial.height;
  
  // Make a new image of the same size
  PImage mirror = new PImage(this_x, this_y);
  
  // mirror the image
  for (int y = 0; y < this_y; y++) {
    for (int x = 0; x < this_x; x++) {
      mirror.set(this_x - 1 - x, y, initial.get(x, y));
    } 
  }
  
  // return the image
  return mirror;
  
}



/****************************************************************************************
 *  randomColor():
 *    Select a random color from these random presets.
 *    Return the color.
 ****************************************************************************************/
color randomColor() {
  
  randomSeed(millis());
  int select = floor(random(0,5));
  color c[] = {color(255,0,0),     //red
               color(255,157,0),   //orange
               color(28,255,0),    //green
               color(57,90,255),   //blue
               color(167,0,219)};  //purple
                
  return c[select];
  
}



/****************************************************************************************
 *  fetchRing():
 *    Get the cordinates of the pixles around a given pixel
 *    Return an array of cordinates.
 ****************************************************************************************/
ArrayList<PVector> fetchRing(int x, int y)
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
