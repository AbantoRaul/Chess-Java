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

    // MOUSE
    private void attachMouseListeners() {

    }

}
