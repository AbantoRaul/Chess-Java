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

        // Layered pane: board + overlays stacked
        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(new Dimension(side, side));
        layered.setMinimumSize(new Dimension(side, side));
        layered.setMaximumSize(new Dimension(side, side));

        boardPanel = new BoardPanel(this, cfg);
        boardPanel.setBounds(0, 0, side, side);
        layered.add(boardPanel, JLayeredPane.DEFAULT_LAYER);

        checkOverlay = new CheckOverlay();
        checkOverlay.setBounds(0, 0, side, side);
        checkOverlay.setVisible(false);
        layered.add(checkOverlay, JLayeredPane.PALETTE_LAYER);

        pauseOverlay = new PauseOverlay(this::resumeGame, this::onNewGame, this::goToMenu);
        pauseOverlay.setBounds(0, 0, side, side);
        pauseOverlay.setVisible(false);
        layered.add(pauseOverlay, JLayeredPane.MODAL_LAYER);

        endGameOverlay = new EndGameOverlay(this::onNewGame, this::goToMenu, () -> System.exit(0));
        endGameOverlay.setBounds(0, 0, side, side);
        endGameOverlay.setVisible(false);
        endGameOverlay.setEnabled(false);
        layered.add(endGameOverlay, JLayeredPane.POPUP_LAYER);

        promotionOverlay = new PromotionOverlay();
        promotionOverlay.setBounds(0, 0, side, side);
        promotionOverlay.setVisible(false);
        promotionOverlay.setEnabled(false);
        layered.add(promotionOverlay, Integer.valueOf(JLayeredPane.POPUP_LAYER + 100));

        // Keep overlay bounds in sync when the layered pane resizes
        layered.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                int w = layered.getWidth(), h = layered.getHeight();
                boardPanel.setBounds(0, 0, w, h);
                checkOverlay.setBounds(0, 0, w, h);
                pauseOverlay.setBounds(0, 0, w, h);
                endGameOverlay.setBounds(0, 0, w, h);
                promotionOverlay.setBounds(0, 0, w, h);
            }
        });

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(layered, new GridBagConstraints());

        JPanel boardArea = new JPanel(new BorderLayout());
        boardArea.setOpaque(false);
        boardArea.add(topBar, BorderLayout.NORTH);
        boardArea.add(centerWrapper, BorderLayout.CENTER);

        sidePanel = new SidePanel(this, cfg);

        outer.add(boardArea, BorderLayout.CENTER);
        outer.add(sidePanel, BorderLayout.EAST);

        return outer;
    }

    private JButton buildPauseButton(int size) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int r = Math.min(getWidth(), getHeight()) / 2;
                int cx = getWidth()  / 2;
                int cy = getHeight() / 2;

                g2.setColor(new java.awt.Color(0, 0, 0, 80));
                g2.fillOval(cx - r + 2, cy - r + 3, r * 2, r * 2);

                g2.setColor(getModel().isRollover()
                        ? new java.awt.Color(180, 180, 180)
                        : new java.awt.Color(220, 220, 220));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);

                g2.setColor(new java.awt.Color(197, 153, 83));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                g2.setStroke(new BasicStroke(1f));

                g2.setColor(new java.awt.Color(60, 30, 10));
                int bw = Math.max(3, r / 4);
                int bh = Math.max(8, r - 6);
                int gap = Math.max(3, r / 5);
                int lx = cx - gap / 2 - bw;
                int rx = cx + gap / 2;
                int by = cy - bh / 2;
                g2.fillRoundRect(lx, by, bw, bh, 3, 3);
                g2.fillRoundRect(rx, by, bw, bh, 3, 3);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(size, size));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> onPauseClicked());

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
        paused = false;
        awaitingPromotion = false;

        if (promotionOverlay != null) promotionOverlay.hide();
        if (pauseOverlay != null) pauseOverlay.setVisible(false);
        if (endGameOverlay != null) {
            endGameOverlay.setVisible(false);
            endGameOverlay.setEnabled(false);
        }

        clearSelection();
        cardLayout.show(rootPanel, CARD_MENU);
        menuPanel.startAnimation();
    }

    // ── Square interaction ────────────────────────────────────────────────
    public void onSquareClicked(int row, int col) {
        if (!gameState.isOngoing() || paused || awaitingPromotion) return;

        Square sq = gameState.getBoard().getSquare(row, col);

        if (selectedSquare == null) {
            trySelect(sq);
        } else {
            Move move = findMoveToSquare(row, col);
            if (move != null) {
                clearSelection();
                executeMove(move);
            }
            else if (sq.isOccupied() && sq.getPiece().getColor() == gameState.getCurrentTurn()) {
                trySelect(sq);
            }
            else {
                clearSelection();
            }
        }
        refreshAll();
    }

    public void onDragCompleted(int fromRow, int fromCol, int toRow, int toCol) {
        if (!gameState.isOngoing() || paused || awaitingPromotion) { refreshAll(); return; }

        Square from = gameState.getBoard().getSquare(fromRow, fromCol);

        if (!from.isOccupied() || from.getPiece().getColor() != gameState.getCurrentTurn()) {
            refreshAll(); return;
        }

        selectedSquare = from;
        selectedMoves  = validator.getLegalMoves(from);
        Move move = findMoveToSquare(toRow, toCol);
        clearSelection();

        if (move != null) executeMove(move);
        refreshAll();

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
        paused = false;
        awaitingPromotion = false;

        if (pauseOverlay != null) pauseOverlay.setVisible(false);
        if (endGameOverlay != null) {
            endGameOverlay.setVisible(false);
            endGameOverlay.setEnabled(false);
        }

        newGame();
        refreshAll();
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
