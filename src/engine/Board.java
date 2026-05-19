package engine;

import model.*;

public class Board {

    //The Board holds the 8x8 grid of squares and applies moves.
    public static final int SIZE = 8;
    private final Square[][] grid;
    private Square enPassantTarget;

    public Board() {
        grid = new Square[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = new Square(row, col);
            }
        }
        enPassantTarget = null;
    }

    // Places only Pawns for the demo.
    // White Pawns on row 1 (rank 2), Black Pawns on row 6 (rank 7).
    public void initialize() {
        PieceType[] back = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        for (int c = 0; c < SIZE; c++) {
            grid[0][c].setPiece(createPiece(back[c], Color.WHITE));
            grid[1][c].setPiece(new Pawn(Color.WHITE));
            grid[7][c].setPiece(createPiece(back[c], Color.BLACK));
            grid[6][c].setPiece(new Pawn(Color.BLACK));
        }
    }

    private Piece createPiece(PieceType t, Color col) {
        return switch (t) {
            case ROOK -> new Rook(col);
            case KNIGHT -> new Knight(col);
            case BISHOP -> new Bishop(col);
            case QUEEN -> new Queen(col);
            case KING -> new King(col);
            default -> new Pawn(col);
        };
    }

    public Square getSquare(int row, int col) {
        return grid[row][col];
    }
    // Parses algebraic notation like "e2" → row=1, col=4
    public Square getSquare(String algebraic) {
        int col = algebraic.charAt(0) - 'a';
        int row = algebraic.charAt(1) - '1';
        return grid[row][col];
    }
    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public Square getEnPassantTarget(){ return enPassantTarget; }
    public void setEnPassantTarget(Square sq){ this.enPassantTarget = sq; }
    public void clearEnPassantTarget(){ this.enPassantTarget = null; }


    // Moves a piece from one square to another based on move type.
    // Only NORMAL, CAPTURE, and PROMOTION are relevant for the pawn demo.
    public void applyMove(Move move) {
        Square from  = move.getFrom();
        Square to = move.getTo();
        Piece piece = move.getPiece();
        clearEnPassantTarget();

        switch (move.getMoveType()) {

            case NORMAL, CAPTURE -> {

                to.setPiece(piece);
                from.clearPiece();
                piece.setMoved();
            }

            case EN_PASSANT -> {
                to.setPiece(piece);
                from.clearPiece();
                grid[from.getRow()][to.getCol()].clearPiece();
                piece.setMoved();
            }
            case CASTLE_KINGSIDE -> {
                to.setPiece(piece);
                from.clearPiece();
                piece.setMoved();
                Square krf = grid[from.getRow()][7];
                Square krt = grid[from.getRow()][5];
                krt.setPiece(krf.getPiece());
                krf.clearPiece();
                krt.getPiece().setMoved();
            }
            case CASTLE_QUEENSIDE -> {
                to.setPiece(piece);
                from.clearPiece();
                piece.setMoved();
                Square qrf = grid[from.getRow()][0];
                Square qrt = grid[from.getRow()][3];
                qrt.setPiece(qrf.getPiece());
                qrf.clearPiece();
                qrt.getPiece().setMoved();
            }

            case PROMOTION -> {
                Piece promoted = createPromotedPiece(move.getPromotionType(), piece.getColor());
                to.setPiece(promoted);
                from.clearPiece();
                promoted.setMoved();
            }
        }
    }

    private Piece createPromotedPiece(PieceType t, Color c) {
        if (t == null) return new Queen(c);

        return switch (t) {
            case QUEEN -> new Queen(c);
            case ROOK -> new Rook(c);
            case BISHOP -> new Bishop(c);
            case KNIGHT -> new Knight(c);
            default -> new Queen(c);
        };
    }

    public Square findKing(Color color) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Piece p = grid[r][c].getPiece();
                if (p != null && p.getColor() == color && p.getType() == PieceType.KING)
                    return grid[r][c];
            }
        }

        return null;
    }

    // Here ky mo create a copy of the board so MoveValidator can simulate moves
    // without changing the real game board.
    public Board deepCopy() {
        Board copy = new Board();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                copy.grid[r][c].setPiece(copyPiece(grid[r][c].getPiece()));
            }
        }
        copy.enPassantTarget = enPassantTarget == null
                ? null
                : copy.getSquare(enPassantTarget.getRow(), enPassantTarget.getCol());
        return copy;
    }

}
