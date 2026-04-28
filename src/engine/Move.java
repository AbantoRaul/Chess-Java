package engine;

import model.Piece;
import model.PieceType;
import model.Square;

// Stores everything here about one chess move
public class Move {

    private final Square from;
    private final Square to;
    private final Piece piece;
    private final Piece capturedPiece;
    private final MoveType type;
    private final PieceType promotionType; // null unless PROMOTION

    // Constructor for normal moves
    public Move(Square from, Square to, Piece piece, Piece capturedPiece, MoveType type) {
        this(from, to, piece, capturedPiece, type, null);
    }

    // Constructor para sa promotion moves
    public Move(Square from, Square to, Piece piece, Piece capturedPiece,
                MoveType type, PieceType promotionType) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.type = type;
        this.promotionType = promotionType;
    }
}
