package model;

public class King extends Piece{
    public King(Color color) { super(color, PieceType.KING); }

    @Override public String getSymbol() { return super.getColor() == Color.WHITE ? "K" : "k"; }

    @Override
    public List<Move> getPseudoLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};

        return List.of();
    }

}
