package byow;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

public class WorldGenerator {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final long SEED = 5524887;
    private static final int MAXROOMSIZE = 8;
    private static final int MINOBJECTS = 40;
    private static final int MAXOBJECTS = 50;
    private static final Random RANDOM = new Random(12345);


    public static void initializeTiles(TETile[][] tiles, int width, int height, Round round) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j] = round.getWallTile();
            }
        }
    }

    public static void deleteUnusedSpace(TETile[][] tiles, int width, int height, Round round) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (tiles[i][j] == round.getPathTile()) {
                    continue;
                } else {
                    int xStart = (i == 0) ? i : i - 1;
                    int yStart = (j == 0) ? j : j - 1;
                    int xEnd = (i == width - 1) ? i : i + 1;
                    int yEnd = (j == height - 1) ? j : j + 1;
                    int count = 0;
                    for (int x = xStart; x <= xEnd; x++) {
                        for (int y = yStart; y <= yEnd; y++) {
                            if (tiles[x][y] == round.getPathTile()) {
                                count += 1;
                            }
                        }
                    }

                    if (count == 0) {
                        tiles[i][j] = Tileset.NOTHING;
                    }
                }
            }
        }
    }

    private static int randomSize(Random random) {
        int num = random.nextInt(MAXROOMSIZE - 2) + 2;
        return num;
    }

    private static int randomXCoord(Random random, int mapWidth) {
        int num = random.nextInt(mapWidth - 3) + 1;
        return num;
    }

    private static int randomYCoord(Random random, int mapHeight) {
        int num = random.nextInt(mapHeight - 3) + 1;
        return num;
    }

    private static int randomObjectLim(Random random) {
        int num = random.nextInt(MAXOBJECTS - MINOBJECTS) + MINOBJECTS;
        return num;
    }

    public static Room generateRandomRoom(TETile[][] tiles, Random random, int mapWidth,
                                          int mapHeight, Round round) {
        Room randomRoom = new Room(randomSize(random), randomSize(random),
                randomXCoord(random, mapWidth), randomYCoord(random, mapHeight));
        if (validateArea(randomRoom, mapWidth, mapHeight)) {
            randomRoom.drawRoom(tiles, round);
            return randomRoom;
        } else {
            return generateRandomRoom(tiles, random, mapWidth, mapHeight, round);
        }
    }

    public static Hallway appendRandomHallway(TETile[][] tiles, Room room, Random random,
                                              int mapWidth, int mapHeight, Round round, int iter) {
        if (iter > 1000) {
            throw new RuntimeException();
        }
        int dir = furthestDir(tiles, room, mapWidth, mapHeight, round);
        Hallway randomHall = room.appendHallway(tiles, randomSize(random), dir);
        if (validateArea(randomHall, mapWidth, mapHeight)) {
            if (dir == 0 || dir == 3) {
                randomHall.addSecondNeighbor();
            } else {
                randomHall.addFirstNeighbor();
            }
            randomHall.drawHallway(tiles, round);
            return randomHall;
        } else {
            return appendRandomHallway(tiles, room, random, mapWidth, mapHeight, round, iter + 1);
        }
    }

    private static int furthestDir(TETile[][] tiles, Room room, int mapWidth,
                                   int mapHeight, Round round) {
        int xStart = room.getXPos();
        int xEnd = room.getXPos() + room.getWidth();
        int yStart = room.getYPos();
        int yEnd = room.getYPos() + room.getHeight();

        int topDist = 0;
        int rightDist = 0;
        int botDist = 0;
        int leftDist = 0;

        int currentTopY = yEnd;
        int currentRightX = xEnd;
        int currentBotY = yStart - 1;
        int currentLeftX = xStart - 1;

        while (tiles[xStart + room.getWidth() / 2][currentTopY] != round.getPathTile()
                && currentTopY < mapHeight - 1) {
            topDist += 1;
            currentTopY += 1;
        }

        while (tiles[currentRightX][yStart + room.getHeight() / 2] != round.getPathTile()
                && currentRightX < mapWidth - 1) {
            rightDist += 1;
            currentRightX += 1;
        }

        while (tiles[xStart + room.getWidth() / 2][currentBotY] != round.getPathTile()
                && currentBotY > 0) {
            botDist += 1;
            currentBotY -= 1;
        }

        while (tiles[currentLeftX][yStart + room.getHeight() / 2] != round.getPathTile()
                && currentLeftX > 0) {
            leftDist += 1;
            currentLeftX -= 1;
        }

        int[] distList = new int[] {topDist, rightDist, botDist, leftDist};

        int max = distList[0];
        int maxIndex = 0;
        for (int i = 1; i < distList.length; i++) {
            if (distList[i] > max) {
                max = distList[i];
                maxIndex = i;
            }

        }
        return maxIndex;

    }

    public static Room appendRandomRoom(TETile[][] tiles, Hallway hall, Random random,
                                        int mapWidth, int mapHeight, Round round, int iter) {
        if (iter > 1000) {
            throw new RuntimeException();
        }
        Room randomRoom = hall.appendRoom(tiles, randomSize(random), randomSize(random));
        if (validateArea(randomRoom, mapWidth, mapHeight)) {
            randomRoom.drawRoom(tiles, round);

            return randomRoom;
        } else {
            return appendRandomRoom(tiles, hall, random, mapWidth, mapHeight, round, iter + 1);
        }
    }

    public static boolean validateArea(Room room, int mapWidth, int mapHeight) {
        int xStart = room.getXPos();
        int yStart = room.getYPos();
        int xEnd = xStart + room.getWidth();
        int yEnd = yStart + room.getHeight();
        if (xStart < 1 || xEnd > mapWidth - 2 || yStart < 1 || yEnd > mapHeight - 5) {
            return false;
        }

        return true;
    }

    public static boolean validateArea(Hallway hall, int mapWidth, int mapHeight) {
        boolean hor = hall.isHorizontal();
        int length = hall.getLength();
        int xStart = hall.getXPos();
        int yStart = hall.getYPos();

        if (hor) {
            if (xStart < 3 || xStart + length > mapWidth - 4
                    || yStart < 3 || yStart > mapHeight - 5) {
                return false;
            }
        } else {
            if (yStart < 3 || yStart + length > mapHeight - 5
                    || xStart < 3 || xStart > mapWidth - 4) {
                return false;
            }
        }

        return true;
    }

    public static Position createAvatar(TETile[][] tiles, Random random, int width, int height,
                                        Round round) {
        int x = randomXCoord(random, width);
        int y = randomYCoord(random, height);
        if (tiles[x][y] == round.getPathTile()) {
            tiles[x][y] = Tileset.AVATAR;
            return new Position(x, y);
        } else {
            return createAvatar(tiles, random, width, height, round);
        }
    }

    public static Position createExit(TETile[][] tiles, Random random, int width, int height,
                                      Round round) {
        int x = randomXCoord(random, width);
        int y = randomYCoord(random, height);
        if (tiles[x][y] == round.getWallTile()) {
            if ((tiles[x - 1][y] == Tileset.NOTHING
                    || tiles[x + 1][y] == Tileset.NOTHING
                    || tiles[x][y - 1] == Tileset.NOTHING
                    || tiles[x][y + 1] == Tileset.NOTHING)
                    && (tiles[x - 1][y] == round.getPathTile()
                    || tiles[x + 1][y] == round.getPathTile()
                    || tiles[x][y - 1] == round.getPathTile()
                    || tiles[x][y + 1] == round.getPathTile())) {
                tiles[x][y] = Tileset.LOCKED_DOOR;
                return new Position(x, y);
            } else {
                return createExit(tiles, random, width, height, round);
            }
        } else {
            return createExit(tiles, random, width, height, round);
        }
    }

    public static void createKey(TETile[][] tiles, Random random, int width, int height,
                                     Round round) {
        int x = randomXCoord(random, width);
        int y = randomYCoord(random, height);
        if (tiles[x][y] == round.getPathTile()) {
            tiles[x][y] = Tileset.KEY;
        } else {
            createKey(tiles, random, width, height, round);
        }
    }

    public static void createChest(TETile[][] tiles, Random random, int width, int height,
                                   Round round) {
        int x = randomXCoord(random, width);
        int y = randomYCoord(random, height);
        if (tiles[x][y] == round.getPathTile()) {
            tiles[x][y] = Tileset.CHEST;
        } else {
            createChest(tiles, random, width, height, round);
        }
    }
    public static void generateRandomWorld(TETile[][] tiles, Random random, int width, int height,
                                           Round round) {
        int objLimit = randomObjectLim(random);
        int objCount = 1;
        Room currentRoom = generateRandomRoom(tiles, random, width, height, round);
        Hallway currentHall = null;
        while (objCount < objLimit) {
            try {
                currentHall = appendRandomHallway(tiles, currentRoom, random, width, height,
                        round, 0);
                currentRoom = appendRandomRoom(tiles, currentHall, random, width, height, round, 0);
                objCount += 2;
            } catch (RuntimeException re) {
                break;
            }

        }
    }

    public static RandomWorld generateWorld(int seed, int width, int height, Round round) {
        Random random = new Random(seed);
        TETile[][] randomTiles = new TETile[width][height];
        initializeTiles(randomTiles, width, height, round);
        generateRandomWorld(randomTiles, random, width, height, round);
        deleteUnusedSpace(randomTiles, width, height, round);
        Position avatarPos = createAvatar(randomTiles, random, width, height, round);
        Position exitPos = createExit(randomTiles, random, width, height, round);
        for (int i = 0; i < round.getKeys(); i++) {
            createKey(randomTiles, random, width, height, round);
        }
        for (int j = 0; j < round.getChests(); j++) {
            createChest(randomTiles, random, width, height, round);
        }
        RandomWorld newWorld = new RandomWorld(seed, randomTiles, round, avatarPos, exitPos);

        return newWorld;
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        Round round = new Round(Tileset.WALL, Tileset.FLOOR, 1, 1, 30);
        TETile[][] randomTiles = new TETile[WIDTH][HEIGHT];
        initializeTiles(randomTiles, WIDTH, HEIGHT, round);
        generateRandomWorld(randomTiles, RANDOM, WIDTH, HEIGHT, round);
        deleteUnusedSpace(randomTiles, WIDTH, HEIGHT, round);
        ter.renderFrame(randomTiles);
    }
}
