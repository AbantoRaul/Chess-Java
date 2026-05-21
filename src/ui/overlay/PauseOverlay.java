package ui.overlay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class PauseOverlay extends JPanel {
    private static final Color BANNER_RED = new Color(139,  28,  28);
    private static final Color BANNER_RED_DARK = new Color( 90,  15,  15);
    private static final Color BANNER_CREAM = new Color(237, 213, 176);
    private static final Color GOLD = new Color(197, 153,  83);
    private static final Color GOLD_LIGHT = new Color(230, 190, 120);
    private static final Color WOOD_DARK = new Color( 95,  55,  20);
    private static final Color WOOD_MID = new Color(130,  80,  30);
    private static final Color WOOD_LIGHT = new Color(170, 110,  50);

    private final Runnable onResume;
    private final Runnable onRestart;
    private final Runnable onExit;

    private final boolean[] hover = new boolean[3];
    private final boolean[] press = new boolean[3];

    private float scrollAnim = 0f;
    private Timer animTimer;

    // Layout values. Recomputed every paint from actual panel size.
    // Card dimensions
    private int cardW, cardH, cardX, cardY;
    // Scroll tube/knob dimensions
    private int tubeH;
    private int knobR;
    private int knobExtra;
    // Content positions inside the card
    private int titleY;
    private int ornamentY;
    private int zigzagY;
    private int btn0Y, btn1Y, btn2Y;
    private int btnW, btnH;
    private Font titleFont;
    private Font btnFont;

    public PauseOverlay(Runnable onResume, Runnable onRestart, Runnable onExit) {
        this.onResume = onResume;
        this.onRestart = onRestart;
        this.onExit = onExit;
        setOpaque(false);
        setLayout(null);
        attachMouseListeners();
    }

    // LAYOUT — called every paintComponent so it always matches panel size
    private void recomputeLayout() {
        int pw = getWidth(), ph = getHeight();
        if (pw == 0 || ph == 0) return;

        cardW = (int)(pw * 0.52);

        // Tube and knob sizes
        tubeH = Math.max(14, ph / 30);
        knobR = Math.max(12, tubeH / 2 + 6);
        knobExtra = knobR + tubeH / 2 + 4;

        int safeMargin = 16;
        cardH = (int)(ph * 0.75) - knobExtra * 2 - safeMargin;
        cardH = Math.max(200, cardH);

        // Card is centred in the panel
        cardX = (pw - cardW) / 2;
        cardY = (ph - cardH) / 2;

        // Fonts
        int titleSz = Math.max(16, cardW / 8);
        int btnSz = Math.max(10, cardW / 14);
        titleFont = new Font("Serif", Font.BOLD, titleSz);
        btnFont = new Font("SansSerif", Font.BOLD, btnSz);

        // Content positions
        int afterTopTube = cardY + tubeH + knobExtra / 2;
        titleY = afterTopTube + (int)(cardH * 0.10) + titleSz;
        ornamentY = titleY + (int)(cardH * 0.07);
        zigzagY = ornamentY + (int)(cardH * 0.05);

        // Button area: from below zigzag to above bottom scroll tube
        int beforeBotTube = cardY + cardH - tubeH - knobExtra / 2 - 8;
        btnW = (int)(cardW * 0.76);
        btnH = Math.max(30, cardH / 9);
        int btnAreaStart = zigzagY + (int)(cardH * 0.05);
        int btnAreaEnd = beforeBotTube;
        int totalBtnH = btnH * 3;
        int gap = Math.max(6, (btnAreaEnd - btnAreaStart - totalBtnH) / 2);
        btn0Y = btnAreaStart;
        btn1Y = btn0Y + btnH + gap;
        btn2Y = btn1Y + btnH + gap;
    }

    // BUTTON RECTS — used for hit detection
    private Rectangle btnRect(int idx) {
        int cx = getWidth() / 2;
        int y = idx == 0 ? btn0Y : idx == 1 ? btn1Y : btn2Y;
        return new Rectangle(cx - btnW / 2, y, btnW, btnH);
    }

    private int hitBtn(Point p) {
        for (int i = 0; i < 3; i++) if (btnRect(i).contains(p)) return i;
        return -1;
    }

    // ANIMATION
    public void playOpenAnimation() {
        scrollAnim = 0f;

        if (animTimer != null) animTimer.stop();
        animTimer = new Timer(16, e -> {
            scrollAnim += 0.055f;
            if (scrollAnim >= 1f) {
                scrollAnim = 1f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        animTimer.start();
    }

    private float ease(float t) {
        float f = 1 - t;
        return 1 - f * f * f;
    }

    // PAINT
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        recomputeLayout();
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dim background
        g2.setColor(new Color(0, 0, 0, 155));
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (scrollAnim <= 0f) return;

        int clipX = cardX - knobR - 6;
        int clipY = cardY - knobExtra;
        int clipW = cardW + (knobR + 6) * 2;
        int totalH = cardH + knobExtra * 2;
        int visH = (int)(totalH * ease(scrollAnim));

        Shape old = g2.getClip();
        g2.setClip(clipX, clipY, clipW, Math.min(visH, totalH));
        drawCard(g2);
        g2.setClip(old);
    }

    // DRAW CARD
    private void drawCard(Graphics2D g2) {
        int cx = getWidth() / 2;

        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(cardX + 7, cardY + 10, cardW, cardH, 16, 16);

        // Red body
        GradientPaint body = new GradientPaint(
                cardX, cardY, BANNER_RED,
                cardX, cardY + cardH, BANNER_RED_DARK);
        g2.setPaint(body);
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 16, 16);

        // Inner gold border
        g2.setColor(GOLD);
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawRoundRect(cardX + 5, cardY + 5, cardW - 10, cardH - 10, 12, 12);
        g2.setStroke(new BasicStroke(1f));

        // TOP scroll tube
        drawScrollEnd(g2, cx, cardY, cardW);
        // BOTTOM scroll tube
        drawScrollEnd(g2, cx, cardY + cardH, cardW);

        // PAUSED title
        g2.setFont(titleFont);
        FontRenderContext frc = g2.getFontRenderContext();
        String title = "PAUSED";
        Rectangle2D tb = titleFont.getStringBounds(title, frc);
        int tx = (int)(cx - tb.getWidth() / 2);
        g2.setColor(new Color(0, 0, 0, 55));
        g2.drawString(title, tx + 2, titleY + 2);
        g2.setColor(GOLD_LIGHT);
        g2.drawString(title, tx, titleY);

        // Ornament
        drawOrnament(g2, cx, ornamentY);

        // Zigzag
        int zigW = (int)(cardW * 0.78);
        drawZigzag(g2, cx - zigW / 2, zigzagY, zigW);

        // Buttons
        String[] labels = {"RESUME", "RESTART", "EXIT"};
        for (int i = 0; i < 3; i++) {
            Rectangle r = btnRect(i);
            drawWoodButton(g2,
                    r.x + r.width / 2, r.y + r.height / 2,
                    r.width, r.height,
                    labels[i], hover[i], press[i]);
        }
    }

    // SCROLL END (top or bottom tube with knobs)
    private void drawScrollEnd(Graphics2D g2, int cx, int cy, int w) {
        int halfW = w / 2;
        int yTop = cy - tubeH / 2;

        // Tube body
        GradientPaint gp = new GradientPaint(
                cx - halfW, yTop, WOOD_LIGHT,
                cx - halfW, yTop + tubeH, WOOD_DARK);
        g2.setPaint(gp);
        g2.fillRoundRect(cx - halfW, yTop, w, tubeH, tubeH, tubeH);

        // Highlight
        g2.setColor(new Color(230, 190, 120, 150));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawLine(cx - halfW + 8, yTop + 4, cx + halfW - 8, yTop + 4);

        // Gold border
        g2.setColor(GOLD);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(cx - halfW, yTop, w, tubeH, tubeH, tubeH);
        g2.setStroke(new BasicStroke(1f));


    }

    // WOOD BUTTON
    private void drawWoodButton(Graphics2D g2, int cx, int cy,
                                int w, int h,
                                String label,
                                boolean hov, boolean prs) {
        int x = cx - w / 2;
        int y = cy - h / 2;
        int arc = h;
    }

    // ZIGZAG
    private void drawZigzag(Graphics2D g2, int x, int y, int w) {

    }

    // ORNAMENT
    private void drawOrnament(Graphics2D g2, int cx, int cy) {

    }

    // MOUSE
    private void attachMouseListeners() {

    }

}
