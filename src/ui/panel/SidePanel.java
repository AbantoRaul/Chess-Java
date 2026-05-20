package ui.panel;

public class SidePanel extends JPanel {

    // Theme colours
    private static final Color BG_WOOD = new Color( 95,  55,  20);
    private static final Color BG_WOOD_MID = new Color(115,  68,  25);
    private static final Color BG_WOOD_DARK = new Color( 75,  40,  12);
    private static final Color BG_HISTORY = new Color( 20,  12,   4);
    private static final Color GOLD = new Color(197, 153,  83);
    private static final Color GOLD_LIGHT = new Color(230, 190, 120);
    private static final Color FG_WHMOVE = new Color(240, 235, 220);
    private static final Color FG_BKMOVE = new Color(190, 175, 150);
    private static final Color FG_NUM = new Color(140, 110,  70);
    private static final Color LAST_ROW_BG = new Color( 50,  30,  10);
    private static final Color BG_RESIGN = new Color(110,  22,  22);
    private static final Color BG_RES_HOV = new Color(150,  30,  30);
    private static final Color SEPARATOR = new Color(140, 100,  50);

    // Piece values
    private static final Map<PieceType, Integer> VALUE = new EnumMap<>(PieceType.class);
    static {
        VALUE.put(PieceType.QUEEN, 9); VALUE.put(PieceType.ROOK, 5);
        VALUE.put(PieceType.BISHOP, 3); VALUE.put(PieceType.KNIGHT, 3);
        VALUE.put(PieceType.PAWN, 1); VALUE.put(PieceType.KING, 0);
    }

    // Chess Unicode glyphs
    private static final Map<PieceType, String> WG  = new EnumMap<>(PieceType.class);
    private static final Map<PieceType, String> BLK = new EnumMap<>(PieceType.class);
    static {
        WG.put(PieceType.QUEEN, "\u2655"); WG.put(PieceType.ROOK, "\u2656");
        WG.put(PieceType.BISHOP, "\u2657"); WG.put(PieceType.KNIGHT, "\u2658");
        WG.put(PieceType.PAWN, "\u2659"); WG.put(PieceType.KING, "\u2654");
        BLK.put(PieceType.QUEEN, "\u265B"); BLK.put(PieceType.ROOK, "\u265C");
        BLK.put(PieceType.BISHOP,"\u265D"); BLK.put(PieceType.KNIGHT,"\u265E");
        BLK.put(PieceType.PAWN, "\u265F"); BLK.put(PieceType.KING, "\u265A");
    }

    // Config
    private final LayoutConfig cfg;
    private final GameController controller;

    // Derived sizes
    private final int NAME_SZ;
    private final int GLYPH_SZ;
    private final int PAD_H;
    private final int PAD_V;
    private final int CARD_H;
    private final int HEADER_H;
    private final int RESIGN_H;
    private final int ROW_H;

    // Fonts
    private final Font nameFont;
    private final Font scoreFont;
    private final Font capturedFont;
    private final Font sectionFont;
    private final Font moveFont;
    private final Font moveFontBold;
    private final Font moveNumFont;
    private final Font btnFont;

    // Components
    private final PlayerCard blackCard;
    private final PlayerCard whiteCard;
    private final JPanel historyRows;
    private final JScrollPane historyScroll;

