package byow.Core;

import java.util.List;

public class Room {
    private int x; //左下角横坐标
    private int y; //左下角纵坐标
    private int width; //宽度
    private int height; //高度
    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     *
     * @param otherRoom 其他room
     * @return true表示重叠了，false表示没重叠
     */
    public boolean overlapRoom(Room otherRoom) {
        if (this.x + this.width < otherRoom.x ||  // this 在 other 左边
                otherRoom.x + otherRoom.width < this.x || // this 在 other 右边
                this.y + this.height < otherRoom.y || // this 在 other 下方
                otherRoom.y + otherRoom.height < this.y) { // this 在 other 上方
            return false; // 不重叠
        } else {
            return true;
        }
    }

    /**
     * 返回已经出现的room的列表中离当前room最近的room，如果roomList中没有room,则返回null
     * @param roomList 已经出现的room的列表
     * @return
     */
    public Room nearestRoomList(List<Room> roomList) {
        if (roomList.size() == 0) {
            return null;
        }
        Room nearestRoom = roomList.get(0);
        int nearestDistance = (nearestRoom.x - this.x) * (nearestRoom.x - this.x) + (nearestRoom.y - this.y) * (nearestRoom.y - this.y);
        for (Room room : roomList) {
            if (room.equals(this)) {
                continue;
            }
            int tempDistance = (room.x - this.x) * (room.x - this.x) + (room.y - this.y) * (room.y - this.y);
            if (tempDistance < nearestDistance) {
                nearestDistance = tempDistance;
                nearestRoom = room;
            }
        }
        return nearestRoom;
    }

    /**
     * 得到room的中心
     * @return
     */
    public Position getCenter() {
        return new Position(this.x + this.width / 2, this.y + this.height / 2);
    }

}
