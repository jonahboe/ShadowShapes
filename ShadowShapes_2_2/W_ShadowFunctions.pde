/****************************************************************************************
 *  updateShadows():
 *    Update the physical structures that are placeholders for the shadows.
 *    No return.
 ****************************************************************************************/
void updateShadows() {
  
  // Remove old shadows from world
  for (FBox s: shadowsPH) {
    world.remove(s);
  }
  
  // Kill the old shadow structures
  for (int i = shadowsPH.size() - 1; i >= 0; i--) {
    shadowsPH.remove(i);
  }
  
  // Create a new shadow structures
  FBox newShadow;
  for (int y = 0; y < width; y += shadowRes) {
    for (int x = 0; x < width; x += shadowRes) {
      if (shadows.get(x, y) == color(0)) {
        newShadow = new FBox(shadowRes, shadowRes);
        // Set "y" a little lower so it will be flush or lower than shadow tops
        newShadow.setPosition(x, y + shadowRes/2);
        newShadow.setStatic(true);
        newShadow.setNoFill();
        newShadow.setNoStroke();
        newShadow.setRestitution(0);
        shadowsPH.add(newShadow);
      }
    }
  }
  
  // Add new shadow structures to the world.
  for (FBox s: shadowsPH) {
    world.add(s);
  }
   
}
