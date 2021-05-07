import fisica.FBox;
import fisica.FWorld;
import processing.core.PImage;
import java.util.ArrayList;

/**
 * Class for handling the creation and destruction of shadows.
 */
public class ShadowHandler implements Runnable {
    private final ShadowShapes applet;
    private final FWorld world;
    private PImage image;
    private final ArrayList<FBox> shadowsSet;
    private final int shadowResolution;

    /**
     * Constructor.
     *
     * @param applet The applet controlling this handler.
     * @param world The physics world being drawn to.
     * @param shadowResolution How wide/tall the shadow blocks should be.
     */
    ShadowHandler(ShadowShapes applet, FWorld world, int shadowResolution) {
        this.applet = applet;
        this.world = world;
        this.shadowResolution = shadowResolution;

        shadowsSet = new ArrayList<>();
    }

    /**
     * This is where our thread will start.
     */
    @Override
    public final void run() {
        while(true) {
            try {
                Thread.sleep(200);
                setImage(applet.getImage());
                updateShadows();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            finally {
                // Lets add our new shapes
                addShadows();
            }
        }
    }

    /**
     * Set the image currently being searched.
     *
     * @param image The new image.
     */
    public synchronized void setImage(PImage image) {
        this.image = image;
    }

    /**
     * Update the shadow block locations.
     */
    private void updateShadows() {
        // Set some dimensions
        int width = image.width;
        int height = image.height;

        // Remove the old shadows from the world and list
        removeShadows();
        shadowsSet.clear();

        // Create new shadow structures
        FBox newShadow;
        for (int y = 0; y < height; y += shadowResolution) {
            for (int x = 0; x < width; x += shadowResolution) {
                if (image.get(x, y) == ImageHandler.BLACK) {
                    newShadow = new FBox(shadowResolution, shadowResolution);
                    newShadow.setPosition(x, y);
                    newShadow.setStatic(true);
                    // TODO change back to noFill
                    //newShadow.setNoFill();
                    newShadow.setFill(255,0,0);
                    newShadow.setNoStroke();
                    newShadow.setRestitution(0);
                    shadowsSet.add(newShadow);
                }
            }
        }
    }

    /**
     * Remove the old shadows from world
     */
    private synchronized void removeShadows() {
        for (FBox b: shadowsSet) {
            world.remove(b);
        }
    }

    /**
     * Add new shadow structures to the world.
     */
    private synchronized void addShadows() {
        for (FBox b: shadowsSet) {
            world.add(b);
        }
    }

}
