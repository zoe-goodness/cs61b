package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomGenerator {
    public List<Room> roomList = new ArrayList<>();

    /**
     * 随机在world上创建一个房间，并铺上地板，并把他与最近的room用hallway连接起来
     * 注意不需要注意wall，因为wall是在最后的时候手动的加上去的，即floor旁边
     * @param random
     * @param worldWidth
     * @param worldHeight
     * @param world
     */
    public void randomCreateRoom(Random random, int worldWidth, int worldHeight, TETile[][] world) {
        int roomX = RandomUtils.uniform(random, 0, worldWidth);
        int roomY = RandomUtils.uniform(random, 0, worldHeight);
        int roomWidth = 0;
        int roomHeight = 0;
        while (roomWidth == 0) {
            roomWidth = RandomUtils.poisson(random, 2);
        }
        while (roomHeight == 0) {
            roomHeight = RandomUtils.poisson(random, 2);
        }
        Room newRoom = new Room(roomX, roomY, roomWidth, roomHeight);
        for (Room room : roomList) {
            if (room.overlapRoom(newRoom)) {
                return;
            }
        }
        roomList.add(newRoom);
        for (int i = roomX; i < roomWidth; i++) {
            for (int j = roomY; j < roomHeight; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }
        //还需要补充与最近的room用hallway连接起来，可以用伯努利一半一半的概率连接
    }
}
