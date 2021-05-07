void finderMain() { while(true) {
  
  video.read();
  video.loadPixels();
  opencv.loadImage(video);
  opencv.findCannyEdges(20,75);
  backDrop = opencv.getSnapshot(); 
  
  if (!showBackDrop) {
    removeCurrent = true;
    while(removeCurrent) {
      delay(1);
    }
  }
  
  for (int y = 1; y < height - 1; y++) {
    for (int x = 1; x < width - 1; x++) {
      if (red(backDrop.get(x, y)) != call && 
          green(backDrop.get(x, y)) != call && 
          blue(backDrop.get(x, y)) > thresh) {
        findShape(x, y);
      }
    } 
  }
    
} }