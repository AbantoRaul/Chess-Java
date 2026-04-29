package engine;

import model.Color;
import model.Piece;
import model.Square;
import java.util.ArrayList;
import java.util.List;

// MoveValidator checks whether a move is legal before it is applied.
// Filters pseudo-legal moves by simulating them and checking if the king is left in check.
// NOTE (Pawn Demo): leavesKingInCheck() is skipped since there is no King on the board yet.
// Will be re-enabled once King and other pieces are added.

public class MoveValidator {

    private final GameState gameState;

    public MoveValidator(GameState gameState) {
        this.gameState = gameState;
    }

    public List<Move> getLegalMoves(Square square) {
        List<Move> legalMoves = new ArrayList<>();

        if (!square.isOccupied()) return legalMoves;

        Piece piece = square.getPiece();
        Board board = gameState.getBoard();

        // Get all moves the piece can make based on movement rules
        List<Move> pseudoLegal = piece.getPseudoLegalMoves(board, square);

        // For now, every pseudo-legal move is accepted directly
        legalMoves.addAll(pseudoLegal);

        return legalMoves;
    }

    // Mo Returns true if the given move is in the piece's legal move list
    public boolean isLegalMove(Move move) {
        List<Move> legal = getLegalMoves(move.getFrom());
        for (Move m : legal) {
            if (m.getFrom().getRow() == move.getFrom().getRow()
                    && m.getFrom().getCol() == move.getFrom().getCol()
                    && m.getTo().getRow()   == move.getTo().getRow()
                    && m.getTo().getCol()   == move.getTo().getCol()) {
                return true;
            }
        }
        return false;
    }

    // Returns true if the current player has no legal moves.
    // Without a king, stalemate occurs if all pawns are blocked.
    public boolean isStalemate(Color color) {
        return getAllLegalMoves(color).isEmpty();
    }

    //Mo returns every legal move available to the given color across all their pieces
    private List<Move> getAllLegalMoves(Color color) {
        List<Move> allMoves = new ArrayList<>();
        Board board = gameState.getBoard();

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Square sq = board.getSquare(row, col);
                if (sq.isOccupied() && sq.getPiece().getColor() == color) {
                    allMoves.addAll(getLegalMoves(sq));
                }
            }
        }

        return allMoves;
    }
}
