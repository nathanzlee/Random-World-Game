package byow;

import byow.TileEngine.TETile;

public class Room {
    private int height;
    private int width;
    private int xPos;
    private int yPos;

    public Room(int h, int w, int x, int y) {
        height = h;
        width = w;
        xPos = x;
        yPos = y;
    }

    public void drawRoom(TETile[][] tiles, Round round) {
        for (int x = xPos; x < xPos + width; x++) {
            for (int y = yPos; y < yPos + height; y++) {
                tiles[x][y] = round.getPathTile();
            }
        }
    }

    public Hallway appendHallway(TETile[][] tiles, int len, int dir) {
        if (dir == 0) {
            return appendHallwayTop(tiles, len);
        } else if (dir == 1) {
            return appendHallwayRight(tiles, len);
        } else if (dir == 2) {
            return appendHallwayBot(tiles, len);
        } else {
            return appendHallwayLeft(tiles, len);
        }
    }

    private Hallway appendHallwayTop(TETile[][] tiles, int len) {
        int x = xPos + (width / 2);
        int y = yPos + height;
        Hallway newHallway = new Hallway(len, false, x, y);

        return newHallway;
    }

    private Hallway appendHallwayRight(TETile[][] tiles, int len) {
        int x = xPos + width;
        int y = yPos + (height / 2);
        Hallway newHallway = new Hallway(len, true, x, y);

        return newHallway;
    }

    private Hallway appendHallwayBot(TETile[][] tiles, int len) {
        int x = xPos + (width / 2);
        int y = yPos - len;
        Hallway newHallway = new Hallway(len, false, x, y);

        return newHallway;
    }

    private Hallway appendHallwayLeft(TETile[][] tiles, int len) {
        int x = xPos - len;
        int y = yPos + (height / 2);
        Hallway newHallway = new Hallway(len, true, x, y);

        return newHallway;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }


}
