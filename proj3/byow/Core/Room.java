package byow.Core;

import java.util.List;

public class Room {
    public int x; //左下角横坐标
    public int y; //左下角纵坐标
    public int width; //宽度
    public int height; //高度
    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public boolean overlapRoom(Room otherRoom) {
        if ((this.x > otherRoom.x + otherRoom.width && this.y > otherRoom.y + otherRoom.height) ||
                (this.x > otherRoom.x + otherRoom.width && this.y + this.height < otherRoom.y) ||
                (this.x + this.width < otherRoom.x && this.y > otherRoom.y + otherRoom.height) ||
                (this.x + this.width < otherRoom.x && this.y + this.height < otherRoom.y)) {
            return true;
        } else {
            return false;
        }
    }
    public Room nearsetRoomList(List<Room> roomList) {

    }


}
