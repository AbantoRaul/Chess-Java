package model;

public abstract class Piece {
    protected Color     color;
    protected PieceType type;
    protected boolean   hasMoved;

    public Piece(Color color, PieceType type) {
        this.color    = color;
        this.type     = type;
        this.hasMoved = false;
    }

    public Color getColor(){ return color; }
    public PieceType getType(){ return type; }
    public boolean hasMoved(){ return hasMoved; }

    public void setMoved() {
        this.hasMoved = true;
    }

    public abstract String getSymbol();
}
