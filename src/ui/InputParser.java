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

    //Converts a raw input string like "e2 e4" into a Move object
    //Mo return ug null if ang format kay invalid
    public Move parseMove(String input, Board board) {
        String cleaned = input.replace(" ", "");

        if (!isValidFormat(cleaned)) return null;

        int fromCol = cleaned.charAt(0) - 'a';
        int fromRow = cleaned.charAt(1) - '1';
        int toCol = cleaned.charAt(2) - 'a';
        int toRow = cleaned.charAt(3) - '1';

        if (!board.isInBounds(fromRow, fromCol) || !board.isInBounds(toRow, toCol)) {
            return null;
        }

        Square from = board.getSquare(fromRow, fromCol);
        Square to = board.getSquare(toRow, toCol);

        if (!from.isOccupied()) return null;

        Piece piece = from.getPiece();
        Piece capturedPiece = to.isOccupied() ? to.getPiece() : null;
        MoveType moveType = determineMoveType(piece, from, to, capturedPiece, board);

        return new Move(from, to, piece, capturedPiece, moveType);
    }

    //Validates the format. Must be 4 characters like "e2e4"
    public boolean isValidFormat(String input) {
        if (input.length() != 4) return false;

        char fromFile = input.charAt(0);
        char fromRank = input.charAt(1);
        char toFile = input.charAt(2);
        char toRank = input.charAt(3);

        boolean fromOk = (fromFile >= 'a' && fromFile <= 'h') && (fromRank >= '1' && fromRank <= '8');
        boolean toOk = (toFile   >= 'a' && toFile   <= 'h') && (toRank   >= '1' && toRank   <= '8');

        return fromOk && toOk;
    }
}
