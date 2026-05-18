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

        if (!super.hasMoved()) moves.addAll(getCastlingMoves(board,from));

        return List.of();
    }

    private List<Move> getCastlingMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();
        int row = from.getRow();

        Square hRook = board.getSquare(row,7);
        if (hRook.isOccupied() &&
                hRook.getPiece().getType() == PieceType.ROOK &&
                hRook.getPiece().getColor() == super.getColor() &&
                !hRook.getPiece().hasMoved() &&
                !board.getSquare(row,5).isOccupied() &&
                !board.getSquare(row,6).isOccupied()) {
            moves.add(
                    new Move(from, board.getSquare(row, 6), this, null, MoveType.CASTLE_KINGSIDE)
            );
        }

        Square aRook = board.getSquare(row,0);
        if (aRook.isOccupied() &&
                aRook.getPiece().getType() == PieceType.ROOK &&
                aRook.getPiece().getColor() == super.getColor() &&
                !aRook.getPiece().hasMoved() &&
                !board.getSquare(row,1).isOccupied() &&
                !board.getSquare(row,2).isOccupied() &&
                !board.getSquare(row,3).isOccupied()) {
            moves.add(
                    new Move(from, board.getSquare(row, 2), this, null, MoveType.CASTLE_QUEENSIDE)
            );
        }

        return moves;
    }

}
