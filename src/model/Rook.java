package model;

public class Rook extends Piece{
    public Rook(Color color){ super(color, PieceType.ROOK); }
    @Override public String getSymbol(){ return super.getColor() == Color.WHITE ? "R" : "r"; }

}
