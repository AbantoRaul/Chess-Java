package model;

public class Pawn extends Piece{
    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public String getSymbol() {
        return color == Color.WHITE ? "P" : "p";
    }

    @Override
    public List<Move> getPseudoLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();

        int currentRow = from.getRow();
        int currentCol = from.getCol();

        int moveDirection = (color == Color.WHITE) ? 1 : -1;
        int startingRow   = (color == Color.WHITE) ? 1 : 6;
        int promotionRow  = (color == Color.WHITE) ? 7 : 0;

        int oneStepRow = currentRow + moveDirection;

        if (board.isInBounds(oneStepRow, currentCol)) {
            Square oneStep = board.getSquare(oneStepRow, currentCol);

            if (!oneStep.isOccupied()) {
                if (currentRow == startingRow) {
                    int twoStepRow = currentRow + 2 * moveDirection;
                    Square twoStep = board.getSquare(twoStepRow, currentCol);
                    if (!twoStep.isOccupied()) {
                        moves.add(new Move(from, twoStep, this, null, MoveType.NORMAL));
                    }
                }
            }
        }

        return moves;
    }

}
