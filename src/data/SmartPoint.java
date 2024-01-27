package data;
public class SmartPoint extends Point {

    public enum Side{left,right,top,botton}
    
    public int velocity;
    public Side side;

    public SmartPoint(int x, int y, Side side) {
        super(x, y);
        this.side = side;
    }
}
