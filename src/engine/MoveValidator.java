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

    private boolean leavesKingInCheck(Move move) {
        Board copy = gameState.getBoard().deepCopy();
        Square cf = copy.getSquare(move.getFrom().getRow(), move.getFrom().getCol());
        Square ct = copy.getSquare(move.getTo().getRow(),   move.getTo().getCol());
        Piece piece = cf.getPiece();

        if (move.getMoveType() == MoveType.EN_PASSANT)
            copy.getSquare(cf.getRow(), ct.getCol()).clearPiece();

        if (move.getMoveType() == MoveType.PROMOTION && move.getPromotionType() != null)
            ct.setPiece(createPromoPiece(move.getPromotionType(), piece.getColor()));
        else
            ct.setPiece(piece);

        cf.clearPiece();

        if (move.getMoveType() == MoveType.CASTLE_KINGSIDE) {
            Square rf = copy.getSquare(cf.getRow(), 7);
            Square rt = copy.getSquare(cf.getRow(), 5);
            rt.setPiece(rf.getPiece());
            rf.clearPiece();
        } else if (move.getMoveType() == MoveType.CASTLE_QUEENSIDE) {
            Square rf = copy.getSquare(cf.getRow(), 0);
            Square rt = copy.getSquare(cf.getRow(), 3);
            rt.setPiece(rf.getPiece());
            rf.clearPiece();
        }

        Color mc = piece.getColor();
        Square king = copy.findKing(mc);
        if (king == null) return true;
        return isSquareAttackedBy(king, mc.opposite(), copy);
    }

    private Piece createPromoPiece(PieceType t, Color c) {
        return switch (t) {
            case QUEEN -> new Queen(c);
            case ROOK -> new Rook(c);
            case BISHOP -> new Bishop(c);
            case KNIGHT -> new Knight(c);
            default -> new Queen(c);
        };
    }
}
