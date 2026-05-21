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



    }
}
