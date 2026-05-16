package model;

public class Rook extends Piece{
    public Rook(Color color){ super(color, PieceType.ROOK); }
    @Override public String getSymbol(){ return super.getColor() == Color.WHITE ? "R" : "r"; }

    @Override
    public List<Move> getPseudoLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();

        return moves;
    }
}
