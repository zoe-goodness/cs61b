package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final File CWD = new File(System.getProperty("user.dir"));
    private String avatarName = "default";


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        world = initializeTiles(world);
        showMenu();
        InputSource inputSource = new KeyboardInputSource();
        long seed;
        boolean running = true;
        while (running && inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (c == 'N' || c == 'n') {
                seed = showProvideExtractSeed();
                world = drawWorld(seed, world);
                ter.initialize(WIDTH + 20, HEIGHT + 20);
                showWorld(world);
                actionForInteract(world);
                running = false;
                ter.initialize(40, 40);
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
                StdDraw.text(40 / 2.0, 40 / 2.0, "Game exited. Goodbye!");
                StdDraw.show();
                StdDraw.pause(1000);
                return;
            } else if (c == 'L' || c == 'l') {
                //load game
                world = loadWorldFromFile();
                ter.initialize(WIDTH + 20, HEIGHT + 20);
                showWorld(world);
                actionForInteract(world);
                running = false;
                ter.initialize(40, 40);
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
                StdDraw.text(40 / 2.0, 40 / 2.0, "Game exited. Goodbye!");
                StdDraw.show();
                StdDraw.pause(1000);
                return;
            } else if (c == 'Q' || c == 'q') {
                running = false;
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
                StdDraw.text(40 / 2.0, 40 / 2.0, "Game exited. Goodbye!");
                StdDraw.show();
                StdDraw.pause(1000);
                return;

            } else if (c == 'X' || c == 'x') {
                avatarName = "";
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
                StdDraw.text(40 / 2.0, 40 / 2.0, "Please input your name");
                StdDraw.show();
                InputSource tempInputSource = new KeyboardInputSource();
                while (tempInputSource.possibleNextInput()) {
                    char tempC = tempInputSource.getNextKey();
                    if (tempC == KeyEvent.VK_ENTER) {
                        break;
                    } else if (tempC == 'm') {
                        continue;
                    }
                    avatarName += tempC;
                }
                showMenuNotAvatarName();
            }
        }


    }



    private void enterNewWorld() {
        ter.initialize(20, 20);
        TETile[][] newWorld = new TETile[20][20];
        for (int x = 0; x < 20; x += 1) {
            for (int y = 0; y < 20; y += 1) {
                newWorld[x][y] = Tileset.NOTHING;
            }
        }
        for (int i = 4; i < 8; i++) {
            for (int j = 4; j < 8; j++) {
                newWorld[i][j] = Tileset.FLOOR;
            }
        }
        WorldModifier.fillWithWall(newWorld);
        newWorld[6][6] = Tileset.AVATAR;
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            while (true) {
                int coinX = RandomUtils.uniform(random, 20);
                int coinY = RandomUtils.uniform(random, 20);
                if (newWorld[coinX][coinY].equals(Tileset.FLOOR)) {
                    newWorld[coinX][coinY] = Tileset.FLOWER;
                    break;
                }
            }
        }
        ter.renderFrame(newWorld);
        actionForNewWorld(newWorld);

    }
    private boolean exitNewWorld(TETile[][] world) {
        int num = 0;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (world[i][j].equals(Tileset.FLOWER)) {
                    num += 1;
                }
            }
        }
        return num == 0;
    }
    private void actionForNewWorld(TETile[][] world) {
        int avatarX = 0;
        int avatarY = 0;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (world[i][j].equals(Tileset.AVATAR)) {
                    avatarX = i;
                    avatarY = j;
                }
            }
        }
        InputSource inputSource = new KeyboardInputSource();
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (c == 'W' || c == 'w') {
                if (!world[avatarX][avatarY + 1].equals(Tileset.WALL)) {
                    avatarY = avatarY + 1;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                    world[avatarX][avatarY - 1] = Tileset.FLOOR;
                    if (exitNewWorld(world)) {
                        break;
                    }
                }
            } else if (c == 'a' || c == 'A') {
                if (!world[avatarX - 1][avatarY].equals(Tileset.WALL)) {
                    avatarX = avatarX - 1;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                    world[avatarX + 1][avatarY] = Tileset.FLOOR;
                    if (exitNewWorld(world)) {
                        break;
                    }
                }
            } else if (c == 's' || c == 'S') {
                if (!world[avatarX][avatarY - 1].equals(Tileset.WALL)) {
                    avatarY = avatarY - 1;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                    world[avatarX][avatarY + 1] = Tileset.FLOOR;
                    if (exitNewWorld(world)) {
                        break;
                    }
                }
            } else if (c == 'D' || c == 'd') {
                if (!world[avatarX + 1][avatarY].equals(Tileset.WALL)) {
                    avatarX = avatarX + 1;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                    world[avatarX - 1][avatarY] = Tileset.FLOOR;
                    if (exitNewWorld(world)) {
                        break;
                    }
                }
            }
            showWorld(world);
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
            if (input.indexOf(":q") == -1) {
                //没有找到:q
                String actionSeries = input.substring(input.indexOf("s") + 1);
                actionForInput(world, actionSeries);

            } else {
                //找到了:q，所以要save
                String actionSeries = input.substring(input.indexOf("s") + 1, input.indexOf(":q"));
                actionForInput(world, actionSeries);
                saveWorldToFile(world);
            }

        } else if (input.charAt(0) == 'l') {
            //load上次世界
            world = loadWorldFromFile();
            if (input.indexOf(":q") == -1) {
                //没有找到:q
                String actionSeries = input.substring(input.indexOf("l") + 1);
                actionForInput(world, actionSeries);
            } else {
                //找到了:q，所以要save
                String actionSeries = input.substring(input.indexOf("l") + 1, input.indexOf(":q"));
                actionForInput(world, actionSeries);
                saveWorldToFile(world);
            }
        }
        return world;
    }

    private void createOneAvatar(TETile[][] world, Random random) {
        while (true) {
            int randomX = RandomUtils.uniform(random, WIDTH);
            int randomY = RandomUtils.uniform(random, HEIGHT);
            if (world[randomX][randomY].equals(Tileset.FLOOR)) {
                world[randomX][randomY] = Tileset.AVATAR;
                break;
            }
        }
    }
    private void actionForInput(TETile[][] world, String actionSeries) {
        int avatarX = 0;
        int avatarY = 0;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (world[i][j].equals(Tileset.AVATAR)) {
                    avatarX = i;
                    avatarY = j;
                }
            }
        }
        for (int i = 0; i < actionSeries.length(); i++) {
            char c = actionSeries.charAt(i);
            if (c == 'W' || c == 'w') {
                if (!world[avatarX][avatarY + 1].equals(Tileset.WALL)) {
                    avatarY = avatarY + 1;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                    world[avatarX][avatarY - 1] = Tileset.FLOOR;

                }
            } else if (c == 'a' || c == 'A') {
                if (!world[avatarX - 1][avatarY].equals(Tileset.WALL)) {
                    avatarX = avatarX - 1;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                    world[avatarX + 1][avatarY] = Tileset.FLOOR;
                }
            } else if (c == 's' || c == 'S') {
                if (!world[avatarX][avatarY - 1].equals(Tileset.WALL)) {
                    avatarY = avatarY - 1;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                    world[avatarX][avatarY + 1] = Tileset.FLOOR;
                }
            } else if (c == 'D' || c == 'd') {
                if (!world[avatarX + 1][avatarY].equals(Tileset.WALL)) {
                    avatarX = avatarX + 1;
                    world[avatarX][avatarY] = Tileset.AVATAR;
                    world[avatarX - 1][avatarY] = Tileset.FLOOR;
                }
            }
        }

    }

    private void actionForInteract(TETile[][] world) {
        int avatarX = 0;
        int avatarY = 0;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (world[i][j].equals(Tileset.AVATAR)) {
                    avatarX = i;
                    avatarY = j;
                }
            }
        }
        InputSource inputSource = new KeyboardInputSource();
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (c == 'W' || c == 'w') {
                if (!world[avatarX][avatarY + 1].equals(Tileset.WALL)) {
                    if (world[avatarX][avatarY + 1].equals(Tileset.MOUNTAIN)) {
                        avatarY = avatarY + 1;
                        world[avatarX][avatarY] = Tileset.AVATAR;
                        world[avatarX][avatarY - 1] = Tileset.FLOOR;
                        enterNewWorld();
                        ter.initialize(WIDTH + 20, HEIGHT + 20);
                        ter.renderFrame(world);
                    } else {

                        avatarY = avatarY + 1;
                        world[avatarX][avatarY] = Tileset.AVATAR;
                        world[avatarX][avatarY - 1] = Tileset.FLOOR;
                    }
                }
            } else if (c == 'a' || c == 'A') {
                if (!world[avatarX - 1][avatarY].equals(Tileset.WALL)) {
                    if (world[avatarX - 1][avatarY].equals(Tileset.MOUNTAIN)) {
                        avatarX = avatarX - 1;
                        world[avatarX][avatarY] = Tileset.AVATAR;
                        world[avatarX + 1][avatarY] = Tileset.FLOOR;
                        enterNewWorld();
                        ter.initialize(WIDTH + 20, HEIGHT + 20);
                        ter.renderFrame(world);
                    } else {
                        avatarX = avatarX - 1;
                        world[avatarX][avatarY] = Tileset.AVATAR;
                        world[avatarX + 1][avatarY] = Tileset.FLOOR;
                    }
                }
            } else if (c == 's' || c == 'S') {
                if (!world[avatarX][avatarY - 1].equals(Tileset.WALL)) {
                    if (world[avatarX][avatarY - 1].equals(Tileset.MOUNTAIN)) {
                        avatarY = avatarY - 1;
                        world[avatarX][avatarY] = Tileset.AVATAR;
                        world[avatarX][avatarY + 1] = Tileset.FLOOR;
                        enterNewWorld();
                        ter.initialize(WIDTH + 20, HEIGHT + 20);
                        ter.renderFrame(world);

                    } else {
                        avatarY = avatarY - 1;
                        world[avatarX][avatarY] = Tileset.AVATAR;
                        world[avatarX][avatarY + 1] = Tileset.FLOOR;
                    }
                }
            } else if (c == 'D' || c == 'd') {
                if (!world[avatarX + 1][avatarY].equals(Tileset.WALL)) {
                    if (world[avatarX + 1][avatarY].equals(Tileset.MOUNTAIN)) {
                        avatarX = avatarX + 1;
                        world[avatarX][avatarY] = Tileset.AVATAR;
                        world[avatarX - 1][avatarY] = Tileset.FLOOR;
                        enterNewWorld();
                        ter.initialize(WIDTH + 20, HEIGHT + 20);
                        ter.renderFrame(world);
                    } else {
                        avatarX = avatarX + 1;
                        world[avatarX][avatarY] = Tileset.AVATAR;
                        world[avatarX - 1][avatarY] = Tileset.FLOOR;
                    }
                }
            } else if (c == ':') {
                //:q保存
                if (inputSource.possibleNextInput()) {
                    while (true) {
                        char temp = inputSource.getNextKey();
                        if (temp == 'm') {
                            double mouseX = StdDraw.mouseX();
                            double mouseY = StdDraw.mouseY();
                            StdDraw.setPenColor(Color.WHITE);
                            StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
                            StdDraw.text(world.length / 10.0, world[0].length * 1.4, world[(int)mouseX][(int)mouseY].description());
                            StdDraw.text(world.length / 10.0, world[0].length * 1.3, avatarName);
                            StdDraw.show();
                        }
                        else if (temp == 'Q' || temp == 'q') {
                            saveWorldToFile(world);
                            return;
                        } else {
                            break;
                        }
                    }


                }
            }
            else if (c == 'm') {
                // mouse hud
                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
                StdDraw.text(world.length / 10.0, world[0].length * 1.4, world[(int)mouseX][(int)mouseY].description());
                StdDraw.text(world.length / 10.0, world[0].length * 1.3, avatarName);
                StdDraw.show();
            }
            showWorld(world);
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
            else if (c != 'm'){
                seed += c;
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
                StdDraw.text(menuWidth / 2, menuHeight / 2, "Provide seed");
                StdDraw.text(menuWidth / 2, menuHeight / 5.0 * 2, seed.substring(1));
                StdDraw.show();
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
    private void showMenuNotAvatarName() {
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
        StdDraw.text(menuWidth / 2, menuHeight / 10 * 3, "Give yourself a Name(X)");
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
        createOneMountain(world, random);
        return world;
    }
    private void createOneMountain(TETile[][] world, Random random) {
        while (true) {
            int mountainX = RandomUtils.uniform(random, WIDTH);
            int mountainY = RandomUtils.uniform(random, HEIGHT);
            if (world[mountainX][mountainY].equals(Tileset.FLOOR)) {
                world[mountainX][mountainY] = Tileset.MOUNTAIN;
                break;
            }
        }
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

    /**
     * 把world转化为对应的Tileset的character成员变量的char二维数组，然后放在Core下的world.txt文件中
     * @param world
     */
    private void saveWorldToFile(TETile[][] world) {
        File worldFile = join(join(join(CWD.toString(), "byow").toString(), "Core").toString(), "world.txt");
        if (!worldFile.exists()) {
            try {
                worldFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        char[][] identicalString = new char[world.length][world[0].length];
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                identicalString[i][j] = world[i][j].character();
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(worldFile));){
            for (char[] row : identicalString) {
                for (int j = 0; j < row.length; j++) {
                    writer.write(row[j]); // 写当前字符
                }
                writer.newLine(); // 每行结束换行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 从world.txt中得到world数组
     * @return
     */
    private TETile[][] loadWorldFromFile() {
        File worldFile = join(join(join(CWD.toString(), "byow").toString(), "Core").toString(), "world.txt");
        List<char[]> rows = new ArrayList<char[]>();
        try (BufferedReader reader = new BufferedReader(new FileReader(worldFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 直接将每行字符串转换为char数组（没有空格分隔）
                char[] row = line.toCharArray();
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        char[][] identicalChar = rows.toArray(new char[0][]);
        TETile[][] world = new TETile[identicalChar.length][identicalChar[0].length];

        for (int i = 0; i < identicalChar.length; i++) {
            for (int j = 0; j < identicalChar[i].length; j++) {
                world[i][j] = charToTile(identicalChar[i][j]);
            }
        }
        return world;
    }
    private TETile charToTile(char c) {
        switch (c) {
            case '@':
                return Tileset.AVATAR;
            case '·':
                return Tileset.FLOOR;  // 注意：这个可能是中间点字符
            case '#':
                return Tileset.WALL;
            case ' ':
                return Tileset.NOTHING;
            case '❀':
                return Tileset.FLOWER;
            case '▲':
                return Tileset.MOUNTAIN;
        }
        return null;
    }
    /** Return the concatentation of FIRST and OTHERS into a File designator */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

}
