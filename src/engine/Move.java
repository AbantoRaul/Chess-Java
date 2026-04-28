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

}
