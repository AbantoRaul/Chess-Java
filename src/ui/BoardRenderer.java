package ui;

import engine.Board;
import engine.GameState;
import engine.GameStatus;
import engine.Move;
import model.Color;
import model.Piece;
import model.Square;

import java.util.List;

//Prints chess board and game info to the terminal.
//Uses the same ANSI style as the full version.
//Only the status messages are simplified for the pawn-only demo.
public class BoardRenderer {

    // ── ANSI codes ────────────────────────────────────────
    private static final String RESET = "\u001B[0m";
    private static final String BOLD  = "\u001B[1m";
    private static final String WHITE_PIECE = "\u001B[1;97m"; // bold bright white
    private static final String BLACK_PIECE = "\u001B[1;93m"; // bold bright yellow
    private static final String GOLD = "\u001B[1;33m";
    private static final String CYAN = "\u001B[1;36m";
    private static final String GREEN = "\u001B[1;32m";
    private static final String RED = "\u001B[1;31m";
    private static final String DIM = "\u001B[2;37m";
    private static final String RANK_FG = "\u001B[38;5;214m"; // orange labels
}
