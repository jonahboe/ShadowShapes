import fisica.FBox;
import fisica.FWorld;
import processing.core.PImage;
import java.util.ArrayList;

public class ShadowHandler implements Runnable {

    private FWorld world;
    private PImage shadows = new PImage();
    private ArrayList<FBox> shadowsSet = new ArrayList<>();
    private int shadowRes = 25;

    ShadowHandler(FWorld w) {
        world = w;
    }

    public void updateShadows(PImage image) {
        // Update the shadow world
        shadows = image;
        new Thread(this).start();
    }

    public void run() {
        // Set some dimensions
        int width = shadows.width;
        int height = shadows.height;

        // Remove old shadows from world
        for (FBox b: shadowsSet) {
            world.remove(b);
        }

        // Kill the old shadow structures
        for (int i = shadowsSet.size() - 1; i >= 0; i--) {
            shadowsSet.remove(i);
        }

        // Create new shadow structures
        FBox newShadow;
        for (int y = 0; y < height; y += shadowRes) {
            for (int x = 0; x < width; x += shadowRes) {
                if (shadows.get(x, y) == 0) {
                    System.out.println("hit");
                    newShadow = new FBox(shadowRes, shadowRes);
                    // Set "y" a little lower so it will be flush or lower than shadow tops
                    newShadow.setPosition(x, y + shadowRes/2);
                    newShadow.setStatic(true);
                    newShadow.setNoFill();
                    newShadow.setNoStroke();
                    newShadow.setRestitution(0);
                    shadowsSet.add(newShadow);
                }
            }
        }

        // Add new shadow structures to the world.
        for (FBox b: shadowsSet) {
            world.add(b);
        }
    }
}
