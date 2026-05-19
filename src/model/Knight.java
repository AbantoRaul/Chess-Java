package model;

import engine.Board;
import engine.Move;
import engine.MoveType;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece{
    public Knight(Color color) { super(color, PieceType.KNIGHT); }
    @Override public String getSymbol() { return super.getColor() == Color.WHITE ? "N" : "n"; }

    @Override
    public List<Move> getPseudoLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();
        int[][] jumps = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};

        for (int[] jump : jumps) {
            int newRow = from.getRow() + jump[0], newCol = from.getCol() + jump[1];

            if (!board.isInBounds(newRow, newCol)) continue;
            Square target = board.getSquare(newRow, newCol);

            if (!target.isOccupied()){
                moves.add(new Move(from, target,this,null, MoveType.NORMAL));
            }
            else if (target.getPiece().getColor() != super.getColor()){
                moves.add(new Move(from, target,this, target.getPiece(), MoveType.CAPTURE));
            }
        }
        return moves;
    }
}
