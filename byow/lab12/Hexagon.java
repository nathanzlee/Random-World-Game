package byow.lab12;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Hexagon {
    private int side;
    private int xPos;
    private int yPos;
    private TETile tile;


    public Hexagon(int sideLength, int posX, int posY, TETile tileType) {
        side = sideLength;
        xPos = posX;
        yPos = posY;
        tile = tileType;
    }

    public void drawHexagon(TETile[][] tiles) {
        int xStart = xPos;
        int newRow = side;

        for (int y = yPos; y < yPos + (side * 2); y++) {
            /** Draw new row */
            for (int x = xStart; x < xStart + newRow; x++) {
                tiles[x][y] = tile;
            }
            /** Update posX and row length */
            if (y - yPos + 1 < side) {
                xStart -= 1;
                newRow += 2;
            } else if (y - yPos + 1 > side) {
                xStart += 1;
                newRow -= 2;
            }
        }
    }

    public void addLeftBottom(TETile[][] tiles, TETile tile) {
        int xStart = xPos - ((2 * side) - 1);
        int yStart = yPos - side;
        Hexagon otherHex = new Hexagon(side, xStart, yStart, tile);
        otherHex.drawHexagon(tiles);
    }

    public void addBottom(TETile[][] tiles, TETile tile) {
        int xStart = xPos;
        int yStart = yPos - (side * 2);
        Hexagon otherHex = new Hexagon(side, xStart, yStart, tile);
        otherHex.drawHexagon(tiles);
    }

    public void addRightBottom(TETile[][] tiles, TETile tile) {
        int xStart = xPos + ((2 * side) - 1);
        int yStart = yPos - side;
        Hexagon otherHex = new Hexagon(side, xStart, yStart, tile);
        otherHex.drawHexagon(tiles);
    }

    public void addRightTop(TETile[][] tiles, TETile tile) {
        int xStart = xPos + ((2 * side) - 1);
        int yStart = yPos + side;
        Hexagon otherHex = new Hexagon(side, xStart, yStart, tile);
        otherHex.drawHexagon(tiles);
    }

    public void addTop(TETile[][] tiles, TETile tile) {
        int xStart = xPos;
        int yStart = yPos + (2 * side);
        Hexagon otherHex = new Hexagon(side, xStart, yStart, tile);
        otherHex.drawHexagon(tiles);
    }

    public void addLeftTop(TETile[][] tiles, TETile tile) {
        int xStart = xPos - ((2 * side) - 1);
        int yStart = yPos + side;
        Hexagon otherHex = new Hexagon(side, xStart, yStart, tile);
        otherHex.drawHexagon(tiles);
    }

    public void drawTesselatingHex(TETile[][] tiles, TETile leftBot,
                                     TETile bot, TETile rightBot, TETile rightTop, TETile top,
                                     TETile leftTop) {
        drawHexagon(tiles);
        addLeftBottom(tiles, leftBot);
        addBottom(tiles, bot);
        addRightBottom(tiles, rightBot);
        addRightTop(tiles, rightTop);
        addTop(tiles, top);
        addLeftTop(tiles, leftTop);
    }
}
