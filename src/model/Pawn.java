package model;

import engine.Board;
import engine.Move;
import engine.MoveType;

import java.util.ArrayList;
import java.util.List;

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
        List<Move> moves   = new ArrayList<>();
        int row = from.getRow(), col = from.getCol();
        int dir = color == Color.WHITE ? 1 : -1;
        int startRow = color == Color.WHITE ? 1 : 6;
        int promRow     = color == Color.WHITE ? 7 : 0;
        // Single push
        int oneRow = row + dir;

        if (board.isInBounds(oneStepRow, currentCol)) {
            Square oneStep = board.getSquare(oneStepRow, currentCol);

            if (!oneStep.isOccupied()) {
                if (oneStepRow == promotionRow) {
                    moves.add(new Move(from, oneStep, this, null, MoveType.PROMOTION, PieceType.QUEEN));
                    moves.add(new Move(from, oneStep, this, null, MoveType.PROMOTION, PieceType.ROOK));
                    moves.add(new Move(from, oneStep, this, null, MoveType.PROMOTION, PieceType.BISHOP));
                    moves.add(new Move(from, oneStep, this, null, MoveType.PROMOTION, PieceType.KNIGHT));
                } else {
                    moves.add(new Move(from, oneStep, this, null, MoveType.NORMAL));
                }

                if (currentRow == startingRow) {
                    int twoStepRow = currentRow + 2 * moveDirection;
                    Square twoStep = board.getSquare(twoStepRow, currentCol);
                    if (!twoStep.isOccupied()) {
                        moves.add(new Move(from, twoStep, this, null, MoveType.NORMAL));
                    }
                }
            }
        }

        int[] sideways = {-1, 1};

        for (int colOffset : sideways) {
            int diagCol = currentCol + colOffset;

            if (!board.isInBounds(oneStepRow, diagCol)) continue;

            Square diagonal = board.getSquare(oneStepRow, diagCol);

            // There must be an enemy piece on the diagonal square to capture
            if (diagonal.isOccupied() && diagonal.getPiece().getColor() != this.color) {
                if (oneStepRow == promotionRow) {
                    moves.add(new Move(from, diagonal, this, diagonal.getPiece(), MoveType.PROMOTION, PieceType.QUEEN));
                    moves.add(new Move(from, diagonal, this, diagonal.getPiece(), MoveType.PROMOTION, PieceType.ROOK));
                    moves.add(new Move(from, diagonal, this, diagonal.getPiece(), MoveType.PROMOTION, PieceType.BISHOP));
                    moves.add(new Move(from, diagonal, this, diagonal.getPiece(), MoveType.PROMOTION, PieceType.KNIGHT));
                } else {
                    moves.add(new Move(from, diagonal, this, diagonal.getPiece(), MoveType.CAPTURE));
                }
            }
        }

        return moves;
    }

}
