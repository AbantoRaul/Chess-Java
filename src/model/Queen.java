package model;

public class Queen extends Piece{
    public Queen(Color color) { super(color, PieceType.QUEEN); }
    @Override public String getSymbol() { return super.getColor() == Color.WHITE ? "Q" : "q"; }

    @Override
    public List<Move> getPseudoLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};

        for (int[] dir : directions) {
            int newRow = from.getRow() + dir[0], newCol = from.getCol() + dir[1];

            while (board.isInBounds(newRow, newCol)) {
                Square target = board.getSquare(newRow,newCol);

                if (!target.isOccupied()){
                    moves.add(new Move(from, target,this,null, MoveType.NORMAL));
                }

                newRow += dir[0];
                newCol += dir[1];
            }
        }
        return moves;
    }
}
