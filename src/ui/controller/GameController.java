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

    private JPanel buildGamePanel() {
        int side = cfg.boardSide;
        int tileSz = Math.max(40, cfg.sq - 5);
        int btnSize = Math.max(36, cfg.pauseBtnR * 2 + 8);


        return outer;
    }

    private JButton buildPauseButton(int size) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            }
        };

        return btn;
    }

    // ── Pause / Resume ────────────────────────────────────────────────────
    public void onPauseClicked() {
        paused = true;
        pauseOverlay.setVisible(true);
        pauseOverlay.playOpenAnimation();
    }

    private void resumeGame() {
        paused = false;
        pauseOverlay.setVisible(false);
        refreshAll();
    }

    private void goToMenu() {

    }

    // ── Square interaction ────────────────────────────────────────────────
    public void onSquareClicked(int row, int col) {
        Square sq = gameState.getBoard().getSquare(row, col);
    }

    public void onDragCompleted(int fromRow, int fromCol, int toRow, int toCol) {
        Square from = gameState.getBoard().getSquare(fromRow, fromCol);

    }
}
