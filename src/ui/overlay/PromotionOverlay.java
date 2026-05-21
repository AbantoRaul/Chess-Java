package ui.overlay;

import model.PieceType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.function.Consumer;

public class PromotionOverlay extends JPanel {
    private static final Color BANNER_RED = new Color(139,  28,  28);
    private static final Color BANNER_RED_DARK = new Color( 90,  15,  15);
    private static final Color BANNER_CREAM = new Color(237, 213, 176);
    private static final Color GOLD = new Color(197, 153,  83);
    private static final Color GOLD_LIGHT = new Color(230, 190, 120);
    private static final Color WOOD_DARK = new Color( 95,  55,  20);
    private static final Color WOOD_MID = new Color(130,  80,  30);
    private static final Color WOOD_LIGHT = new Color(170, 110,  50);

    private static final PieceType[] TYPES  =
            {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
    private static final String[] LABELS    =
            {"Queen", "Rook", "Bishop", "Knight"};
    private static final String[] WHITE_GLYPHS =
            {"\u2655", "\u2656", "\u2657", "\u2658"};
    private static final String[] BLACK_GLYPHS =
            {"\u265B", "\u265C", "\u265D", "\u265E"};

    private Consumer<PieceType> callback;
    private model.Color pieceColor;

    private final boolean[] hover = new boolean[4];
    private final boolean[] press = new boolean[4];

    private float alpha  = 0f;
    private float slideY = 0f;
    private Timer animIn;
    private Timer animOut;

    // Layout — recomputed every paint
    private int bannerW, bannerH, bannerX, bannerY;
    private int titleTextY, subtitleTextY;
    private int btnW, btnH, gridGap;
    private int col0X, col1X;
    private int row0Y, row1Y;

    private Font titleFont;
    private Font subtitleFont;
    private Font btnLabelFont;
    private Font glyphFont;

    public PromotionOverlay() {
        setOpaque(false);
        setLayout(null);
        attachMouseListeners();
    }

    // Mouse listeners
    private void attachMouseListeners() {

    }

}
