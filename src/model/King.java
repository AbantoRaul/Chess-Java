package model;

public class King extends Piece{
    public King(Color color) { super(color, PieceType.KING); }

    @Override public String getSymbol() { return super.getColor() == Color.WHITE ? "K" : "k"; }

    @Override
    public List<Move> getPseudoLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};

        for (int[] dir : directions) {
            int newRow = from.getRow() + dir[0], newCol = from.getCol() + dir[1];

            if (!board.isInBounds(newRow, newCol)) continue;
            Square target = board.getSquare(newRow, newCol);

            if (!target.isOccupied()) {
                moves.add(new Move(from, target, this, null, MoveType.NORMAL));
            }
            else if (target.getPiece().getColor() != super.getColor()){
                moves.add(new Move(from, target, this, target.getPiece(), MoveType.CAPTURE));
            }
        }

        return List.of();
    }

}
