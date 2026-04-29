package engine;

// Here ky gi describes what kind of move was made so the board handles it correctly.
public enum MoveType {

    NORMAL,
    CAPTURE,
    EN_PASSANT,
    CASTLE_KINGSIDE,
    CASTLE_QUEENSIDE,
    PROMOTION
}
