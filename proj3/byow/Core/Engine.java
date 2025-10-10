package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

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

    public TETile[][] drawWorld(long seed, TETile[][] world) {
        Random random = new Random(seed);
        RoomGenerator roomGenerator = new RoomGenerator();
        int time = RandomUtils.uniform(random, 20, 25);
        for (int i = 0; i < time; i++) {
            roomGenerator.randomCreateRoom(random, world);
            roomGenerator.connectRoom(roomGenerator.roomList.get(i), roomGenerator.roomList.get(i).nearestRoomList(roomGenerator.roomList), random, world);
        }
        WorldModifier.fillWithWall(world);
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
    public TETile[][] initializeTiles(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        return world;
    }

}
