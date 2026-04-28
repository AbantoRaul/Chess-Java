package model;

public class Square {
    private final int row;
    private final int col;
    private Piece piece; // null if empty

    public Square(int row, int col) {
        this.row   = row;
        this.col   = col;
        this.piece = null;
    }

    public int getRow(){ return row; }
    public int getCol(){ return col; }
    public Piece getPiece(){ return piece; }
    public boolean isOccupied(){ return piece != null; }

    public void setPiece(Piece piece){ this.piece = piece; }
    public void clearPiece(){ this.piece = null; }

    public String toAlgebraic() {
        char file = (char) ('a' + col);
        int  rank = row + 1;
        return "" + file + rank;
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }
}
