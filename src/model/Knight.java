package model;

public class Knight extends Piece{
    public Knight(Color color) { super(color, PieceType.KNIGHT); }
    @Override public String getSymbol() { return super.getColor() == Color.WHITE ? "N" : "n"; }

    @Override
    public List<Move> getPseudoLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();

        return moves;
    }
}
