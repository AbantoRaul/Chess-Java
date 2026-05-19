package engine;

import model.*;
import java.util.ArrayList;
import java.util.List;


public class MoveValidator {

    private final GameState gameState;

    public MoveValidator(GameState gameState) {
        this.gameState = gameState;
    }

    public List<Move> getLegalMoves(Square square) {
        List<Move> legal = new ArrayList<>();

        if (!square.isOccupied()) return legal;

        Piece piece = square.getPiece();
        Board board = gameState.getBoard();

        for (Move m : piece.getPseudoLegalMoves(board, square))
            if (!leavesKingInCheck(m) && !castlesThroughCheck(m)) legal.add(m);
        return legal;
    }

    public boolean isInCheck(Color color) {
        Board board = gameState.getBoard();
        Square king = board.findKing(color);
        return king != null && isSquareAttackedBy(king, color.opposite(), board);
    }

    public boolean isCheckmate(Color color) {
        return isInCheck(color) && getAllLegalMoves(color).isEmpty();
    }


    public boolean isStalemate(Color color) {
        return !isInCheck(color) && getAllLegalMoves(color).isEmpty();
    }

    //Mo returns every legal move available to the given color across all their pieces
    public List<Move> getAllLegalMoves(Color color) {
        List<Move> all = new ArrayList<>();
        Board board = gameState.getBoard();

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Square sq = board.getSquare(r, c);

                if (sq.isOccupied() && sq.getPiece().getColor() == color)
                    all.addAll(getLegalMoves(sq));
            }
        }
        return all;
    }
}
