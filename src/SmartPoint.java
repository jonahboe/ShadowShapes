public class SmartPoint extends Point {

    public enum Side{left,right,top,botton}

    public int dx;
    public int dy;
    public Side side;

    SmartPoint(int x, int y, Side side) {
        super(x, y);
        this.side = side;
    }
}
