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
}