    public SidePanel(GameController controller, LayoutConfig cfg) {
        this.controller = controller;
        this.cfg = cfg;
        int sq = cfg.sq;

        // All sizes derived from sq with clamp
        NAME_SZ = clamp(sq / 5, 10, 15);
        GLYPH_SZ = clamp(sq / 6, 9, 13);
        PAD_H = clamp(sq / 8, 6, 12);
        PAD_V  = clamp(sq / 10, 4, 8);
        CARD_H = PAD_V + NAME_SZ + PAD_V
                + 1
                + PAD_V + GLYPH_SZ
                + 2 + GLYPH_SZ + PAD_V;

        HEADER_H = clamp(sq / 4,  18, 26);
        RESIGN_H = clamp(sq / 2,  30, 44) + 10;
        ROW_H    = clamp(sq / 4,  15, 20);

        // Fonts
        nameFont = new Font("Serif", Font.BOLD,  NAME_SZ);
        scoreFont = new Font("SansSerif", Font.BOLD, clamp(sq/6, 8, 12));
        capturedFont = loadPieceFont(GLYPH_SZ);
        sectionFont = new Font("Serif", Font.BOLD, clamp(sq/7, 8, 11));
        moveFont = new Font(Font.MONOSPACED, Font.PLAIN, clamp(sq/6, 9, 12));
        moveFontBold = new Font(Font.MONOSPACED, Font.BOLD, clamp(sq/6, 9, 12));
        moveNumFont = new Font(Font.MONOSPACED, Font.PLAIN, clamp(sq/7, 8, 10));
        btnFont = new Font("SansSerif", Font.BOLD, clamp(sq/5, 10, 13));

        // Player cards
        blackCard = new PlayerCard("BLACK");
        whiteCard = new PlayerCard("WHITE");

        // History
        historyRows = new JPanel();
        historyRows.setLayout(new BoxLayout(historyRows, BoxLayout.Y_AXIS));
        historyRows.setBackground(BG_HISTORY);
        historyRows.setBorder(new EmptyBorder(2, 2, 2, 2));

        historyScroll = new JScrollPane(historyRows);
        historyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        historyScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        historyScroll.setBorder(null);
        historyScroll.getViewport().setBackground(BG_HISTORY);
        styleScrollBar(historyScroll);

        // Panel config
        setPreferredSize(new Dimension(cfg.sideW, cfg.boardSide));
        setBackground(BG_WOOD);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, GOLD));

        JPanel northStack = new JPanel(new BorderLayout());
        northStack.setOpaque(false);
        northStack.add(blackCard, BorderLayout.CENTER);
        northStack.add(buildSectionHeader(), BorderLayout.SOUTH);

        JPanel southStack = new JPanel(new BorderLayout());
        southStack.setOpaque(false);
        southStack.add(whiteCard, BorderLayout.CENTER);
        southStack.add(buildResignBar(), BorderLayout.SOUTH);

        add(northStack, BorderLayout.NORTH);
        add(historyScroll, BorderLayout.CENTER);
        add(southStack, BorderLayout.SOUTH);
    }

    // UPDATE
    public void update() {
        GameState gs = controller.getGameState();

        List<Piece> capByWhite = new ArrayList<>();
        List<Piece> capByBlack = new ArrayList<>();

        for (Move m : gs.getMoveHistory()) {
            if (m.getCapturedPiece() == null) continue;

            Piece cp = m.getCapturedPiece();

            if (cp.getColor() == model.Color.BLACK) capByWhite.add(cp);
            else capByBlack.add(cp);
        }

        Comparator<Piece> byVal =
                (a, b) -> VALUE.get(b.getType()) - VALUE.get(a.getType());
        capByWhite.sort(byVal);
        capByBlack.sort(byVal);

        int scoreW = capByWhite.stream().mapToInt(p -> VALUE.get(p.getType())).sum();
        int scoreB = capByBlack.stream().mapToInt(p -> VALUE.get(p.getType())).sum();
        int adv = scoreW - scoreB;

        blackCard.update(capByWhite, false, adv < 0 ? "+" + (-adv) : "");
        whiteCard.update(capByBlack, true, adv > 0 ? "+" + adv : "");

        extracted(gs);
    }

    private void extracted(GameState gs) {
        updateHistory(gs);
        revalidate();
        repaint();
    }

    // PLAYER CARD — inner class
    private class PlayerCard extends JPanel {
        private final String label;
        private String score = "";
        private String line1 = "";
        private String line2 = "";

        PlayerCard(String label) {
            this.label = label;

            setPreferredSize(new Dimension(0, CARD_H));
            setMaximumSize (new Dimension(Short.MAX_VALUE, CARD_H));
            setMinimumSize (new Dimension(0, CARD_H));
            setOpaque(false);
        }

        void update(List<Piece> pieces, boolean useWhiteGlyphs, String scoreStr) {
            this.score = scoreStr;
            line1 = "";
            line2 = "";
            if (pieces.isEmpty()) { repaint(); return; }

            StringBuilder sb = new StringBuilder();
            for (Piece p : pieces)
                sb.append(useWhiteGlyphs
                        ? WG.getOrDefault(p.getType(),  "?")
                        : BLK.getOrDefault(p.getType(), "?"));
            String all = sb.toString();

            int availW = getWidth() > 0 ? getWidth() - PAD_H * 2
                    : cfg.sideW - PAD_H * 2;
            int glyphW = (int)(GLYPH_SZ * 0.90);
            int perLine = Math.max(1, availW / glyphW);
            int total = all.length();

            if (total <= perLine) {
                line1 = all;
            } else {
                line1 = all.substring(0, Math.min(perLine, total));
                String rest = all.substring(Math.min(perLine, total));
                if (rest.length() <= perLine) {
                    line2 = rest;
                } else {
                    int shown = perLine - 2;
                    int overflow = rest.length() - shown;
                    line2 = rest.substring(0, Math.max(0, shown)) + "+" + overflow;
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, BG_WOOD_MID, 0, h, BG_WOOD_DARK);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            g2.setColor(GOLD);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(0, h - 1, w, h - 1);

            // Name row
            int nameBaseline = PAD_V + NAME_SZ;
            g2.setFont(nameFont);
            g2.setColor(GOLD_LIGHT);
            g2.drawString(label, PAD_H, nameBaseline);
            // Score to the right of name
            if (!score.isEmpty()) {
                int nameW = g2.getFontMetrics(nameFont).stringWidth(label);
                g2.setFont(scoreFont);
                g2.setColor(new Color(255, 215, 0));
                g2.drawString(score, PAD_H + nameW + 5, nameBaseline);
            }

            // Subtle separator under name
            int sepY = nameBaseline + PAD_V;
            g2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 50));
            g2.drawLine(PAD_H, sepY, w - PAD_H, sepY);

            // Captured pieces
            g2.setFont(capturedFont);
            g2.setColor(new Color(210, 180, 130));

            int gy1 = sepY + PAD_V + GLYPH_SZ;
            if (!line1.isEmpty())
                g2.drawString(line1, PAD_H, gy1);

            int gy2 = gy1 + GLYPH_SZ + 2;
            if (!line2.isEmpty() && gy2 < h - 2)
                g2.drawString(line2, PAD_H, gy2);

        }

        }

    }

    // HISTORY
    private void updateHistory(GameState gs) {
        historyRows.removeAll();
        List<Move> history = gs.getMoveHistory();

        for (int i = 0; i < history.size(); i += 2) {
            int num = i / 2 + 1;
            String white = history.get(i).toAlgebraic();
            String black = (i + 1 < history.size())
                    ? history.get(i + 1).toAlgebraic() : "";
            boolean isLast = (i >= history.size() - 2);
            JPanel row = buildHistoryRow(num, white, black, isLast);

            row.setAlignmentX(LEFT_ALIGNMENT);
            historyRows.add(row);
        }

        historyRows.revalidate();
        historyRows.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vsb = historyScroll.getVerticalScrollBar();
            vsb.setValue(vsb.getMaximum());
        });

    }

    private JPanel buildHistoryRow(int num, String white,
                                   String black, boolean isLast) {

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        row.setOpaque(true);
        row.setBackground(isLast ? LAST_ROW_BG : BG_HISTORY);
        row.setPreferredSize(new Dimension(0, ROW_H));
        row.setMaximumSize (new Dimension(Short.MAX_VALUE, ROW_H));

        int numW  = clamp(cfg.sideW / 7, 20,28);
        int moveW = clamp(cfg.sideW / 4, 36,55);

        row.add(makeCell(String.format("%2d.", num), FG_NUM, moveNumFont, numW));
        row.add(makeCell(white, FG_WHMOVE, moveFontBold, moveW));
        row.add(makeCell(black, FG_BKMOVE, moveFont, moveW));
        return row;
    }

    private JLabel makeCell(String text, Color fg, Font font, int width) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(fg);
        l.setPreferredSize(new Dimension(width, ROW_H - 2));
        return l;
    }

    // SECTION HEADER
    private JComponent buildSectionHeader() {
        JLabel lbl = new JLabel("MOVE HISTORY", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setColor(BG_HISTORY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(SEPARATOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.drawLine(0, getHeight() - 1, getWidth(),getHeight() - 1);
                super.paintComponent(g);

            }
        };

        lbl.setFont(sectionFont);
        lbl.setForeground(GOLD);
        lbl.setOpaque(false);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setPreferredSize(new Dimension(0, HEADER_H));
        lbl.setMaximumSize (new Dimension(Short.MAX_VALUE, HEADER_H));
        lbl.setMinimumSize (new Dimension(0, HEADER_H));
        return lbl;
    }

    // RESIGN BAR
    private JPanel buildResignBar() {
        JPanel bar = new JPanel(new GridLayout(1, 1));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(5, 8, 5, 8));
        bar.setPreferredSize(new Dimension(0, RESIGN_H));
        bar.setMaximumSize (new Dimension(Short.MAX_VALUE, RESIGN_H));
        bar.setMinimumSize (new Dimension(0, RESIGN_H));

        JButton btn = makeWoodButton("Resign",
                BG_RESIGN, BG_RES_HOV, new Color(245, 198, 198));
        btn.addActionListener(e -> controller.onResign());
        bar.add(btn);
        return bar;
    }

    private JButton makeWoodButton(String text, Color bg, Color hov, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color top = getModel().isPressed() ? bg.darker()
                        : getModel().isRollover() ? hov : bg;

                GradientPaint gp = new GradientPaint(
                        0, 0, top.brighter(), 0, getHeight(), top.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(btnFont);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    // UTILITIES
    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static Font loadPieceFont(int size) {

        String[] candidates = {
                "Segoe UI Emoji", "Apple Color Emoji", "Noto Emoji",
                "DejaVu Sans", "Arial Unicode MS", "SansSerif"
        };

    }

}
