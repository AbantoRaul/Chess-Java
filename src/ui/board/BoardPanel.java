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
}
