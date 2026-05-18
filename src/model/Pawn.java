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

        if (board.isInBounds(oneRow, col)) {
            Square one = board.getSquare(oneRow, col);
            if (!one.isOccupied()) {
                if (oneRow == promRow) {
                    addPromotions(moves, from, one, null);
                } else {
                    moves.add(new Move(from, one, this, null, MoveType.NORMAL));
                    // Double push from starting rank
                    if (row == startRow) {
                        Square two = board.getSquare(row + 2 * dir, col);
                        if (!two.isOccupied())
                            moves.add(new Move(from, two, this, null, MoveType.NORMAL));
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
