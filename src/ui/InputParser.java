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

    //Asks the player to choose a promotion piece
    public PieceType readPromotionChoice() {
        System.out.println("Promote pawn to: [Q]ueen  [R]ook  [B]ishop  [N]knight");

        while (true) {
            String input = readInput();
            switch (input) {
                case "q": case "queen": return PieceType.QUEEN;
                case "r": case "rook": return PieceType.ROOK;
                case "b": case "bishop": return PieceType.BISHOP;
                case "n": case "knight": return PieceType.KNIGHT;
                default:
                    System.out.println("Invalid choice. Please type Q, R, B, or N.");
            }
        }
    }
}
