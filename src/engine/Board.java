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

    public Square getEnPassantTarget(){ return enPassantTarget; }
    public void setEnPassantTarget(Square sq){ this.enPassantTarget = sq; }
    public void clearEnPassantTarget(){ this.enPassantTarget = null; }


    // Moves a piece from one square to another based on move type.
    // Only NORMAL, CAPTURE, and PROMOTION are relevant for the pawn demo.
    public void applyMove(Move move) {
        Square from  = move.getFrom();
        Square to = move.getTo();
        Piece piece = move.getPiece();
        clearEnPassantTarget();

        switch (move.getMoveType()) {

            case NORMAL:
            case CAPTURE:
                to.setPiece(piece);
                from.clearPiece();
                piece.setMoved();
                break;

            case PROMOTION:
                // Piece replacement is handled by GameController after prompting the player
                // board just clears the source.
                to.setPiece(piece);
                from.clearPiece();
                piece.setMoved();
                break;

            // EN_PASSANT, CASTLE_KINGSIDE, CASTLE_QUEENSIDE
            // are no-ops until the full piece set is added.
            default:
                break;
        }
    }

    // Here ky mo create a copy of the board so MoveValidator can simulate moves
    // without changing the real game board.
    public Board deepCopy() {
        Board copy = new Board();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                copy.grid[row][col].setPiece(grid[row][col].getPiece());
            }
        }
        copy.enPassantTarget = this.enPassantTarget;
        return copy;
    }

}
