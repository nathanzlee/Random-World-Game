package byow;

import byow.TileEngine.TETile;

public class Hallway {
    private int length;
    private boolean horizontal;
    private int xPos;
    private int yPos;
    private boolean[] neighbors;

    public Hallway(int len, boolean hor, int x, int y) {
        length = len;
        horizontal = hor;
        xPos = x;
        yPos = y;
        neighbors = new boolean[] {false, false};
    }

    public void drawHallway(TETile[][] tiles, Round round) {
        if (horizontal) {
            for (int x = xPos; x < xPos + length; x++) {
                tiles[x][yPos] = round.getPathTile();
            }
        } else {
            for (int y = yPos; y < yPos + length; y++) {
                tiles[xPos][y] = round.getPathTile();
            }
        }
    }

    public Room appendRoom(TETile[][] tiles, int height, int width) {
        int x = xPos;
        int y = yPos;
        if (horizontal) {
            if (neighbors[0]) {
                x += length;
                y -= (height / 2);
            } else {
                x -= width;
                y -= (height / 2);
            }
        } else {
            if (neighbors[0]) {
                x -= (width / 2);
                y -= height;
            } else {
                x -= (width / 2);
                y += length;
            }
        }
        Room newRoom = new Room(height, width, x, y);

        return newRoom;
    }

    public int getLength() {
        return length;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void addFirstNeighbor() {
        neighbors[0] = true;
    }

    public void addSecondNeighbor() {
        neighbors[1] = true;
    }
}
