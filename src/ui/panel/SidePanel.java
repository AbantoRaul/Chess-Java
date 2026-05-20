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

    }

}
