public class Location {
    private int x;
    private int y;
    private String id;
    private double kerteriz;

    public Location(int x,int y,String id,double kerteriz) {
        setX(x);
        setY(y);
        setId(id);
        setKerteriz(kerteriz);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getKerteriz() {
        return kerteriz;
    }

    public void setKerteriz(double kerteriz) {
        this.kerteriz = kerteriz;
    }
}