package ui;

import engine.*;
import model.*;

//Runs the game loop and connects all parts together
public class GameController {
    private final GameState gameState;
    private final MoveValidator validator;
    private final BoardRenderer renderer;
    private final InputParser parser;
}
