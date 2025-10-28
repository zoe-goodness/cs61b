package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        world = initializeTiles(world);
        showMenu();
        InputSource inputSource = new KeyboardInputSource();
        char c = inputSource.getNextKey();
        long seed;
        if (c == 'N' || c == 'n') {
            seed = showProvideExtractSeed();
            world = drawWorld(seed, world);
            ter.initialize(WIDTH, HEIGHT);
            showWorld(world);
        } else if (c == 'L' || c == 'l') {
            //load game
            world = loadWorld();

        } else if (c == 'Q' || c == 'q'){
            //quit game
            return;
        }

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
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        world = initializeTiles(world);
        long seed = 0;
        input = input.toLowerCase();
        if (input.charAt(0) == 'n') {
            //构造世界
            seed = extractSeed(input);
            world = drawWorld(seed, world);
        } else if (input.charAt(0) == 'l') {
            //load上次世界
            world = loadWorld();
        }
        return world;
    }
    
    private void createOneAvatar(TETile[][] world, Random random) {
        while (true) {
            int randomX = RandomUtils.uniform(random, WIDTH);
            int randomY = RandomUtils.uniform(random, HEIGHT);
        }
    }
    /**
     * 用于interactWithKeyboard提示输入种子的页面
     * @return 种子
     */
    private long showProvideExtractSeed() {
        int menuWidth = 40;
        int menuHeight = 40;
        ter.initialize(40, 40);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(menuWidth / 2, menuHeight / 2, "Provide seed");
        StdDraw.show();
        InputSource inputSource = new KeyboardInputSource();
        String seed = "N";
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (c == 'S' || c == 's') {
                seed += c;
                break;
            }
            else {
                seed += c;
            }
        }
        return extractSeed(seed);
    }

    /**
     * 用来展示world
     * @param world world数组
     */
    private void showWorld(TETile[][] world) {
        ter.renderFrame(world);
    }
    
    //用于interactWithKeyboard展示页面
    private void showMenu() {
        int menuWidth = 40;
        int menuHeight = 40;
        ter.initialize(40, 40);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(menuWidth / 2, menuHeight / 5 * 4, "CS61B: THE GAME");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
        StdDraw.text(menuWidth / 2, menuHeight / 10 * 6, "New Game (N)");
        StdDraw.text(menuWidth / 2, menuHeight / 2, "Load Game (L)");
        StdDraw.text(menuWidth / 2, menuHeight / 10 * 4, "Quit (Q)");
        StdDraw.show();
    }
    public TETile[][] drawWorld(long seed, TETile[][] world) {
        Random random = new Random(seed);
        RoomGenerator roomGenerator = new RoomGenerator();
        int time = RandomUtils.uniform(random, 20, 25);
        for (int i = 0; i < time; i++) {
            roomGenerator.randomCreateRoom(random, world);

            roomGenerator.connectRoom(roomGenerator.roomList.get(i), roomGenerator.roomList.get(i).nearestRoomList(roomGenerator.roomList), random, world);

        }
        WorldModifier.fillWithWall(world);
        createOneAvatar(world, random);
        return world;
    }
    public TETile[][] loadWorld() {
        return null;
    }

    /**
     * 从N###SSSSSSS中提取种子###
     * @param input :类似N###SSSSSSS
     * @return
     */
    public long extractSeed(String input) {
        input = input.toLowerCase();
        if (Long.parseLong(input.substring(1, input.indexOf("s"))) < Long.MAX_VALUE) {
            return Long.parseLong(input.substring(1, input.indexOf("s")));
        } else {
            throw new RuntimeException("exceed the maximum of seed");
        }
    }

    /**
     * 初始化世界
     * @param world
     * @return
     */
    public TETile[][] initializeTiles(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        return world;
    }

}
