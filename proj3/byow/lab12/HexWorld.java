package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 58;
    private static final int HEIGHT = 60;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        addHexagonTesselation(world, new Position(3, 10), 4);
        ter.renderFrame(world);

    }

    /**
     * 左下角为p，往右边画六边形tesselation
     * @param world world数组
     * @param p p为六边形的左下角
     * @param size 六边形大小
     */
    public static void addHexagonTesselation(TETile[][] world, Position p, int size) {
        Position tempPosition = p;
        //left half
        for (int i = 0; i < 3; i++) {
            addHexagonColumn(world, tempPosition, size, 3 + i);
            tempPosition = shiftPosition(tempPosition, 2 * size - 1, -1 * size);
        }
        tempPosition = shiftPosition(tempPosition, 0, 2 * size);
        //right half
        for (int i = 1; i >= 0; i--) {
            addHexagonColumn(world, tempPosition, size, 3 + i);
            tempPosition = shiftPosition(tempPosition, 2 * size - 1, size);
        }
    }

    /**
     * 在位置p上往上面画出一连串的六边形
     * @param world world数组
     * @param p p为六边形的左下角
     * @param size 六边形大小
     * @param num 往上画出num个六边形
     */
    public static void addHexagonColumn(TETile[][] world, Position p, int size, int num) {
        Position tempPosition = p;
        for (int i = 0; i < num; i++) {
            addHexagon(world, tempPosition, size, randomTile());
            tempPosition = shiftPosition(tempPosition, 0, 2 * size);
        }
    }
    /**
     * 在位置p上画出大小为size的六边形
     * @param world world数组
     * @param p p为六边形的左下角
     * @param size 六边形大小
     * @param teTile 瓦片
     */
    public static void addHexagon(TETile[][] world, Position p, int size, TETile teTile) {
        int x = p.getX();
        int y = p.getY();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size + 2 * i; j++) {
                world[x + j - i][y + i] = teTile;
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size + 2 * i; j++) {
                world[x + j - i][y + 2 * size - 1 - i] = teTile;
            }
        }

    }
    /** Picks a RANDOM tile with an equal change of being
     *  a water, flower, grass, sand, mountain, tree.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(6);
        switch (tileNum) {
            case 0: return Tileset.WATER;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.SAND;
            case 4: return Tileset.MOUNTAIN;
            case 5: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }
    private static Position shiftPosition(Position p, int dx, int dy) {
        return new Position(p.x + dx, p.y + dy);
    }

}
