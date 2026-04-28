package model;

public class Piece {
    protected Color     color;
    protected PieceType type;
    protected boolean   hasMoved;

    public Piece(Color color, PieceType type) {
        this.color    = color;
        this.type     = type;
        this.hasMoved = false;
    }
}
