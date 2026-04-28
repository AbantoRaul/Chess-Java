package model;

public class Square {
    private final int row;
    private final int col;
    private Piece piece; // null if empty

    public Square(int row, int col) {
        this.row   = row;
        this.col   = col;
        this.piece = null;
    }

}
