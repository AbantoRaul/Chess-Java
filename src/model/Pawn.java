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

        for (int dc : new int[]{-1, 1}) {
            int dc2 = col + dc;
            if (!board.isInBounds(oneRow, dc2)) continue;
            Square diag = board.getSquare(oneRow, dc2);

            // Normal diagonal capture
            if (diag.isOccupied() && diag.getPiece().getColor() != color) {
                if (oneRow == promRow) addPromotions(moves, from, diag, diag.getPiece());
                else moves.add(new Move(from, diag, this, diag.getPiece(), MoveType.CAPTURE));
            }

            // En passant
            Square ep = board.getEnPassantTarget();
            if (ep != null && ep.getRow() == oneRow && ep.getCol() == dc2) {
                Piece cap = board.getSquare(row, dc2).getPiece();
                if (cap != null) {
                    moves.add(new Move(from, ep, this, cap, MoveType.EN_PASSANT));
                }
            }

        }

        return moves;
    }

    private void addPromotions(List<Move> moves, Square from, Square to, Piece cap) {
        moves.add(new Move(from, to, this, cap, MoveType.PROMOTION, PieceType.QUEEN));
        moves.add(new Move(from, to, this, cap, MoveType.PROMOTION, PieceType.ROOK));
        moves.add(new Move(from, to, this, cap, MoveType.PROMOTION, PieceType.BISHOP));
        moves.add(new Move(from, to, this, cap, MoveType.PROMOTION, PieceType.KNIGHT));
    }
}
