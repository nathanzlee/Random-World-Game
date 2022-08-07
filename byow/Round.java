package byow;

import byow.TileEngine.TETile;

public class Round {
    private TETile wall;
    private TETile path;
    private int keys;
    private int chests;
    private int timeLimit;

    public Round(TETile wallTile, TETile pathTile, int keyNum, int chestNum, int time) {
        wall = wallTile;
        path = pathTile;
        keys = keyNum;
        chests = chestNum;
        timeLimit = time;
    }

    public TETile getWallTile() {
        return wall;
    }

    public TETile getPathTile() {
        return path;
    }

    public int getKeys() {
        return keys;
    }

    public int getChests() {
        return chests;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

}
