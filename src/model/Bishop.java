package model;

public class Bishop extends Piece{
    public Bishop(Color color) { super(color, PieceType.BISHOP); }

    @Override public String getSymbol() { return super.getColor() == Color.WHITE ? "B" : "b"; }

}
