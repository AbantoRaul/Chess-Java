package ui.overlay;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class CheckOverlay extends JPanel {
    private static final Color BANNER_RED = new Color(139,  28,  28);
    private static final Color BANNER_RED_DARK = new Color( 90,  15,  15);
    private static final Color BANNER_CREAM = new Color(237, 213, 176);
    private static final Color GOLD = new Color(197, 153,  83);
    private static final Color GOLD_LIGHT = new Color(230, 190, 120);
    private static final Color WOOD_DARK = new Color( 95,  55,  20);
    private static final Color WOOD_MID = new Color(130,  80,  30);
    private static final Color WOOD_LIGHT = new Color(170, 110,  50);

    // Message to display
    private String message = "";

    // Animation
    private float alpha = 0f;
    private float slideY = 0f;
    private Timer animIn;
    private Timer animOut;
    private Timer holdTimer;

    // Layout — recomputed each paint
    private int bannerW, bannerH, bannerX, bannerY;
    private int tubeH, knobR;
    private Font msgFont;

    public CheckOverlay() {
        setOpaque(false);
        setLayout(null);
    }

    // Called by GameControllerSwing whenever check starts
    public void showCheck(String playerName) {
        message = playerName + " is in CHECK!";
        stopAll();
        alpha  = 0f;
        slideY = 0f;

        animIn = new Timer(16, e -> {
            alpha  = Math.min(1f, alpha  + 0.08f);
            slideY = Math.min(1f, slideY + 0.08f);

            repaint();

            if (getParent() != null) getParent().repaint();

            if (alpha >= 1f && slideY >= 1f) {
                ((Timer) e.getSource()).stop();
                // Hold for 1.8 seconds then fade out
                holdTimer = new Timer(400, ev -> {
                    ((Timer) ev.getSource()).stop();
                    fadeOut();
                });
                holdTimer.setRepeats(false);
                holdTimer.start();
            }
        });
        animIn.start();
        setVisible(true);
    }

    // Called when check is resolved (king moved out of check)
    public void hideCheck() {
        stopAll();
        fadeOut();
    }

    private void fadeOut() {
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
        if (holdTimer != null) holdTimer.stop();
    }

    // LAYOUT
    private void recomputeLayout() {
        int pw = getWidth(), ph = getHeight();
        if (pw == 0 || ph == 0) return;

        // Banner: wide but short — sits at the top center of the board
        bannerW = (int)(pw * 0.55);
        bannerH = (int)(ph * 0.12);
        bannerH = Math.max(50, Math.min(90, bannerH));
        bannerX = (pw - bannerW) / 2;

        // Slide in from above: starts above the panel, slides to final position
        int finalBannerY = (int)(ph * 0.06);
        int startBannerY = -bannerH - 20;
        bannerY = (int)(startBannerY + (finalBannerY - startBannerY) * easeOut(slideY));

        // Tube and knobs
        tubeH = Math.max(12, bannerH / 5);
        knobR = Math.max(10, tubeH / 2 + 4);

        // Font scales with banner
        int msgSz = Math.max(13, bannerW / 12);
        msgFont = new Font("Serif", Font.BOLD, msgSz);
    }

    private float easeOut(float t) {
        float f = 1 - t;
        return 1 - f * f * f;
    }

    // PAINT
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (alpha <= 0f) return;

        recomputeLayout();
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Composite origComp = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        drawBanner(g2);

        g2.setComposite(origComp);
    }

    private void drawBanner(Graphics2D g2) {
        int cx = getWidth() / 2;

        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(bannerX + 5, bannerY + 7, bannerW, bannerH, 14, 14);

        // Red body
        GradientPaint body = new GradientPaint(
                bannerX, bannerY, BANNER_RED,
                bannerX, bannerY + bannerH, BANNER_RED_DARK);

        g2.setPaint(body);
        g2.fillRoundRect(bannerX, bannerY, bannerW, bannerH, 14, 14);

        // Inner gold border
        g2.setColor(GOLD);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bannerX + 4, bannerY + 4,
                bannerW - 8, bannerH - 8, 10, 10);
        g2.setStroke(new BasicStroke(1f));

        // Left and right scroll ends
        // These sit on the left and right edges of the banner (like a horizontal scroll)
        drawScrollKnob(g2, bannerX, bannerY + bannerH / 2);
        drawScrollKnob(g2, bannerX + bannerW, bannerY + bannerH / 2);

        // CHECK message
        g2.setFont(msgFont);
        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D tb = msgFont.getStringBounds(message, frc);
        int tx = (int) (cx - tb.getWidth() / 2);
        int ty = (int) (bannerY + bannerH / 2 - tb.getHeight() / 2 - tb.getY());

        // Shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawString(message, tx + 1, ty + 1);

        // Main text
        g2.setColor(GOLD_LIGHT);
        g2.drawString(message, tx, ty);

        // Zigzag decorations left and right of text
        int textW = (int) tb.getWidth();
        int zigGap = 10;
        int zigLen = (bannerW / 2 - textW / 2) - knobR - zigGap * 2;
        if (zigLen > 20) {
            int zigY = bannerY + bannerH / 2;
            drawZigzag(g2, bannerX + knobR + zigGap, zigY, zigLen);
            drawZigzag(g2, cx + textW / 2 + zigGap, zigY, zigLen);
        }
    }

    // Scroll knob on the left or right edge of the banner
    private void drawScrollKnob(Graphics2D g2, int cx, int cy) {
        int kw = knobR * 2;
        int kh = (int) (bannerH * 0.85);
        int kx = cx - knobR;
        int ky = cy - kh / 2;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(kx + 2, ky + 3, kw, kh, kw, kw);

        // Body
        GradientPaint gp = new GradientPaint(kx, ky, WOOD_LIGHT, kx + kw, ky + kh, WOOD_DARK);
        g2.setPaint(gp);
        g2.fillRoundRect(kx, ky, kw, kh, kw, kw);

        // Gold border
        g2.setColor(GOLD);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(kx, ky, kw, kh, kw, kw);
        g2.setStroke(new BasicStroke(1f));

        // Highlight
        g2.setColor(new Color(230, 190, 120, 120));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(cx - knobR / 2, ky + 5, cx + knobR / 2, ky + 5);
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawZigzag(Graphics2D g2, int x, int y, int w) {

    }
