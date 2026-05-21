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

        bannerH = paddingTop
                + titleSz
                + lineGap
                + subtitleSz
                + sepGap
                + btnH
                + gridGap
                + btnH
                + paddingBot;
        bannerH = Math.max(280, bannerH);

        bannerX = (pw - bannerW) / 2;
        int finalY = (int)(ph * 0.15);
        int startY = -bannerH - 50;
        bannerY = (int)(startY + (finalY - startY) * easeOut(slideY));

        // Text positions
        int cursor = bannerY + paddingTop;
        titleTextY = cursor + titleSz;
        cursor = titleTextY + lineGap;
        subtitleTextY = cursor + subtitleSz;
        cursor = subtitleTextY + sepGap;

        int totalGridW = btnW * 2 + gridGap;
        int gridLeft = bannerX + (bannerW - totalGridW) / 2;
        col0X = gridLeft + btnW / 2;
        col1X = gridLeft + btnW + gridGap + btnW / 2;
        row0Y = cursor + btnH / 2;
        row1Y = cursor + btnH + gridGap + btnH / 2;
    }

    // Button rect for hit detection (index 0-3)
    private Rectangle btnRect(int idx) {
        int cx = (idx == 0 || idx == 2) ? col0X : col1X;
        int cy = (idx == 0 || idx == 1) ? row0Y : row1Y;
        return new Rectangle(cx - btnW / 2, cy - btnH / 2, btnW, btnH);
    }

    private int hitBtn(Point p) {
        for (int i = 0; i < 4; i++) if (btnRect(i).contains(p)) return i;
        return -1;
    }

    // Paint
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (alpha <= 0f) return;

        recomputeLayout();
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Composite orig = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        drawBanner(g2);
        g2.setComposite(orig);
    }

    // Draw banner
    private void drawBanner(Graphics2D g2) {
        int cx = getWidth() / 2;

        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(bannerX + 5, bannerY + 7, bannerW, bannerH, 14, 14);

        // Red body
        GradientPaint body = new GradientPaint(
                bannerX, bannerY, BANNER_RED,
                bannerX, bannerY + bannerH,  BANNER_RED_DARK);
        g2.setPaint(body);
        g2.fillRoundRect(bannerX, bannerY, bannerW, bannerH, 14, 14);

        // Gold inner border
        g2.setColor(GOLD);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bannerX + 4, bannerY + 4, bannerW - 8, bannerH - 8, 10, 10);
        g2.setStroke(new BasicStroke(1f));

        g2.setColor(new Color(0, 0, 0, 40));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(bannerX, bannerY, bannerW, bannerH, 14, 14);
        g2.setStroke(new BasicStroke(1f));

        // Title
        FontRenderContext frc = g2.getFontRenderContext();
        g2.setFont(titleFont);
        Rectangle2D tb = titleFont.getStringBounds("Pawn Promotion", frc);
        int tx = (int)(cx - tb.getWidth() / 2);
        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawString("Pawn Promotion", tx + 1, titleTextY + 1);
        g2.setColor(GOLD_LIGHT);
        g2.drawString("Pawn Promotion", tx, titleTextY);

        // Subtitle
        g2.setFont(subtitleFont);
        String sub = "Choose a piece to promote to:";
        Rectangle2D sb = subtitleFont.getStringBounds(sub, frc);
        int sx = (int)(cx - sb.getWidth() / 2);
        g2.setColor(new Color(0, 0, 0, 70));
        g2.drawString(sub, sx + 1, subtitleTextY + 1);
        g2.setColor(BANNER_CREAM);
        g2.drawString(sub, sx, subtitleTextY);

        // Thin gold separator
        int sepY = subtitleTextY + Math.max(6, btnH / 6);
        g2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 120));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(bannerX + 20, sepY, bannerX + bannerW - 20, sepY);

        // 2×2 grid of piece buttons
        for (int i = 0; i < 4; i++) {
            Rectangle r = btnRect(i);
            drawPieceButton(g2,
                    r.x + r.width  / 2,
                    r.y + r.height / 2,
                    r.width, r.height,
                    i, hover[i], press[i]);
        }
    }

    // Piece button: wood button with chess glyph above label
    private void drawPieceButton(Graphics2D g2, int cx, int cy,
                                 int w, int h,
                                 int idx, boolean hov, boolean prs) {
        int x = cx - w / 2;
        int y = cy - h / 2;
        int arc = Math.max(10, h / 5);

        // Shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(x + 3, y + 5, w, h, arc, arc);

        // Wood body
        Color c1 = prs ? WOOD_DARK : hov ? WOOD_LIGHT : WOOD_MID;
        Color c2 = prs ? WOOD_MID : hov ? WOOD_MID : WOOD_DARK;
        GradientPaint gp = new GradientPaint(x, y, c1, x, y + h, c2);
        g2.setPaint(gp);
        g2.fillRoundRect(x, y, w, h, arc, arc);
    }



        // Mouse listeners
    private void attachMouseListeners() {

    }

    //Glyph font loader — mirrors BoardPanel
    private static Font loadGlyphFont(int size) {

    }

}
