package ui;

import engine.*;
import model.*;

//Runs the game loop and connects all parts together
public class GameController {
    private final GameState gameState;
    private final MoveValidator validator;
    private final BoardRenderer renderer;
    private final InputParser parser;

    public GameController() {
        this.gameState = new GameState();
        this.validator = new MoveValidator(gameState);
        this.renderer = new BoardRenderer();
        this.parser = new InputParser();
    }

    //Sets up the board and starts the game
    public void start() {
        renderer.printWelcome();
        gameState.getBoard().initialize();
        gameLoop();
    }

    //Repeats every turn until the game ends or a player resigns
    private void gameLoop() {
        while (gameState.isOngoing()) {
            renderer.render(gameState);

            String input = parser.readInput();

            if (input.equals("resign")) {
                handleResign();
                break;
            }

            if (input.equals("history")) {
                renderer.printMoveHistory(gameState.getMoveHistory());
                continue;
            }

            processMove(input);
        }

        //Shows the final board state after the game ends
        renderer.render(gameState);
    }

    //Tries to parse, validate, and apply the player's move
    private void processMove(String input) {
        Board board = gameState.getBoard();
        Color currentPlayer = gameState.getCurrentTurn();

        //Step 1: Parse raw input "e2 e4" into a Move object
        Move move = parser.parseMove(input, board);
        if (move == null) {
            renderer.printError("Invalid format. Please type a move like: e2 e4");
            return;
        }

        //Step 2: Make sure the player is moving their own piece
        if (!move.getFrom().isOccupied()) {
            renderer.printError("There is no piece on that square.");
            return;
        }

        if (move.getPiece().getColor() != currentPlayer) {
            renderer.printError("That is not your piece.");
            return;
        }

        //Step 3: Check if the move is in the piece's legal move list
        if (!validator.isLegalMove(move)) {
            renderer.printError("That move is not allowed.");
            return;
        }

        //Step 4: Apply the move to the board
        board.applyMove(move);

        //Step 5: Set en passant target if a pawn moved 2 squares
        setEnPassantTarget(move, board);

        //Step 6: Handle pawn promotion
        if (move.getMoveType() == MoveType.PROMOTION) {
            handlePromotion(move);
        }

        //Step 7: Record the move and switch to the other player
        gameState.recordMove(move);
        gameState.switchTurn();

        //Step 8: Check if the game has ended
        checkGameEnd();
    }

    //After a pawn moves 2 squares, record the skipped square for en passant
    private void setEnPassantTarget(Move move, Board board) {
        board.clearEnPassantTarget();

        boolean isPawn = move.getPiece().getType() == PieceType.PAWN;
        int rowDifference = Math.abs(move.getTo().getRow() - move.getFrom().getRow());

        if (isPawn && rowDifference == 2) {
            int skippedRow = (move.getFrom().getRow() + move.getTo().getRow()) / 2;
            board.setEnPassantTarget(board.getSquare(skippedRow, move.getFrom().getCol()));
        }
    }

    //Asks the player what piece they want and replaces the pawn on the board
    private void handlePromotion(Move move) {
        System.out.println("Your pawn has reached the other side!");
        PieceType chosenType = parser.readPromotionChoice();
        Piece newPiece = createPromotedPiece(chosenType, move.getPiece().getColor());
        move.getTo().setPiece(newPiece);
    }

    //Creates the promoted piece based on the player's choice
    private Piece createPromotedPiece(PieceType type, Color color) {
        switch (type) {
            // Uncomment each case when the corresponding class is added:
            // case ROOK:   return new Rook(color);
            // case BISHOP: return new Bishop(color);
            // case KNIGHT: return new Knight(color);
            case QUEEN:
            default:
                // Inline anonymous Queen for the demo — no Queen.java needed yet
                return new Piece(color, PieceType.QUEEN) {
                    @Override
                    public String getSymbol() {
                        return color == Color.WHITE ? "Q" : "q";
                    }
                    @Override
                    public java.util.List<Move> getPseudoLegalMoves(engine.Board board, model.Square from) {
                        return new java.util.ArrayList<>(); // Queen moves added later
                    }
                };
        }
    }

    //Checks if the current player is in stalemate after the last move
    private void checkGameEnd() {
        Color nextPlayer = gameState.getCurrentTurn();

        if (validator.isStalemate(nextPlayer)) {
            gameState.setStatus(GameStatus.STALEMATE);
        } else {
            gameState.setStatus(GameStatus.ONGOING);
        }
    }
}
