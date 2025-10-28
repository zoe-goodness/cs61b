package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class RoomGenerator {
    public List<Room> roomList = new ArrayList<>();

    /**
     * 随机在world上创建一个房间，并铺上地板，这一步不连接Room，如果出现重合的Room，则重新创建一个room
     * 注意不需要注意wall，因为wall是在最后的时候手动的加上去的，即floor旁边
     * @param random 即种子
     * @param world 世界
     */
    public void randomCreateRoom(Random random, TETile[][] world) {
        Room newRoom = null;
        while (true) {
            int roomX = randomRoomX(random, world);
            int roomY = randomRoomY(random, world);
            int roomWidth = 0;
            int roomHeight = 0;
            while (roomWidth == 0) {
                roomWidth = randomRoomWidth(random, world);
                if (roomX + 2 >= world.length) {
                    roomX = randomRoomX(random, world);
                }
                if (roomX + roomWidth >= world.length) {
                  roomWidth = 0;
                }

            }
            while (roomHeight == 0) {
                roomHeight = randomRoomHeight(random, world);
                if (roomY + 2 >= world[0].length) {
                    roomY = randomRoomY(random, world);
                }
                if (roomY + roomHeight >= world[0].length) {
                    roomHeight = 0;
                }
            }
            newRoom = new Room(roomX, roomY, roomWidth, roomHeight);
            if (roomList.size() == 0) {
                roomList.add(newRoom);
                for (int i = 0; i < roomWidth; i++) {
                    for (int j = 0; j < roomHeight; j++) {
                        world[roomX + i][roomY + j] = Tileset.FLOOR;
                    }
                }
                break;
            }
            boolean overlap = false;
            for (Room room : roomList) {
               if (room.overlapRoom(newRoom)) {
                   overlap = true;
                   break;
               }
            }
            if (!overlap) {
                roomList.add(newRoom);
                for (int i = 0; i < roomWidth; i++) {
                    for (int j = 0; j < roomHeight; j++) {
                        world[roomX + i][roomY + j] = Tileset.FLOOR;
                    }
                }
                break;
            }
        }
    }

    /**
     * 返回一个新的随机的roomX
     * @param random
     * @param world
     * @return
     */
    public int randomRoomX(Random random, TETile[][] world) {
        return RandomUtils.uniform(random, 1, world.length);
    }

    /**
     * 返回一个新的随机的roomY
     * @param random
     * @param world
     * @return
     */
    public int randomRoomY(Random random, TETile[][] world) {
        return RandomUtils.uniform(random, 1, world[0].length);
    }

    /**
     * 返回一个新的随机的roomWidth
     * @param random
     * @param world
     * @return
     */
    public int randomRoomWidth(Random random, TETile[][] world) {
        return RandomUtils.uniform(random, 2, 5);
    }

    /**
     * 返回一个新的随机的roomHeight
     * @param random
     * @param world
     * @return
     */
    public int randomRoomHeight(Random random, TETile[][] world) {
        return RandomUtils.uniform(random, 2, 5);
    }

    /**
     * 连接两个room     
     * 分为
     * @param roomA
     * @param roomB
     * @param random
     * @param world
     */
    public void connectRoom(Room roomA, Room roomB, Random random, TETile[][] world) {
        Position roomACenter = roomA.getCenter();
        Position roomBCenter = roomB.getCenter();
        int roomAX = roomACenter.getX();
        int roomAY = roomACenter.getY();
        int roomBX = roomBCenter.getX();
        int roomBY = roomBCenter.getY();
        int tempX = roomAX;
        int tempY = roomAY;
        //先铺竖地板
        for (int i = 0; i <= Math.abs(roomBY - tempY); i++) {
            if (roomBY - tempY > 0) {
                world[tempX][tempY + i] = Tileset.FLOOR;
            } else if (roomBY - tempY < 0) {
                world[tempX][tempY - i] = Tileset.FLOOR;
            }
        }

        //再铺横地板
        for (int i = 0; i <= Math.abs(roomBX - tempX); i++) {
            if (roomBX - tempX > 0) {
                world[tempX + i][roomBY] = Tileset.FLOOR;
            } else if (roomBX - tempX < 0) {
                world[tempX - i][roomBY] = Tileset.FLOOR;
            }
        }

    }

}
