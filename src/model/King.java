package model;

public class King extends Piece{
    public King(Color color) { super(color, PieceType.KING); }

    @Override public String getSymbol() { return super.getColor() == Color.WHITE ? "K" : "k"; }

}
