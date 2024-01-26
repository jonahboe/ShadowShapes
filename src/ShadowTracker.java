import java.util.ArrayList;

import java.lang.Math;

import fisica.FBody;
import fisica.FWorld;
import processing.core.PGraphics;

public class ShadowTracker {

    public ArrayList<SmartPoint> horisontal = new ArrayList<>();
    public ArrayList<SmartPoint> vertical = new ArrayList<>();
    
    private int resolution;
    private int maxDistance;

    public ShadowTracker(int resolution, int maxDistance) {
        this.resolution = resolution;
        this.maxDistance = maxDistance;
    }

    public void trackShadows(PGraphics window, boolean draw) {
        ArrayList<SmartPoint> newH = new ArrayList<>();
        ArrayList<SmartPoint> newV = new ArrayList<>();

        // Get the new shadow edges on the x-asis.
        for (int y = 1; y < window.height; y += resolution) {
            for (int x = 1; x < window.width; x++) {
                if (window.get(x, y) == window.color(0) && window.get(x-1, y) == window.color(255))
                    newH.add(new SmartPoint(x, y, SmartPoint.Side.left));
                else if (window.get(x, y) == window.color(255) && window.get(x-1, y) == window.color(0))
                    newH.add(new SmartPoint(x-1, y, SmartPoint.Side.right));
            }
        }
        // Find the closest previouse point, and set the velocity.
        for (SmartPoint np : newH) {
            if (draw) {
                if (np.side == SmartPoint.Side.left)
                    window.fill(0,255,0);
                else if (np.side == SmartPoint.Side.right)
                    window.fill(0,0,255);
                window.ellipse(np.x, np.y, 2, 2);
            }

            int dist = window.width;
            int dx = 0;
            int dy = 0;
            for (SmartPoint lp : horisontal) {
                int nd = (int) Math.sqrt((np.x - lp.x)^2 + (np.x - lp.x)^2);
                if (nd < dist) {
                    dist = nd;
                    dx = np.x - lp.x;
                    dy = np.y - lp.y;
                }
            }
            if (maxDistance < dist) {
                np.dx = dx * 2;
                np.dy = dy * 2;
            }
            else {
                np.dx = 0;
                np.dy = 0;
            }
        }
        // Update the main values
        horisontal = newH;
    }

    public void updateCollisions(FWorld world) {
        for (SmartPoint p : horisontal) {
            FBody body = world.getBody(p.x, p.y);
            if (body != null) {
                float dx = p.x - body.getBox2dBody().getWorldCenter().x;
                float dy = p.y - body.getBox2dBody().getWorldCenter().y;
                body.setVelocity(p.dx, p.dy);
            }
        }
    }
}
