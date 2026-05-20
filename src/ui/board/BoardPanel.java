package ui.board;


public class BoardPanel extends JPanel{
    // ── Square colours ────────────────────────────────────────────────────
    private static final Color LIGHT = new Color(240, 217, 181);
    private static final Color DARK = new Color(181, 136,  99);

    // ── Frame colours ─────────────────────────────────────────────────────
    private static final Color FRAME_DARK = new Color( 90,  45,  15);
    private static final Color FRAME_MID = new Color(130,  70,  25);
    private static final Color FRAME_GOLD = new Color(197, 153,  83);
    private static final Color COORD_FG = new Color(237, 213, 176);

    // ── Highlight colours ─────────────────────────────────────────────────
    private static final Color SEL_TINT = new Color(246, 246, 105, 180);
    private static final Color LAST_TINT = new Color(205, 209, 110, 130);
    private static final Color CHECK_TINT = new Color(229,  57,  53, 160);
    private static final Color MOVE_DOT = new Color( 61,  61,  61,  70);
    private static final Color CAPTURE_RING = new Color(198,  40,  40, 140);

    private final LayoutConfig cfg;
    private final GameController controller;

    private Font pieceFont;
    private Font coordFont;

    private int dragFromRow = -1, dragFromCol = -1;
    private int dragX = -1, dragY = -1;
    private boolean dragging = false;

    public BoardPanel(GameController controller, LayoutConfig cfg) {
        this.controller = controller;
        this.cfg = cfg;
        setPreferredSize(new Dimension(cfg.boardSide, cfg.boardSide));
        setOpaque(false);
        loadFonts();
        attachMouseListeners();
    }
    private void loadFonts() {

    }

    private int boardX() { return cfg.padding + cfg.frame; }
    private int boardY() { return cfg.padding + cfg.frame; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

    }

    private void drawWoodenFrame(Graphics2D g2) {
        int fx = cfg.padding, fy = cfg.padding;
        int fw = cfg.total,   fh = cfg.total;

    }

    private void drawSquares(Graphics2D g2) {
        Square sel = controller.getSelectedSquare();
        List<Move> moves = controller.getSelectedMoves();
    }

    private void drawPieces(Graphics2D g2) {
        int ox = boardX(), oy = boardY();

    }

    // Semi-transparent piece following the cursor while dragging
    private void drawDragGhost(Graphics2D g2) {
        Square sq = controller.getGameState().getBoard().getSquare(dragFromRow, dragFromCol);

    }

    private void drawPieceAt(Graphics2D g2, Piece piece, int px, int py, int size) {

    }

    // ── Mouse listeners ───────────────────────────────────────────────────
    private void attachMouseListeners() {
        addMouseListener(new MouseAdapter() {

        });

        addMouseMotionListener(new MouseMotionAdapter() {

        });
    }

    // Converts pixel coordinates to board row/col, returns null if outside board
    private int[] pixelToSquare(int px, int py) {
        int ox = boardX(), oy = boardY();

        if (px < ox || py < oy || px >= ox + cfg.boardPx || py >= oy + cfg.boardPx) return null;

        int col = (px - ox) / cfg.sq;
        int row = 7 - (py - oy) / cfg.sq;

        if (col < 0 || col > 7 || row < 0 || row > 7) return null;
        return new int[]{row, col};
    }
    
    private static String getGlyph(Piece p) {
        boolean w = p.getColor() == model.Color.WHITE;
        return switch (p.getType()) {
            case KING   -> w ? "\u2654" : "\u265A";
            case QUEEN  -> w ? "\u2655" : "\u265B";
            case ROOK   -> w ? "\u2656" : "\u265C";
            case BISHOP -> w ? "\u2657" : "\u265D";
            case KNIGHT -> w ? "\u2658" : "\u265E";
            case PAWN   -> w ? "\u2659" : "\u265F";
        };
    }
}
