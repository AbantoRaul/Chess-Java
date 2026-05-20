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

}
