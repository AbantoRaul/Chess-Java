package model;

public class Queen extends Piece{
    public Queen(Color color) { super(color, PieceType.QUEEN); }
    @Override public String getSymbol() { return super.getColor() == Color.WHITE ? "Q" : "q"; }

    @Override
    public List<Move> getPseudoLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};

        return moves;
    }
}
