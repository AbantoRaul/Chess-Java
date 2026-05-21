package ui.controller;

public class GameController {
    private static final String CARD_MENU = "MENU";
    private static final String CARD_GAME = "GAME";

    // ── Game state ────────────────────────────────────────────────────────
    private GameState gameState;
    private MoveValidator validator;
    private boolean paused = false;
    private boolean awaitingPromotion = false;

    // ── Selection state ───────────────────────────────────────────────────
    private Square selectedSquare;
    private List<Move> selectedMoves;
    private int lastFromRow = -1, lastFromCol = -1;
    private int lastToRow = -1, lastToCol = -1;

    // ── UI components ─────────────────────────────────────────────────────
    private final JFrame frame;
    private final LayoutConfig cfg;
    private JPanel rootPanel;
    private CardLayout cardLayout;
    private BoardPanel boardPanel;
    private SidePanel sidePanel;
    private MenuPanel menuPanel;
    private PauseOverlay pauseOverlay;
    private CheckOverlay checkOverlay;
    private EndGameOverlay endGameOverlay;
    private PromotionOverlay promotionOverlay;

    public GameController(JFrame frame, int usableW, int usableH) {
        this.frame = frame;
        this.cfg = new LayoutConfig(usableW, usableH);
        newGame();
    }

    // ── Main panel setup ──────────────────────────────────────────────────
    public JPanel buildMainPanel() {

    }

    private void startGame() {
        menuPanel.stopAnimation();
        newGame();
        refreshAll();
        cardLayout.show(rootPanel, CARD_GAME);
    }
}
