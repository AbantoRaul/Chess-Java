package ui;

import engine.Board;
import engine.Move;
import engine.MoveType;
import model.Color;
import model.Piece;
import model.PieceType;
import model.Square;
import java.util.Scanner;

//Mo read ug player input gikan sa terminal ug i convert into Move objects
public class InputParser {
    private final Scanner scanner;

    public InputParser() {
        this.scanner = new Scanner(System.in);
    }

    //Waits for the player to type and returns the input
    public String readInput() {
        System.out.print("> ");
        return scanner.nextLine().trim().toLowerCase();
    }
}
