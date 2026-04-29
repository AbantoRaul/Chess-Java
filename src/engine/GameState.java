package engine;

import model.Color;
import model.PieceType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameState {
    private final Board board;
    private Color currentTurn;
    private GameStatus status;
    private final List<Move> moveHistory;
    private int halfMoveClock;  // resets on pawn move or capture (50-move rule)
    private int fullMoveNumber; // increments after Black moves

    public GameState() {
        this.board = new Board();
        this.currentTurn = Color.WHITE;   // White always moves first
        this.status = GameStatus.ONGOING;
        this.moveHistory = new ArrayList<>();
        this.halfMoveClock = 0;
        this.fullMoveNumber = 1;
    }

    public Board getBoard(){ return board; }
    public Color getCurrentTurn(){ return currentTurn; }
    public GameStatus getStatus(){ return status; }
    public int getHalfMoveClock(){ return halfMoveClock; }
    public int getFullMoveNumber(){ return fullMoveNumber; }

    public List<Move> getMoveHistory(){
        return Collections.unmodifiableList(moveHistory);
    }

    public void setStatus(GameStatus newStatus){
        this.status = newStatus;
    }

    // Adds the move to history and updates the half-move clock
    public void recordMove(Move move){
        moveHistory.add(move);

        // Pawn moves and captures reset the 50-move draw clock
        boolean isPawnMove = move.getPiece().getType() == PieceType.PAWN;
        boolean isCapture  = move.isCapture();

        if (isPawnMove || isCapture) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }
    }

    // Flips the current turn and increments the full-move counter after Black plays
    public void switchTurn() {
        if (currentTurn == Color.BLACK) {
            fullMoveNumber++;
        }
        currentTurn = currentTurn.opposite();
    }

    // Returns true if the game can still be played
    public boolean isOngoing() {
        return status == GameStatus.ONGOING || status == GameStatus.CHECK;
    }

    // Returns formatted move history like "1. e2e4  e7e5"
    public String getFormattedHistory() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moveHistory.size(); i++) {
            boolean isWhiteMove = (i % 2 == 0);
            if (isWhiteMove) {
                sb.append((i / 2 + 1)).append(". ");
            }
            sb.append(moveHistory.get(i).toAlgebraic());
            sb.append(isWhiteMove ? "  " : "\n");
        }
        return sb.toString();
    }
}
