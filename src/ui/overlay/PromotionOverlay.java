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

    public void show(model.Color color, Consumer<PieceType> callback) {
        this.pieceColor = color;
        this.callback = callback;

        for (int i = 0; i < 4; i++) hover[i] = press[i] = false;

        stopAll();

        alpha  = 0f;
        slideY = 0f;

        setVisible(true);
        setEnabled(true);

        animIn = new Timer(16, e -> {
            alpha  = Math.min(1f, alpha  + 0.07f);
            slideY = Math.min(1f, slideY + 0.07f);
            repaint();
            if (getParent() != null) getParent().repaint();
            if (alpha >= 1f && slideY >= 1f) ((Timer) e.getSource()).stop();
        });
        animIn.start();
    }

    public void hide() {
        stopAll();
        setEnabled(false);

        animOut = new Timer(16, e -> {
            alpha = Math.max(0f, alpha - 0.06f);
            repaint();
            if (getParent() != null) getParent().repaint();
            if (alpha <= 0f) {
                ((Timer) e.getSource()).stop();
                setVisible(false);
            }
        });
        animOut.start();
    }

    private void stopAll() {
        if (animIn != null) animIn.stop();
        if (animOut != null) animOut.stop();
    }

    private float easeOut(float t) {
        float f = 1 - t;
        return 1 - f * f * f;
    }

    //Layout
    private void recomputeLayout() {
        int pw = getWidth(), ph = getHeight();
        if (pw == 0 || ph == 0) return;

        bannerW = (int)(pw * 0.70);
        bannerW = Math.max(360, Math.min(580, bannerW));

        int titleSz = Math.max(15, bannerW / 18);
        int subtitleSz = Math.max(12, bannerW / 24);
        int btnLabelSz = Math.max(10, bannerW / 28);
        int glyphSz = Math.max(24, bannerW / 12);

        titleFont = new Font("Serif", Font.BOLD,  titleSz);
        subtitleFont = new Font("Serif", Font.BOLD,  subtitleSz);
        btnLabelFont = new Font("SansSerif", Font.BOLD,  btnLabelSz);
        glyphFont = loadGlyphFont(glyphSz);

        gridGap = Math.max(10, bannerW / 26);
        int sidePad = 30;
        btnW = Math.max(90, (bannerW - sidePad * 2 - gridGap) / 2);
        btnH = btnW;

        int paddingTop = Math.max(14, titleSz);
        int lineGap = Math.max(4,  titleSz / 4);
        int sepGap = Math.max(28, titleSz + 16);
        int paddingBot = Math.max(18, titleSz + 4);
    }



        // Mouse listeners
    private void attachMouseListeners() {

    }

    //Glyph font loader — mirrors BoardPanel
    private static Font loadGlyphFont(int size) {

    }

}
