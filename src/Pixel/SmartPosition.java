package Pixel;

public class SmartPosition extends Position {

    public int start;
    public int current;
    public Position[] neighbors;

    public SmartPosition(int x, int y, Position[] neighbors, int start) {
        this.x = x;
        this.y = y;
        this.neighbors = neighbors;
        this.start = start;
        this.current = start;
    }

    public boolean advance() {
        current = (current + 1) % 8;
        return current != start;
    }

}
