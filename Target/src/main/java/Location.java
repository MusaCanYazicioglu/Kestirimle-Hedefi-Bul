public class Location {
    private int x;
    private int y;
    private long id;

    public Location(int x,int y,long id) {
        setX(x);
        setY(y);
        setId(id);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}