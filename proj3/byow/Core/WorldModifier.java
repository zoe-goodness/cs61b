package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;



public class WorldModifier {
    /**
     * 最后的时候把floor旁边都布满wall
     * @param world
     */
    public static void fillWithWall(TETile[][] world) {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (world[i][j].equals(Tileset.FLOOR)) {
                    //上-下-左-右，注意边界
                    //上
                    if (j + 1 < world[0].length) {
                        if (world[i][j + 1].equals(Tileset.NOTHING)) {
                            world[i][j + 1] = Tileset.WALL;
                        }
                    }
                    //下
                    if (j - 1 >= 0) {
                        if (world[i][j - 1].equals(Tileset.NOTHING)) {
                            world[i][j - 1] = Tileset.WALL;
                        }
                    }
                    //左
                    if (i - 1 >= 0) {
                        if (world[i - 1][j].equals(Tileset.NOTHING)) {
                            world[i - 1][j] = Tileset.WALL;
                        }
                    }
                    //右
                    if (i + 1 < world.length) {
                        if (world[i + 1][j].equals(Tileset.NOTHING)) {
                            world[i + 1][j] = Tileset.WALL;
                        }
                    }
                }
            }
        }
    }
}
