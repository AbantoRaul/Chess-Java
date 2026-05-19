package model;

import engine.Board;
import engine.Move;
import engine.MoveType;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece{
    public Rook(Color color){ super(color, PieceType.ROOK); }
    @Override public String getSymbol(){ return super.getColor() == Color.WHITE ? "R" : "r"; }

    @Override
    public List<Move> getPseudoLegalMoves(Board board, Square from) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};

        for (int[] dir : directions) {
            int newRow = from.getRow() + dir[0], newCol = from.getCol() + dir[1];

            while (board.isInBounds(newRow, newCol)) {
                Square target = board.getSquare(newRow, newCol);
                if (!target.isOccupied()){
                    moves.add(new Move(from, target,this,null, MoveType.NORMAL));
                }
                else {
                    if (target.getPiece().getColor() != super.getColor()){
                        moves.add(new Move(from, target,this, target.getPiece(), MoveType.CAPTURE));
                    }
                    break;
                }
                newRow += dir[0];
                newCol += dir[1];
            }
        }
        return moves;
    }
}
