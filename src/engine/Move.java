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

    public Square getFrom(){ return from; }
    public Square getTo(){ return to; }
    public Piece getPiece(){ return piece; }
    public Piece getCapturedPiece(){ return capturedPiece; }
    public MoveType getMoveType(){ return type; }
    public PieceType getPromotionType(){ return promotionType; }

    public boolean isCapture(){
        return type == MoveType.CAPTURE || type == MoveType.EN_PASSANT;
    }

    // Returns the move as a readable string like "e2e4" or "e7e8Q"
    public String toAlgebraic() {
        String move = from.toAlgebraic() + to.toAlgebraic();
        if (type == MoveType.PROMOTION && promotionType != null) {
            move += promotionType.name().charAt(0);
        }
        return move;
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }

}

