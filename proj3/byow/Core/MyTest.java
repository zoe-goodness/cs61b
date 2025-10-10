package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class MyTest {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;
    @Test
    public void testExtractSeed() {
        Engine engine = new Engine();
        System.out.println(engine.extractSeed("n929896041742075871s"));
    }
    @Test
    public void testRandomCreateRoom() {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        Random random = new Random(131);
        RoomGenerator roomGenerator = new RoomGenerator();
        int time = RandomUtils.uniform(random, 20, 25);
        for (int i = 0; i < time; i++) {
            roomGenerator.randomCreateRoom(random, world);
        }
        ter.renderFrame(world);
    }
    @Test
    public void testConnectRoom() {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        Random random = new Random(131);
        RoomGenerator roomGenerator = new RoomGenerator();
        for (int i = 0; i < 2; i++) {
            roomGenerator.randomCreateRoom(random, world);
        }
        roomGenerator.connectRoom(roomGenerator.roomList.get(0), roomGenerator.roomList.get(1), random, world);
        WorldModifier.fillWithWall(world);
        ter.renderFrame(world);
    }
    @Test
    public void testSameSeedInteractWithInputStringWithoutConnect() {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        Random random = new Random(131);
        RoomGenerator roomGenerator = new RoomGenerator();
        int time = RandomUtils.uniform(random, 20, 25);
        for (int i = 0; i < time; i++) {
            roomGenerator.randomCreateRoom(random, world);
        }
        WorldModifier.fillWithWall(world);
        ter.renderFrame(world);
    }
    @Test
    public void testSameSeedInteractWithInputStringWithConnect() {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        Random random = new Random(131);
        RoomGenerator roomGenerator = new RoomGenerator();
        int time = RandomUtils.uniform(random, 20, 25);
        for (int i = 0; i < time; i++) {
            roomGenerator.randomCreateRoom(random, world);
            roomGenerator.connectRoom(roomGenerator.roomList.get(i), roomGenerator.roomList.get(i).nearestRoomList(roomGenerator.roomList), random, world);
        }

        WorldModifier.fillWithWall(world);
        ter.renderFrame(world);
    }


    public static void main(String[] args) {
        MyTest myTest = new MyTest();
        myTest.interactWithInputString("n929896041742075871s");

    }
    @Test
    public void tempRandomUtilsEqualSeed() {
        Random random = new Random(111);
        System.out.println(RandomUtils.poisson(random, 4));
        System.out.println(RandomUtils.poisson(random, 4));
        System.out.println(RandomUtils.poisson(random, 4));

    }
    private void interactWithInputString(String seed) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        Engine engine = new Engine();
        Random random = new Random(engine.extractSeed(seed));
        RoomGenerator roomGenerator = new RoomGenerator();
        int time = RandomUtils.uniform(random, 20, 25);
        for (int i = 0; i < time; i++) {
            roomGenerator.randomCreateRoom(random, world);
            roomGenerator.connectRoom(roomGenerator.roomList.get(i), roomGenerator.roomList.get(i).nearestRoomList(roomGenerator.roomList), random, world);
        }

        WorldModifier.fillWithWall(world);
        ter.renderFrame(world);
    }



}
