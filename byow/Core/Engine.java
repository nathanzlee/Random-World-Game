package byow.Core;

import byow.RandomWorld;
import byow.Round;
import byow.TileEngine.Tileset;
import byow.WorldGenerator;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.text.SimpleDateFormat;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final Round[] ROUNDLIST = {
        new Round(Tileset.WALL, Tileset.FLOOR, 1, 1, 60),
        new Round(Tileset.TREE, Tileset.DIRT, 2, 1, 50),
        new Round(Tileset.MOUNTAIN, Tileset.WATER, 3, 2, 40)
    };
    private boolean gameOver = false;
    private int round = 1;
    private RandomWorld currentWorld;
    private boolean fogOfWar = true;
    private int fogRadius = 10;
    private int keysAcquired = 0;
    private int timeLimit;
    private int roundsCompleted = 0;
    private HashMap<TETile, Boolean> items;
    private TETile currentItem;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        initializeCanvas();
        startScreen();
        startOptions();
    }

    private void initializeCanvas() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    private void startScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font titleFont = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(titleFont);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "CS61B: THE GAME");
        Font smallFont = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(smallFont);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 10, "Leaderboard (B)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 15, "Quit (Q)");
        StdDraw.show();
    }

    private void startOptions() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String next = Character.toString(StdDraw.nextKeyTyped());
                if (next.equals("n") || next.equals("N")) {
                    keysAcquired = 0;
                    round = 1;
                    fogRadius = 10;
                    timeLimit = ROUNDLIST[0].getTimeLimit();
                    items = new HashMap<>();
                    currentItem = null;
                    newWorldScreen("");
                    enterSeed();
                    break;
                } else if (next.equals("l") || next.equals("L")) {
                    RandomWorld savedWorld = loadSavedWorld();
                    currentWorld = savedWorld;
                    keysAcquired = loadKeys();
                    round = loadRound();
                    fogOfWar = loadFog();
                    fogRadius = loadFogRadius();
                    timeLimit = loadTimeLeft();
                    roundsCompleted = loadRoundsCompleted();
                    items = loadItems();
                    currentItem = loadCurrentItem();

                    if (savedWorld == null) {
                        System.exit(0);
                    } else {
                        startGame();
                    }
                    break;
                } else if (next.equals("b") || next.equals("B")) {
                    showLeaderboard();
                } else if (next.equals("q") || next.equals("Q")) {
                    gameOver = true;
                    System.exit(0);
                }
            }
        }
    }

    private void newWorldScreen(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font titleFont = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(titleFont);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "Enter A Seed");
        Font smallFont = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(smallFont);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    private void enterSeed() {
        String seedStr = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String next = Character.toString(StdDraw.nextKeyTyped());
                if (next.equals("s") || next.equals("S")) {
                    break;
                } else {
                    seedStr += next;
                    newWorldScreen(seedStr);
                }
            }
        }

        int seed = (int) Long.parseLong(seedStr);
        RandomWorld newWorld = WorldGenerator.generateWorld(seed, WIDTH, HEIGHT, ROUNDLIST[0]);
        currentWorld = newWorld;
        startGame();
    }

    private void startGame() {
        Font defFont = new Font("Sans Serif", Font.PLAIN, 16);
        StdDraw.setFont(defFont);
        currentWorld.loadWorld(fogOfWar, fogRadius);
        gameOver = false;
        updateHud();
        updateTimer();
        startGameInteractions();
    }

    private void startGameInteractions() {
        String typed = "";
        String tile = "nothing";
        while (!gameOver) {
            if (StdDraw.hasNextKeyTyped()) {
                String next = Character.toString(StdDraw.nextKeyTyped());
                switch (next) {
                    case "a":
                    case "A":
                        String tileLeft = currentWorld.moveLeft();
                        handleMove(tileLeft);
                        break;
                    case "w":
                    case "W":
                        String tileUp = currentWorld.moveUp();
                        handleMove(tileUp);
                        break;
                    case "d":
                    case "D":
                        String tileRight = currentWorld.moveRight();
                        handleMove(tileRight);
                        break;
                    case "s":
                    case "S":
                        String tileDown = currentWorld.moveDown();
                        handleMove(tileDown);
                        break;
                    case ":":
                        typed += ":";
                        break;
                    case "q":
                    case "Q":
                        if (typed.equals(":")) {
                            quitGame();
                        }
                        break;
                    case "f":
                    case "F":
                        if (fogOfWar) {
                            fogOfWar = false;
                        } else {
                            fogOfWar = true;
                        }
                        break;
                    case "o":
                    case "O":
                        currentWorld.open(ROUNDLIST[round - 1].getKeys(), keysAcquired);
                        break;
                    case "u":
                    case "U":
                        currentWorld.useItem(currentItem);
                        break;
                    case " ":
                        currentItem = nextItem(currentItem);
                        break;
                    default:
                        typed = "";
                }
            }
        }
    }

    private TETile nextItem(TETile item) {
        if (item.description().equals("axe")) {
            return (items.containsKey(Tileset.TNT)) ? Tileset.TNT : Tileset.AXE;
        } else {
            return (items.containsKey(Tileset.AXE)) ? Tileset.AXE : Tileset.TNT;
        }
    }

    private void drawHud() {
        StdDraw.clear(Color.BLACK);
        currentWorld.loadWorld(fogOfWar, fogRadius);
        StdDraw.setPenColor(Color.white);
        // Tile
        StdDraw.text(WIDTH * 0.1, HEIGHT - 1, "Tile: " + tileHover());
        StdDraw.text(WIDTH * 0.3, HEIGHT - 1,
                "Keys: " + keysAcquired + "/" + ROUNDLIST[round - 1].getKeys());

        // Time and date
        String timeStamp =
                new SimpleDateFormat("MM/dd hh:mm").format(Calendar.getInstance().getTime());
        StdDraw.text(WIDTH * 0.9, HEIGHT - 1, timeStamp);

        // Time left
        StdDraw.text(WIDTH / 2, HEIGHT - 1, Integer.toString(timeLimit));

        // Current item
        StdDraw.text(WIDTH * 0.7, HEIGHT - 1, "Item:");
        if (currentItem != null) {
            StdDraw.picture((WIDTH * 0.7) + 3, HEIGHT - 1, currentItem.filepath());
        }

        // Items unlocked
        for (TETile item : items.keySet()) {
            if (item.description().equals("torch") && items.get(item)) {
                StdDraw.picture((WIDTH / 2) - 2, HEIGHT - 2, "./img/torch.png");
            }
            if (item.description().equals("axe") && items.get(item)) {
                StdDraw.picture(WIDTH / 2, HEIGHT - 2, "./img/axe.png");
            }
            if (item.description().equals("tnt") && items.get(item)) {
                StdDraw.picture((WIDTH / 2) + 2, HEIGHT - 2, "./img/tnt.png");
            }

        }

        StdDraw.show();
    }

    private void updateTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!gameOver) {
                    if (timeLimit > 0) {
                        timeLimit--;
                    } else {
                        gameOver = true;
                        fail();
                    }
                } else {
                    timer.cancel();
                    timer.purge();
                }


            }
        }, 0, 1000);
    }

    private void updateHud() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!gameOver) {
                    drawHud();
                } else {
                    timer.cancel();
                    timer.purge();
                }


            }
        }, 0, 50);
    }

    private String tileHover() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            TETile tile = currentWorld.getTile(x, y, fogOfWar, fogRadius);
            return tile.description();
        } else {
            return "";
        }

    }

    private void quitGame() {
        gameOver = true;
        saveGame();
        System.exit(0);
    }

    private void saveGame() {
        File worldFile = new File("world.txt");
        File keysFile = new File("keys.txt");
        File roundFile = new File("round.txt");
        File fogFile = new File("fog.txt");
        File fogRadiusFile = new File("fogradius.txt");
        File timeFile = new File("time.txt");
        File roundsCompletedFile = new File("roundscompleted.txt");
        File itemsFile = new File("items.txt");
        File currentItemFile = new File("currentitem.txt");
        try {
            if (!worldFile.exists()) {
                worldFile.createNewFile();
            }
            if (!keysFile.exists()) {
                keysFile.createNewFile();
            }
            if (!roundFile.exists()) {
                roundFile.createNewFile();
            }
            if (!fogFile.exists()) {
                fogFile.createNewFile();
            }
            if (!fogRadiusFile.exists()) {
                fogRadiusFile.createNewFile();
            }
            if (!timeFile.exists()) {
                timeFile.createNewFile();
            }
            if (!roundsCompletedFile.exists()) {
                roundsCompletedFile.createNewFile();
            }
            if (!itemsFile.exists()) {
                itemsFile.createNewFile();
            }
            if (!currentItemFile.exists()) {
                currentItemFile.createNewFile();
            }
            FileOutputStream f1 = new FileOutputStream(worldFile);
            FileOutputStream f2 = new FileOutputStream(keysFile);
            FileOutputStream f3 = new FileOutputStream(roundFile);
            FileOutputStream f4 = new FileOutputStream(fogFile);
            FileOutputStream f5 = new FileOutputStream(timeFile);
            FileOutputStream f6 = new FileOutputStream(roundsCompletedFile);
            FileOutputStream f7 = new FileOutputStream(fogRadiusFile);
            FileOutputStream f8 = new FileOutputStream(itemsFile);
            FileOutputStream f9 = new FileOutputStream(currentItemFile);
            ObjectOutputStream o1 = new ObjectOutputStream(f1);
            ObjectOutputStream o2 = new ObjectOutputStream(f2);
            ObjectOutputStream o3 = new ObjectOutputStream(f3);
            ObjectOutputStream o4 = new ObjectOutputStream(f4);
            ObjectOutputStream o5 = new ObjectOutputStream(f5);
            ObjectOutputStream o6 = new ObjectOutputStream(f6);
            ObjectOutputStream o7 = new ObjectOutputStream(f7);
            ObjectOutputStream o8 = new ObjectOutputStream(f8);
            ObjectOutputStream o9 = new ObjectOutputStream(f9);
            o1.writeObject(currentWorld);
            o2.writeObject(keysAcquired);
            o3.writeObject(round);
            o4.writeObject(fogOfWar);
            o5.writeObject(timeLimit);
            o6.writeObject(roundsCompleted);
            o7.writeObject(fogRadius);
            o8.writeObject(items);
            o9.writeObject(currentItem);
        } catch (IOException ex) {
            //Do nothing
        }
    }

    private RandomWorld loadSavedWorld() {
        File file = new File("world.txt");
        if (file.exists()) {
            try {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);
                return (RandomWorld) o.readObject();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return null;
            } catch (IOException ex) {
                return null;
            } catch (ClassNotFoundException ex) {
                return null;
            }
        } else {
            System.out.println("It's not here bruh");
            return null;
        }
    }

    private int loadKeys() {
        File file = new File("keys.txt");
        if (file.exists()) {
            try {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);
                return (int) o.readObject();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return 0;
            } catch (IOException ex) {
                return 0;
            } catch (ClassNotFoundException ex) {
                return 0;
            }
        } else {
            System.out.println("It's not here bruh");
            return 0;
        }
    }

    private int loadRound() {
        File file = new File("round.txt");
        if (file.exists()) {
            try {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);
                return (int) o.readObject();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return 1;
            } catch (IOException ex) {
                return 1;
            } catch (ClassNotFoundException ex) {
                return 1;
            }
        } else {
            System.out.println("It's not here bruh");
            return 1;
        }
    }

    private boolean loadFog() {
        File file = new File("fog.txt");
        if (file.exists()) {
            try {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);
                return (boolean) o.readObject();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return true;
            } catch (IOException ex) {
                return true;
            } catch (ClassNotFoundException ex) {
                return true;
            }
        } else {
            System.out.println("It's not here bruh");
            return true;
        }
    }

    private int loadFogRadius() {
        File file = new File("fogradius.txt");
        if (file.exists()) {
            try {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);
                return (int) o.readObject();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return 10;
            } catch (IOException ex) {
                return 10;
            } catch (ClassNotFoundException ex) {
                return 10;
            }
        } else {
            System.out.println("It's not here bruh");
            return 10;
        }
    }

    private int loadTimeLeft() {
        File file = new File("time.txt");
        if (file.exists()) {
            try {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);
                return (int) o.readObject();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return 90;
            } catch (IOException ex) {
                return 90;
            } catch (ClassNotFoundException ex) {
                return 90;
            }
        } else {
            System.out.println("It's not here bruh");
            return 90;
        }
    }

    private int loadRoundsCompleted() {
        File file = new File("timepassed.txt");
        if (file.exists()) {
            try {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);
                return (int) o.readObject();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return 0;
            } catch (IOException ex) {
                return 0;
            } catch (ClassNotFoundException ex) {
                return 0;
            }
        } else {
            System.out.println("It's not here bruh");
            return 0;
        }
    }

    private HashMap<String, Integer> loadLeaderboard() {
        File file = new File("leaderboard.txt");
        if (file.exists()) {
            try {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);
                return (HashMap<String, Integer>) o.readObject();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return new HashMap<>();
            } catch (IOException ex) {
                return new HashMap<>();
            } catch (ClassNotFoundException ex) {
                return new HashMap<>();
            }
        } else {
            System.out.println("It's not here bruh");
            return new HashMap<>();
        }
    }

    private HashMap<TETile, Boolean> loadItems() {
        File file = new File("items.txt");
        if (file.exists()) {
            try {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);
                return (HashMap<TETile, Boolean>) o.readObject();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return new HashMap<>();
            } catch (IOException ex) {
                return new HashMap<>();
            } catch (ClassNotFoundException ex) {
                return new HashMap<>();
            }
        } else {
            System.out.println("It's not here bruh");
            return new HashMap<>();
        }
    }

    private TETile loadCurrentItem() {
        File file = new File("currentitem.txt");
        if (file.exists()) {
            try {
                FileInputStream f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);
                return (TETile) o.readObject();
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
                return null;
            } catch (IOException ex) {
                return null;
            } catch (ClassNotFoundException ex) {
                return null;
            }
        } else {
            System.out.println("It's not here bruh");
            return null;
        }
    }

    private void handleMove(String tile) {
        switch (tile) {
            case "key":
                keysAcquired += 1;
                break;
            case "unlocked door":
                if (round < ROUNDLIST.length) {
                    round += 1;
                    roundsCompleted += 1;
                    timeLimit = ROUNDLIST[round - 1].getTimeLimit();
                    keysAcquired = 0;
                    nextWorld(currentWorld.getSeed() + 455483);
                } else {
                    roundsCompleted += 1;
                    gameOver = true;
                    victory();
                }
                break;
            case "torch":
                fogRadius = 30;
                items.put(Tileset.TORCH, true);
                break;
            case "axe":
                items.put(Tileset.AXE, true);
                currentItem = Tileset.AXE;
                break;
            case "tnt":
                items.put(Tileset.TNT, true);
                currentItem = Tileset.TNT;
                break;
            default:
                // Do nothing
        }
    }

    private void nextWorld(int seed) {
        RandomWorld newWorld = WorldGenerator.generateWorld(seed, WIDTH, HEIGHT,
                ROUNDLIST[round - 1]);
        newWorld.loadWorld(fogOfWar, fogRadius);
        currentWorld = newWorld;
    }

    private void victory() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                StdDraw.clear(Color.BLACK);
                currentWorld.loadWorld(fogOfWar, fogRadius);
                StdDraw.setPenColor(Color.white);
                StdDraw.text(WIDTH / 2, HEIGHT - 1, "You win!");
                StdDraw.show();
            }
        }, 50);

        Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                addToLeaderboard(roundsCompleted);
            }
        }, 3000);


    }

    private void fail() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                StdDraw.clear(Color.BLACK);
                currentWorld.loadWorld(fogOfWar, fogRadius);
                StdDraw.setPenColor(Color.white);
                StdDraw.text(WIDTH / 2, HEIGHT - 1, "You lose!");
                StdDraw.show();
            }
        }, 50);

        Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                startScreen();
                startOptions();

            }
        }, 3000);
    }

    private void addToLeaderboard(int time) {
        leaderboardName("");

        String name = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String next = Character.toString(StdDraw.nextKeyTyped());
                System.out.println(next);
                if (next.equals("\n")) {
                    HashMap<String, Integer> leaderboard = loadLeaderboard();
                    leaderboard.put(name, time);
                    saveLeaderboard(leaderboard);
                    showLeaderboard();
                    break;
                } else {
                    name += next;
                    leaderboardName(name);
                }
            }
        }


    }

    private void leaderboardName(String name) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font titleFont = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(titleFont);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "Enter A Name");
        Font smallFont = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(smallFont);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, name);
        StdDraw.show();
    }

    private void saveLeaderboard(HashMap<String, Integer> leaderboard) {
        File leaderboardFile = new File("leaderboard.txt");

        try {
            if (!leaderboardFile.exists()) {
                leaderboardFile.createNewFile();
            }

            FileOutputStream f1 = new FileOutputStream(leaderboardFile);
            ObjectOutputStream o1 = new ObjectOutputStream(f1);
            o1.writeObject(leaderboard);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showLeaderboard() {
        HashMap<String, Integer> leaderboard = loadLeaderboard();
        HashMap<String, Integer> sortedLeaderboard = sortByValue(leaderboard);
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.WHITE);
        Font smallFont = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(smallFont);
        StdDraw.text(WIDTH / 2, HEIGHT - 5, "Leaderboard");
        Font tinyFont = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(tinyFont);
        StdDraw.text(WIDTH / 2, 3, "Exit (Q)");
        Font smallerFont = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(smallerFont);
        int count = 0;
        for (String name : sortedLeaderboard.keySet()) {
            if (count < 10) {
                int time = sortedLeaderboard.get(name);
                StdDraw.text(WIDTH / 2, HEIGHT - (3 * (count + 4)), name + ": " + time);
                count++;
            } else {
                break;
            }
        }

        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String next = Character.toString(StdDraw.nextKeyTyped());
                if (next.equals("q") || next.equals("Q")) {
                    startScreen();
                    startOptions();
                    break;
                }
            }
        }
    }

    public HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }

        return temp;
    }



    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        char[] userActions = input.toCharArray();
        boolean newWorld = false;
        boolean quit = false;
        boolean worldCreated = false;
        String seed = "";
        for (char letter : userActions) {
            String action = Character.toString(letter);
            switch (action) {
                case "n":
                case "N":
                    newWorld = true;
                    break;
                case "l":
                case "L":
                    currentWorld = loadSavedWorld();
                    if (currentWorld != null) {
                        worldCreated = true;
                    }
                    break;
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                case "8":
                case "9":
                    if (newWorld) {
                        seed += action;
                    }
                    break;
                case "s":
                case "S":
                    if (worldCreated) {
                        currentWorld.moveRight();
                    } else {
                        int worldSeed = (int) Long.parseLong(seed);
                        currentWorld = WorldGenerator.generateWorld(worldSeed, WIDTH, HEIGHT,
                                ROUNDLIST[0]);
                        worldCreated = true;
                    }
                    break;
                case "w":
                case "W":
                    if (worldCreated) {
                        currentWorld.moveUp();
                    }
                    break;
                case "d":
                case "D":
                    if (worldCreated) {
                        currentWorld.moveRight();
                    }
                    break;
                case "a":
                case "A":
                    if (worldCreated) {
                        currentWorld.moveLeft();
                    }
                    break;
                case ":":
                    quit = true;
                    break;
                case "q":
                case "Q":
                    if (quit) {
                        saveGame();
                    }
                    break;
                default:
            }
        }
        if (currentWorld == null) {
            return null;
        }
        return currentWorld.getWorld();
    }
}
