package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile(' ', Color.black, Color.black, "you",
            "./img/avatar.png");
    public static final TETile WALL = new TETile(' ', new Color(216, 128, 128), Color.darkGray,
            "wall", "./img/wall.png");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", "./img/locked_door.png");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door", "./img/unlocked_door.png");
    public static final TETile SAND = new TETile('▒', new Color(255, 255, 204), Color.black,
            "sand");
    public static final TETile MOUNTAIN = new TETile(' ', Color.gray, Color.black, "mountain",
            "./img/mountain.png");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
    public static final TETile DIRT = new TETile('▒', new Color(89, 60, 31), Color.black, "dirt");
    public static final TETile KEY  = new TETile('⚩', Color.ORANGE, Color.black, "key");
    public static final TETile BOMB = new TETile(' ', Color.black, Color.red, "bomb",
            "./img/bomb.png");
    public static final TETile TORCH = new TETile(' ', Color.black, Color.black, "torch",
            "./img/torch.png");
    public static final TETile AXE = new TETile(' ', Color.black, Color.black, "axe",
            "./img/axe.png");
    public static final TETile CHEST = new TETile(' ', Color.black, Color.black, "chest",
            "./img/chest.png");
    public static final TETile TNT = new TETile(' ', Color.black, Color.black, "tnt",
            "./img/tnt.png");
}


