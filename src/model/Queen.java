package model;

public class Queen extends Piece{
    public Queen(Color color) { super(color, PieceType.QUEEN); }
    @Override public String getSymbol() { return super.getColor() == Color.WHITE ? "Q" : "q"; }

}
