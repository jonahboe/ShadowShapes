package Pixel;

public class SmartPixel {

    public int x;
    public int y;
    public int start;
    public int current;

    public SmartPixel(int x, int y, int start) {
        this.x = x;
        this.y = y;
        this.start = start;
        this.current = start;
    }

    public boolean advance() {
        current = (current + 1) % 8;
        return current != start;
    }

    public boolean isSameAs(SmartPixel pixel) {
        return this.x == pixel.x && this.y == pixel.y;
    }

}
