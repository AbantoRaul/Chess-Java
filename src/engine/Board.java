package engine;

import model.*;

public class Board {

    //The Board holds the 8x8 grid of squares and applies moves.
    public static final int SIZE = 8;
    private final Square[][] grid;
    private Square enPassantTarget;

    public Board() {
        grid = new Square[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = new Square(row, col);
            }
        }
        enPassantTarget = null;
    }

}
