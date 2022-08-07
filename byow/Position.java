package byow;

import java.io.Serializable;

public class Position implements Serializable {
    private int x;
    private int y;
    private int dir;

    public Position(int xPos, int yPos) {
        x = xPos;
        y = yPos;
        dir = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addX(int n) {
        x += n;
    }

    public void remX(int n) {
        x -= n;
    }

    public void addY(int n) {
        y += n;
    }

    public void remY(int n) {
        y -= n;
    }

    public int getDir() {
        return dir;
    }

    public void dirNorth() {
        dir = 0;
    }

    public void dirEast() {
        dir = 1;
    }

    public void dirSouth() {
        dir = 2;
    }

    public void dirWest() {
        dir = 3;
    }
}
