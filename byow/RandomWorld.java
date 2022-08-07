package byow;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.*;

public class RandomWorld implements Serializable {
    private int seed;
    private TETile[][] tiles;
    private TETile wallTile;
    private TETile pathTile;
    private Position avatarPos;
    private Position exitPos;

    public RandomWorld(int worldSeed, TETile[][] world, Round round, Position avatar,
                       Position exit) {
        seed = worldSeed;
        tiles = world;
        wallTile = round.getWallTile();
        pathTile = round.getPathTile();
        avatarPos = avatar;
        exitPos = exit;
    }

    public TETile[][] getWorld() {
        return tiles;
    }

    public void loadWorld(boolean fog, int fogRadius) {
        TERenderer ter = new TERenderer();
        if (fog) {
            TETile[][] newTiles = createFog(fogRadius);
            ter.renderFrame(newTiles);
        } else {
            ter.renderFrame(tiles);
        }
    }

    private TETile[][] createFog(int radius) {
        TETile[][] foggyWorld = new TETile[tiles.length][tiles[0].length];
        int x = avatarPos.getX();
        int y = avatarPos.getY();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (Math.pow(Math.abs(x - i), 2) + Math.pow(Math.abs(y - j), 2) > radius) {
                    foggyWorld[i][j] = Tileset.NOTHING;
                } else {
                    foggyWorld[i][j] = tiles[i][j];
                }
            }
        }

        return foggyWorld;
    }

    public String moveLeft() {
        avatarPos.dirWest();
        int x = avatarPos.getX();
        int y = avatarPos.getY();
        String tileDes = "";
        if (!tiles[x - 1][y].description().equals(wallTile.description())
                && !tiles[x - 1][y].description().equals("locked door")
                && !tiles[x - 1][y].description().equals("chest")
                && !tiles[x - 1][y].description().equals("nothing")) {
            tileDes = tiles[x - 1][y].description();
            tiles[x - 1][y] = Tileset.AVATAR;
            tiles[x][y] = pathTile;
            avatarPos.remX(1);
        }

        return tileDes;
    }

    public String moveUp() {
        avatarPos.dirNorth();
        int x = avatarPos.getX();
        int y = avatarPos.getY();
        String tileDes = "";
        if (!tiles[x][y + 1].description().equals(wallTile.description())
                && !tiles[x][y + 1].description().equals("locked door")
                && !tiles[x][y + 1].description().equals("chest")
                && !tiles[x][y + 1].description().equals("nothing")) {
            tileDes = tiles[x][y + 1].description();
            tiles[x][y + 1] = Tileset.AVATAR;
            tiles[x][y] = pathTile;
            avatarPos.addY(1);
        }

        return tileDes;
    }

    public String moveRight() {
        avatarPos.dirEast();
        int x = avatarPos.getX();
        int y = avatarPos.getY();
        String tileDes = "";
        if (!tiles[x + 1][y].description().equals(wallTile.description())
                && !tiles[x + 1][y].description().equals("locked door")
                && !tiles[x + 1][y].description().equals("chest")
                && !tiles[x + 1][y].description().equals("nothing")) {
            tileDes = tiles[x + 1][y].description();
            tiles[x + 1][y] = Tileset.AVATAR;
            tiles[x][y] = pathTile;
            avatarPos.addX(1);
        }

        return tileDes;
    }

    public String moveDown() {
        avatarPos.dirSouth();
        int x = avatarPos.getX();
        int y = avatarPos.getY();
        String tileDes = "";
        if (!tiles[x][y - 1].description().equals(wallTile.description())
                && !tiles[x][y - 1].description().equals("locked door")
                && !tiles[x][y - 1].description().equals("chest")
                && !tiles[x][y - 1].description().equals("nothing")) {
            tileDes = tiles[x][y - 1].description();
            tiles[x][y - 1] = Tileset.AVATAR;
            tiles[x][y] = pathTile;
            avatarPos.remY(1);
        }

        return tileDes;
    }

    public void open(int keys, int keysAcquired) {
        int x = avatarPos.getX();
        int y = avatarPos.getY();

        if (Math.abs(x - exitPos.getX()) + Math.abs(y - exitPos.getY()) == 1) {
            if (keys == keysAcquired) {
                tiles[exitPos.getX()][exitPos.getY()] = Tileset.UNLOCKED_DOOR;
            }
        }

        if (tiles[x + 1][y].description().equals("chest")) {
            tiles[x + 1][y] = randomItem();
        } else if (tiles[x][y + 1].description().equals("chest")) {
            tiles[x][y + 1] = randomItem();
        } else if (tiles[x - 1][y].description().equals("chest")) {
            tiles[x - 1][y] = randomItem();
        } else if (tiles[x][y - 1].description().equals("chest")) {
            tiles[x][y - 1] = randomItem();
        }
    }

    public void useItem(TETile item) {
        System.out.println(item.description());
        if (item == null) {
            return;
        }

        if (item.description().equals("axe")) {
            breakWall();
        } else if (item.description().equals("tnt")) {
            explode();
        }
    }

    public void breakWall() {
        int x = avatarPos.getX();
        int y = avatarPos.getY();
        int dir = avatarPos.getDir();

        if (dir == 0) {
            if (tiles[x][y + 1].description().equals(wallTile.description())) {
                tiles[x][y + 1] = pathTile;
            }
        } else if (dir == 1) {
            if (tiles[x + 1][y].description().equals(wallTile.description())) {
                tiles[x + 1][y] = pathTile;
            }
        } else if (dir == 2) {
            if (tiles[x][y - 1].description().equals(wallTile.description())) {
                tiles[x][y - 1] = pathTile;
            }
        } else {
            if (tiles[x - 1][y].description().equals(wallTile.description())) {
                tiles[x - 1][y] = pathTile;
            }
        }
    }

    public void explode() {
        int x = avatarPos.getX();
        int y = avatarPos.getY();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (Math.pow(Math.abs(x - i), 2) + Math.pow(Math.abs(y - j), 2) <= 7) {
                    if (tiles[i][j].description().equals(wallTile.description())) {
                        tiles[i][j] = pathTile;
                    }
                }
            }
        }
    }

    public TETile randomItem() {
        Random random = new Random(seed);
        int num = random.nextInt(10);
        switch (num) {
            case 0:
            case 1:
            case 2:
            case 3:
                return Tileset.TORCH;
            case 4:
            case 5:
            case 6:
                return Tileset.AXE;
            case 7:
            case 8:
                return Tileset.TNT;
            case 9:
                return Tileset.TNT;
            default:
                return Tileset.TORCH;
        }
    }

    public TETile getTile(int x, int y, boolean fog, int fogRadius) {
        if (fog) {
            TETile[][] newTiles = createFog(fogRadius);
            return newTiles[x][y];
        } else {
            return tiles[x][y];
        }
    }

    public int getSeed() {
        return seed;
    }

    public String getWallTile() {
        return wallTile.description();
    }
}
