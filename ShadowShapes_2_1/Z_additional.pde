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
