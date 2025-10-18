# Classes and Data Structures

## Main

This is the main entry point for the program. This class simply parses the command line inputs, and lets the byow.Core.Engine class take over in either keyboard or input string mode.It also validates the arguments based on the command to ensure that enough arguments were passed in.

### Fields

No Fields

## Engine

This is where the main logic of our program will live. This file will handle all of the commands with interactWithKeyboard and interactWithInputString.To support interactWithInputString, I create drawWord, initalizeTiles, extractSpeed for it. 

### Fields

1.TERenderer ter = new TERenderer(); 用来展示world

2.public static final int WIDTH = 80; 世界的width。Feel free to change the width

3.public static final int HEIGHT = 30;世界的height。Feel free to change the height.

## RandomUtils

A library of static methods to generate pseudo-random numbers from different distributions (bernoulli, uniform, gaussian, discrete, and exponential). Also includes methods for shuffling an array and other randomness related stuff you might want to do.

### Fields

No Fields

## Position

The class represents a Position.

#### Fields

1.private int x : the x value of this position

2.private int y: the y value of this position

## Room

The class represents a Room. It also includes some methods to make help for room and provide some methods for roomGenerator.

### Fields

1.public int x; //world 左下角横坐标
2.public int y; //world 左下角纵坐标
3.public int width; //Room 宽度
4.public int height; //Room 高度

## RoomGenerator

the class is a util class for like roomGenerator and connectRoom

### Fields

public List<Room> roomList = new ArrayList<>() : the list of the world's all room 

## WorldModifier

用来最后辅助修改世界，比如最后的时候把floor旁边都布满wall

### Fields

No Fields

# Algorithms

## Main

public static void main(String[] args)：根据args参数给不同的类来处理

如果args.length > 2，那么输出"Can only have two arguments - the flag and input string"并结束程序

如果args.length == 2 && args[0].equals("-s")，那么调用engine的这个方法engine.interactWithInputString(args[1]);

如果args.length == 2 && args[0].equals("-p")，那么调用这个方法还没写(TODO://)

其他情况则调用engine的engine.interactWithKeyboard()这个方法

## Engine

1.public void interactWithKeyboard()：Method used for exploring a fresh world. This method should handle all inputs, including inputs from the main menu.

2.public TETile[][] interactWithInputString(String input)：input是类似"n123sswwdasdassadwas", "n123sss:q", "lwww"这样的字符串。它behave exactly as if the user typed these characters into the engine using interactWithKeyboard.Recall that strings ending in ":q" should cause the game to quite save. If we then do interactWithInputString("l"), we should be back in the exact same state.它返回的是没有生成的world

3.public TETile[][] drawWorld(long seed, TETile[][] world)：是上述方法的构造世界的子方法，它根据种子首先告诉你会创建多少个房间（RandomUtils.uniform），然后创建房间（RoomGenerator的randomCreateRoom)，每创建一个房间，就连接新生成的房间与已经生成的最近的房间(RoomGenerator的connectRoom)，最后在floor旁生成墙壁(WorldModifier的fillWithWall方法)

4.public long extractSeed(String input)：从N###SSSSSSS中提取种子###

5.public TETile[][] initializeTiles(TETile[][] world)：初始化世界

## RandomUtils

A library of static methods to generate pseudo-random numbers from different distributions (bernoulli, uniform, gaussian, discrete, and exponential). Also includes methods for shuffling an array and other randomness related stuff you might want to do.

## Position

no

## Room

1.public boolean overlapRoom(Room otherRoom): true表示重叠了，false表示没重叠, 判断两个房间重叠了没

2.public Room nearestRoomList(List<Room> roomList)：返回已经出现的room的列表中离当前room最近的room，如果roomList中没有room,则返回null

3.public Position getCenter():得到room的中心，横坐标为（this.x + this.x + this.width) / 2 ，纵坐标为 (this.y + this.y + this.height) / 2

## RoomGenerator

1.public void randomCreateRoom(Random random, TETile[][] world)：随机（种子）在world上创建一个房间（房间的width和height都是根据种子来生成的），并铺上地板，这一步不连接Room，如果出现重合的Room，则重新创建一个room，注意不需要注意wall，因为wall是在最后的时候手动的加上去的，即floor旁边

2.public int randomRoomX(Random random, TETile[][] world)：根据种子返回一个新的随机的roomX

3.public int randomRoomY(Random random, TETile[][] world)：根据种子返回一个新的随机的roomY

4.public int randomRoomWidth(Random random, TETile[][] world)：根据种子返回一个新的随机的roomWidth

5.public int randomRoomHeight(Random random, TETile[][] world)：根据种子返回一个新的随机的roomHeight

6.public void connectRoom(Room roomA, Room roomB, Random random, TETile[][] world)：连接roomA和roomB，从roomA铺地板铺到rooB,注意它是先铺竖地板，再铺横地板

## WorldModifier

1.

public static void fillWithWall(TETile[][] world):最后的时候把floor旁边都布满wall(including up down left right)
