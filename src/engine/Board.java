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

    // Places only Pawns for the demo.
    // White Pawns on row 1 (rank 2), Black Pawns on row 6 (rank 7).
    public void initialize() {
        for (int col = 0; col < SIZE; col++) {
            grid[1][col].setPiece(new Pawn(Color.WHITE)); // White pawns — rank 2
            grid[6][col].setPiece(new Pawn(Color.BLACK)); // Black pawns — rank 7
        }
    }

    public Square getSquare(int row, int col) {
        return grid[row][col];
    }
    // Parses algebraic notation like "e2" → row=1, col=4
    public Square getSquare(String algebraic) {
        int col = algebraic.charAt(0) - 'a';
        int row = algebraic.charAt(1) - '1';
        return grid[row][col];
    }
    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }


}
