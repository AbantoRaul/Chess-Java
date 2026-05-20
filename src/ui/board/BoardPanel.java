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
        String[] candidates = {
                "Segoe UI Emoji", "Apple Color Emoji", "Noto Emoji",
                "DejaVu Sans", "Arial Unicode MS", "SansSerif"
        };

        pieceFont = null;
        for (String name : candidates) {
            Font f = new Font(name, Font.PLAIN, cfg.pieceFontSz);
            if (f.canDisplay('\u2654')) { pieceFont = f; break; }
        }

        if (pieceFont == null) pieceFont = new Font("SansSerif", Font.BOLD, cfg.pieceFontSz);
        coordFont = new Font("Serif", Font.BOLD, Math.max(10, cfg.sq / 5));
    }

    private int boardX() { return cfg.padding + cfg.frame; }
    private int boardY() { return cfg.padding + cfg.frame; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawWoodenFrame(g2);
        drawSquares(g2);
        drawPieces(g2);
        if (dragging && dragFromRow >= 0) drawDragGhost(g2);
    }

    private void drawWoodenFrame(Graphics2D g2) {
        int fx = cfg.padding, fy = cfg.padding;
        int fw = cfg.total,   fh = cfg.total;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(fx + 6, fy + 8, fw, fh, 12, 12);

        // Wood body
        GradientPaint gp = new GradientPaint(fx, fy, FRAME_MID, fx + fw, fy + fh, FRAME_DARK);
        g2.setPaint(gp);
        g2.fillRoundRect(fx, fy, fw, fh, 10, 10);

        // Gold border
        g2.setColor(FRAME_GOLD);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(fx + 4, fy + 4, fw - 8, fh - 8, 8, 8);

        // Inner board edge
        g2.setColor(FRAME_DARK);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(boardX() - 2, boardY() - 2, cfg.boardPx + 4, cfg.boardPx + 4);
        g2.setStroke(new BasicStroke(1f));

        // Rank and file coordinates
        g2.setFont(coordFont);
        g2.setColor(COORD_FG);
        FontRenderContext frc = g2.getFontRenderContext();

        for (int i = 0; i < 8; i++) {
            String rank = String.valueOf(i + 1);
            Rectangle2D rb = coordFont.getStringBounds(rank, frc);
            int ry = boardY() + (7 - i) * cfg.sq + cfg.sq / 2 + (int)(rb.getHeight() / 2) - 2;
            int lx = cfg.padding + (cfg.frame - (int)rb.getWidth()) / 2;
            int rx = boardX() + cfg.boardPx + (cfg.frame - (int)rb.getWidth()) / 2;
            g2.drawString(rank, lx, ry);
            g2.drawString(rank, rx, ry);

            String file = String.valueOf((char)('A' + i));
            Rectangle2D fb = coordFont.getStringBounds(file, frc);

            int fcx = boardX() + i * cfg.sq + cfg.sq / 2 - (int)(fb.getWidth() / 2);
            int topY = cfg.padding + (cfg.frame - (int)fb.getHeight()) / 2 + (int)fb.getHeight() - 2;
            int botY = boardY() + cfg.boardPx + (cfg.frame + (int)fb.getHeight()) / 2 - 2;

            g2.drawString(file, fcx, topY);
            g2.drawString(file, fcx, botY);
        }
    }

    private void drawSquares(Graphics2D g2) {
        Square sel = controller.getSelectedSquare();
        List<Move> moves = controller.getSelectedMoves();

        // Collect move and capture targets for highlighting
        Set<String> moveSqs = new HashSet<>();
        Set<String> captureSqs = new HashSet<>();

        if (moves != null) {
            for (Move m : moves) {
                String key = m.getTo().getRow() + "," + m.getTo().getCol();
                if (m.isCapture()) captureSqs.add(key);
                else moveSqs.add(key);
            }
        }

        // Find king in check for red highlight
        int checkRow = -1, checkCol = -1;

        GameStatus status = controller.getGameState().getStatus();

        if (status == GameStatus.CHECK || status == GameStatus.CHECKMATE) {
            Square king = controller.getGameState().getBoard()
                    .findKing(controller.getGameState().getCurrentTurn());
            if (king != null) { checkRow = king.getRow(); checkCol = king.getCol(); }
        }

        int ox = boardX(), oy = boardY();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int px = ox + col * cfg.sq;
                int py = oy + (7 - row) * cfg.sq;
                String key = row + "," + col;

                // [feat(ui/board): compute boolean flags for each square state]
                boolean isSelected = sel != null && sel.getRow() == row && sel.getCol() == col;
                boolean isLastFrom = row == controller.getLastFromRow() && col == controller.getLastFromCol();
                boolean isLastTo = row == controller.getLastToRow() && col == controller.getLastToCol();
                boolean isCheck = row == checkRow && col == checkCol;
                boolean isMoveTarget = moveSqs.contains(key);
                boolean isCaptureTarget = captureSqs.contains(key);

                // [feat(ui/board): paint base square color alternating light and dark]
                // Base square colour
                g2.setColor((row + col) % 2 == 0 ? LIGHT : DARK);
                g2.fillRect(px, py, cfg.sq, cfg.sq);

                // [feat(ui/board): apply check selection and last move tint overlays]
                // Overlay tints
                if (isCheck) { g2.setColor(CHECK_TINT); g2.fillRect(px, py, cfg.sq, cfg.sq); }
                else if (isSelected) { g2.setColor(SEL_TINT); g2.fillRect(px, py, cfg.sq, cfg.sq); }
                else if (isLastFrom || isLastTo) { g2.setColor(LAST_TINT); g2.fillRect(px, py, cfg.sq, cfg.sq); }

                // [feat(ui/board): determine square occupancy accounting for drag state]
                Square sq = controller.getGameState().getBoard().getSquare(row, col);
                boolean occupied = sq.isOccupied() && !(dragging && row == dragFromRow && col == dragFromCol);

                // [feat(ui/board): draw move dot on empty legal target squares]
                // Move dot for empty target squares
                if (isMoveTarget && !occupied) {
                    g2.setColor(MOVE_DOT);
                    int r = cfg.sq / 5, cx2 = px + cfg.sq / 2, cy2 = py + cfg.sq / 2;
                    g2.fillOval(cx2 - r, cy2 - r, r * 2, r * 2);
                }

                // [feat(ui/board): draw capture ring on occupied legal target squares]
                // Capture ring for occupied target squares
                if (isCaptureTarget && occupied) {
                    g2.setColor(CAPTURE_RING);
                    g2.setStroke(new BasicStroke(Math.max(3, cfg.sq / 15f)));
                    g2.drawOval(px + 5, py + 5, cfg.sq - 10, cfg.sq - 10);
                    g2.setStroke(new BasicStroke(1));
                }
            }
        }
    }

    private void drawPieces(Graphics2D g2) {
        int ox = boardX(), oy = boardY();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                // [feat(ui/board): skip dragged piece square to avoid double render]
                if (dragging && row == dragFromRow && col == dragFromCol) continue;

                Square sq = controller.getGameState().getBoard().getSquare(row, col);

                // [feat(ui/board): skip empty squares]
                if (!sq.isOccupied()) continue;

                // [feat(ui/board): draw piece glyph at computed pixel position]
                drawPieceAt(g2, sq.getPiece(), ox + col * cfg.sq, oy + (7 - row) * cfg.sq, cfg.sq);
            }
        }
    }

    // Semi-transparent piece following the cursor while dragging
    private void drawDragGhost(Graphics2D g2) {
        Square sq = controller.getGameState().getBoard().getSquare(dragFromRow, dragFromCol);

        if (!sq.isOccupied()) return;

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
