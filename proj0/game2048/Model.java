package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: bitterlucky
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        board.setViewingPerspective(side);
        // i 表示column
        for (int i = 0; i < board.size(); i++) {
            boolean[] hasMerged = {false, false, false, false};
            for (int j = 3; j >= 0; j--) {
                if (j == 3) {
                    continue;
                } else if (j == 2) {
                    //j = 2的地方没有tile
                    if (board.tile(i, 2) == null) {
                        continue;
                    } else {//j = 2的地方有tile
                        //j = 3的地方没有tile
                        if (board.tile(i, 3) == null) {

                            board.move(i, 3, board.tile(i, 2));
                            changed = true;
                        } else { //j=3的地方有tile
                            //如果两个tile值相等
                            if (board.tile(i, 3).value() == board.tile(i, 2).value()) {
                                int originValue = board.tile(i, 2).value();
                                board.move(i, 3, board.tile(i, 2));
                                changed = true;
                                score += 2 * originValue;
                                hasMerged[3] = true;
                            } else {//两个tile值不等
                                continue;
                            }
                        }
                    }
                } else if (j == 1) {
                    //如果j=1的地方没有tile
                    if (board.tile(i, 1) == null) {
                        continue;
                    } else {
                        //如果j=1的地方有tile
                        if (board.tile(i, 2) != null) {
                            //j == 2的地方有tile
                            if (board.tile(i, 1).value() == board.tile(i, 2).value()) {
                                //如果j==2和j==1的tile的value相等
                                int originValue = board.tile(i, 2).value();
                                board.move(i, 2, board.tile(i, 1));
                                changed = true;
                                score += 2 * originValue;
                                hasMerged[2] = true;
                            } else {
                               //如果j==2和j==1的tile的value不等
                               continue;
                            }
                        } else {
                            //j == 2的地方没有tile
                            if (board.tile(i, 3) == null) {
//                                j == 3的地方没有tile
                                board.move(i, 3, board.tile(i, 1));
                                changed = true;
                            } else {
                                //j == 3的地方有tile
                                if (hasMerged[3] == true) {
                                    //hasMerged[3] == true
                                    board.move(i, 2, board.tile(i, 1));
                                    changed = true;
                                } else {
                                    //hasMerged[3] = false
                                    if (board.tile(i, 3).value() == board.tile(i, 1).value()) {
                                        //tile的value相等
                                        int originValue = board.tile(i, 3).value();
                                        board.move(i, 3, board.tile(i, 1));
                                        changed = true;
                                        score += 2 * originValue;
                                        hasMerged[3] = true;
                                    } else {
                                        //tilevalue不等
                                        board.move(i, 2, board.tile(i, 1));
                                        changed = true;
                                    }
                                }
                            }
                        }
                    }
                } else if (j == 0) {
                    if (board.tile(i, 0) == null) {
                        //j = 0 的地方没有tile
                        continue;
                    } else {
                        //j == 0的地方有tile
                        if (board.tile(i, 1) != null) {
                            // j == 1的地方有tile
                            if (board.tile(i, 0).value() == board.tile(i, 1).value()) {
                                //j = 0的value和j=1的value相等
                                int originValue = board.tile(i, 0).value();
                                board.move(i, 1, board.tile(i, 0));
                                changed = true;
                                score += 2 * originValue;
                                hasMerged[1] = true;
                            } else {
                                //j=0的value和j=1的value不等
                                continue;
                            }
                        } else {
                            //j == 1的地方没有tile
                            if (board.tile(i, 2) != null) {
                                //j = 2的地方有tile
                                if (hasMerged[2] == true) {
                                    //hasMerged[2] = true
                                    board.move(i, 1, board.tile(i, 0));
                                    changed = true;
                                } else {
                                    //hasMerged[2] = false
                                    if (board.tile(i, 0).value() == board.tile(i, 2).value()) {
                                        //如果j=0的value和j=2的value相等
                                        int originValue = board.tile(i, 0).value();
                                        board.move(i, 2, board.tile(i, 0));
                                        changed = true;
                                        score += 2 * originValue;
                                        hasMerged[2] = true;
                                    } else {
                                        //j=0的value和j=2的value不等
                                        board.move(i, 1, board.tile(i, 0));
                                        changed = true;
                                    }
                                }
                            } else {
                                //j = 2的地方没有tile
                                if (board.tile(i, 3) == null) {
                                    //j = 3的地方没有tile
                                    board.move(i, 3, board.tile(i, 0));
                                    changed = true;
                                } else {
                                    //j = 3的地方有tile
                                    if (hasMerged[3] == true) {
                                        //hasMerged[3] = true
                                        board.move(i, 2, board.tile(i, 0));
                                        changed = true;
                                    } else {
                                        //hasMerged[3] = false
                                        if (board.tile(i, 0).value() == board.tile(i, 3).value()) {
                                            //j = 0 的value和j = 3的value相等
                                            int originValue = board.tile(i, 0).value();
                                            board.move(i, 3, board.tile(i, 0));
                                            changed = true;
                                            score += 2 * originValue;
                                            hasMerged[3] = true;
                                        } else {
                                            // j = 0的value和j=3的value不等
                                            board.move(i, 2, board.tile(i, 0));
                                            changed = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        board.setViewingPerspective(Side.NORTH);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }




    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (b.tile(i, j) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {

        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (b.tile(i, j) != null) {
                    if (b.tile(i, j).value() == MAX_PIECE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        if (emptySpaceExists(b)) {
            return true;
        }
        //i represents column, j represents row
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                if (b.tile(i, j) != null) {
                    int value = b.tile(i, j).value();
                    if (value == calculateTileValue(b, i - 1, j)) {
                        return true;
                    }
                    if (value == calculateTileValue(b, i + 1, j)) {
                        return true;
                    }
                    if (value == calculateTileValue(b, i, j + 1)) {
                        return true;
                    }
                    if (value == calculateTileValue(b, i, j - 1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * calculate the tile value for column i, row j
     * the tile exists return tile value otherwise return 0 (including i or j pass over the size)
     * @param b
     * @param i column
     * @param j row
     * @return
     */
    private static int calculateTileValue(Board b, int i, int j) {
        if (i < 0 || i > b.size() - 1 || j < 0 || j > b.size() - 1) {
            return 0;
        }
        if (b.tile(i, j) == null) {
            return 0;
        } else {
            return b.tile(i, j).value();
        }
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
