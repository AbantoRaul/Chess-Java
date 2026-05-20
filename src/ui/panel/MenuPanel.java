package ui.panel;

public class MenuPanel extends JPanel {

    // Chessboard background
    private static final Color SQ_LIGHT = new Color(220, 195, 155);
    private static final Color SQ_DARK = new Color(165, 115,  75);

    // Card palette 
    private static final Color BANNER_RED = new Color(150,  25,  25);
    private static final Color BANNER_RED_DARK = new Color( 95,  12,  12);
    private static final Color BANNER_CREAM = new Color(237, 213, 176);
    private static final Color BANNER_CREAM_DK = new Color(205, 175, 128);
    private static final Color GOLD = new Color(197, 153,  83);
    private static final Color GOLD_LIGHT = new Color(230, 190, 120);
    private static final Color WOOD_DARK = new Color( 95,  55,  20);
    private static final Color WOOD_MID = new Color(130,  80,  30);
    private static final Color WOOD_LIGHT = new Color(175, 115,  55);

    private static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 40);
    private static final Font BTN_FONT = new Font("SansSerif", Font.BOLD, 15);

    private final Runnable onPlay;
    private boolean btnHover = false;
    private boolean btnPress = false;

    // knight float animation
    private Timer animTimer;
    private float knightOffset = 0f;
    private float knightDir = 1f;
}
