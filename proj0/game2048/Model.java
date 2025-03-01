package game2048;

import java.util.Formatter;
import java.util.Iterator;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
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

    /**
     * 将棋盘倾斜到指定的方向（SIDE）。
     * 如果此操作改变了棋盘，则返回 true。
     *
     * 1. 如果两个相邻的 Tile 对象在运动方向上具有相同的值，它们会合并成一个值为原始值两倍的新 Tile，
     *    并且这个新值会被加到得分（score）实例变量中。
     *
     * 2. 合并后的 Tile 将不会在此次倾斜操作中再次合并。因此，每次移动时，每个 Tile 最多只会参与一次合并（可能是零次）。
     *
     * 3. 当三个相邻的 Tile 在运动方向上具有相同的值时，前两个 Tile 会合并，而第三个 Tile 不会参与合并。
     */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        int bound = board.size();
        boolean[][] has_merge = new boolean[bound][bound];
        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        board.setViewingPerspective(side);
        int jug_cur_row = bound - 2;
        while(jug_cur_row >=0) {
            for (int jug_cur_col = 0; jug_cur_col < bound; jug_cur_col++) {
                Tile cur = tile(jug_cur_col, jug_cur_row);
                if (cur == null) {
                    continue;
                }
                int mer_row = jug_cur_row + 1;
                int mer_col = jug_cur_col;
                while(tile(mer_col, mer_row) == null && mer_row < bound - 1) {
                    mer_row++;
                }
                // 停下来的位置是需要考虑合并/移动的位置,会首先考虑到非空的位置，考虑合并它
                boolean success = false;
                if( tile(mer_col,mer_row) != null && !has_merge[mer_col][mer_row]) {
                    if(tile(mer_col,mer_row).value() == cur.value()) {
                        has_merge[mer_col][mer_row] = true;
                        board.move(mer_col,mer_row,cur);
                        score += cur.value() * 2;
                        changed = true;
                        success = true;
                    }
                }
                // 先考虑是不是合并的位置为空,再考虑是不是因为不一致而合并不了
                if( tile(mer_col,mer_row) == null ) {
                    board.move(mer_col,mer_row,cur);
                    success = true;
                    changed = true;
                }
                if( !success) {
                    mer_row --;
                    //这里其实就是移动到null的位置上
                    if( mer_row > jug_cur_row ) {
                        board.move(mer_col,mer_row,cur);
                        success = true;
                        changed = true;
                    }
                }
            }
            jug_cur_row--;
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
        // TODO: Fill in this function.
        Iterator<Tile> it = b.iterator();
        while(it.hasNext()) {
            Tile t = it.next();
            if(t == null ) {
                return true;
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
        // TODO: Fill in this function.
        Iterator<Tile> it = b.iterator();
        while(it.hasNext()) {
            Tile t = it.next();
            if( t != null) {
                if (t.value() == MAX_PIECE) {
                    return true;
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
        // TODO: Fill in this function.
        Iterator<Tile> it = b.iterator();
        while(it.hasNext()) {
            Tile t = it.next();
            if( t == null ) {
                return true;
            }
        }
        // 下面保证棋盘为满
        int[] off_set_col = {0, -1, 0, 1};
        int[] off_set_row = {-1, 0, 1, 0};
        it = b.iterator();
        while(it.hasNext()) {
            Tile t = it.next();
            int col = t.col();
            int row = t.row();
            int length = b.size();
            for(int i = 0; i < 4; i++) {
                int jug_col = col + off_set_col[i];
                int jug_row = row + off_set_row[i];
                if (jug_row < 0 || jug_row >= length || jug_col >= length || jug_col < 0) {
                    continue;
                }
                if (b.tile(col, row).value() == b.tile(jug_col, jug_row).value()) {
                    return true;
                }
            }
        }
        return false;
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
