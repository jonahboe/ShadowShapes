import java.util.ArrayList;

import data.SmartPoint;
import fisica.FBody;
import fisica.FBox;
import fisica.FWorld;
import processing.core.PGraphics;

public class ShadowTracker {

    private ArrayList<FBox> shadows = new ArrayList<>();
    private ArrayList<SmartPoint> horisontal = new ArrayList<>();
    private ArrayList<SmartPoint> vertical = new ArrayList<>();
    private FWorld world;
    private int resolution;
    private int maxDistance;
    private int forceMultiplier;
    private boolean render;

    public ShadowTracker(FWorld world, int resolution, int maxDistance, int forceMultiplier, boolean render) {
        this.world = world;
        this.resolution = resolution;
        this.maxDistance = maxDistance;
        this.forceMultiplier = forceMultiplier;
        this.render = render;
    }

    public void trackShadows(PGraphics window) {
        ArrayList<SmartPoint> newH = new ArrayList<>();
        ArrayList<SmartPoint> newV = new ArrayList<>();

        // Get the new shadow edges on the x-asis.
        for (int y = 0; y < window.height; y += resolution) {
            for (int x = 0; x < window.width-1; x++) {
                if (window.get(x, y) == window.color(255) && window.get(x+1, y) == window.color(0))
                    newH.add(new SmartPoint(x, y, SmartPoint.Side.left));
                else if (window.get(x, y) == window.color(0) && window.get(x+1, y) == window.color(255))
                    newH.add(new SmartPoint(x+1, y, SmartPoint.Side.right));
            }
        }
        for (int x = 0; x < window.width; x += resolution) {
            for (int y = 0; y < window.height-1; y++) {
                if (window.get(x, y) == window.color(255) && window.get(x, y+1) == window.color(0))
                    newV.add(new SmartPoint(x, y, SmartPoint.Side.top));
                else if (window.get(x, y) == window.color(0) && window.get(x, y+1) == window.color(255))
                    newV.add(new SmartPoint(x, y+1, SmartPoint.Side.botton));
            }
        }

        // Find the closest previouse point, and set the velocity.
        for (SmartPoint np : newH) {
            if (render) {
                if (np.side == SmartPoint.Side.left)
                    window.fill(0,255,0);
                else if (np.side == SmartPoint.Side.right)
                    window.fill(0,0,255);
                window.ellipse(np.x, np.y, 2, 2);
            }

            int dist = window.width;
            for (SmartPoint lp : horisontal) {
                int newDist = np.x - lp.x;
                if (np.y == lp.y && np.side == lp.side && newDist < dist) {
                    dist = newDist;
                }
            }
            if (maxDistance > dist) {
                np.velocity = dist;
            }
            else {
                np.velocity = 0;
            }
        }
        for (SmartPoint np : newV) {
            if (render) {
                if (np.side == SmartPoint.Side.top)
                    window.fill(0,255,0);
                else if (np.side == SmartPoint.Side.botton)
                    window.fill(0,0,255);
                window.ellipse(np.x, np.y, 2, 2);
            }

            int dist = window.height;
            for (SmartPoint lp : vertical) {
                int newDist = np.y - lp.y;
                if (np.x == lp.x && np.side == lp.side && newDist < dist) {
                    dist = newDist;
                }
            }
            if (maxDistance > dist) {
                np.velocity = dist;
            }
            else {
                np.velocity = 0;
            }
        }

        // Update the main values
        horisontal = newH;
        vertical = newV;
    }

    public void updateCollisions(PGraphics window) {
        // Remove all old shadows, and add the new ones.
        while (shadows.size() > 0) {
            world.remove(shadows.remove(0));
        }
        for (int x = 0; x < window.width; x += resolution) {
            for (int y = 0; y < window.height; y += resolution) {
                if (window.get(x+1,y+1) == window.color(0)) {
                    FBox box = new FBox(resolution, resolution);
                    box.setPosition(x, y);
                    box.setStatic(true);
                    if (render) {
                        box.setFill(100);
                    }
                    else {
                        box.setNoFill();
                        box.setNoStroke();
                    }
                    shadows.add(box);
                    world.add(box);
                }
            }
        }

        // Adjust the bounce of objects that hit the shadows
        for (SmartPoint p : horisontal) {
            FBody body = world.getBody(p.x, p.y);
            if (body != null) {
                float dx = p.x - body.getBox2dBody().getWorldCenter().x;
                float dy = p.y - body.getBox2dBody().getWorldCenter().y;
                if ((p.side == SmartPoint.Side.left && p.velocity < 0) ||
                    (p.side == SmartPoint.Side.right && p.velocity > 0))
                    body.addForce(p.velocity*body.getMass()*forceMultiplier, 0, dx, dy);
            }
        }
        for (SmartPoint p : vertical) {
            FBody body = world.getBody(p.x, p.y);
            if (body != null) {
                float dx = p.x - body.getBox2dBody().getWorldCenter().x;
                float dy = p.y - body.getBox2dBody().getWorldCenter().y;
                if ((p.side == SmartPoint.Side.top && p.velocity < 0) ||
                    (p.side == SmartPoint.Side.botton && p.velocity > 0))
                    body.addForce(0, p.velocity*body.getMass()*forceMultiplier, dx, dy);
            }
        }
    }
}
