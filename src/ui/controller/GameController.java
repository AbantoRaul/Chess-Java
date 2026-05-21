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
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);

        menuPanel = new MenuPanel(this::startGame, cfg);
        rootPanel.add(menuPanel, CARD_MENU);

        rootPanel.add(buildGamePanel(), CARD_GAME);

        cardLayout.show(rootPanel, CARD_MENU);
        return rootPanel;
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

        // Tiled wood background
        JPanel outer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth(), h = getHeight();

                g2.setColor(new java.awt.Color(180, 140, 100));
                g2.fillRect(0, 0, w, h);

                for (int r = 0; r * tileSz < h + tileSz; r++) {
                    for (int c = 0; c * tileSz < w + tileSz; c++) {
                        g2.setColor((r + c) % 2 == 0
                                ? new java.awt.Color(200, 170, 130, 100)
                                : new java.awt.Color(150, 110, 70, 100));
                        g2.fillRect(c * tileSz, r * tileSz, tileSz, tileSz);
                    }
                }
            }
        };
        outer.setOpaque(true);

        // Top bar with pause button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        topBar.setOpaque(false);
        topBar.add(buildPauseButton(btnSize));

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

    // ── Getters ───────────────────────────────────────────────────────────
    public Square getSelectedSquare() { return selectedSquare; }
    public List<Move> getSelectedMoves() { return selectedMoves; }
    public GameState getGameState() { return gameState; }
    public MoveValidator getValidator() { return validator; }
    public int getLastFromRow() { return lastFromRow; }
    public int getLastFromCol() { return lastFromCol; }
    public int getLastToRow() { return lastToRow; }
    public int getLastToCol() { return lastToCol; }
    
    // ── Game actions ──────────────────────────────────────────────────────
    public void onNewGame() {

    }

    public void onResign() {

    }

    public void refreshAll() {
        if (boardPanel != null) boardPanel.repaint();
        if (sidePanel != null) sidePanel.update();
    }

    // ── Private helpers ───────────────────────────────────────────────────
    private void trySelect(Square sq) {

    }

    private void clearSelection() {
        selectedSquare = null;
        selectedMoves = null;
    }

    private Move findMoveToSquare(int row, int col) {
        if (selectedMoves == null) return null;

    }

    private Move askPromotion(List<Move> promos) {

    }

    private void executeMove(Move move) {

    }

    private void setEnPassantTarget(Move move, Board board) {

    }

    private void checkGameEnd() {
        awaitingPromotion = false;
        Color next = gameState.getCurrentTurn();

    }

    private void showEndDialog(String title, String message) {
        if (checkOverlay != null) checkOverlay.hideCheck();
        endGameOverlay.show(title, message);
    }

    private void newGame() {

    }
}
