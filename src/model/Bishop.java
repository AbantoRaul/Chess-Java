package model;

public class Bishop extends Piece{
    public Bishop(Color color) { super(color, PieceType.BISHOP); }

    @Override public String getSymbol() { return super.getColor() == Color.WHITE ? "B" : "b"; }

    @Override
    public List<Move> getPseudoLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();

        // TODO: Add diagonal sliding logic (Steps 2-5)
        return moves;
    }
}
