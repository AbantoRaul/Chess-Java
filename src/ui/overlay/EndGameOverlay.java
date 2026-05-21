package ui.overlay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class EndGameOverlay extends JPanel {
    private static final Color BANNER_RED = new Color(139, 28, 28);
    private static final Color BANNER_RED_DARK = new Color(90, 15, 15);
    private static final Color BANNER_CREAM = new Color(237, 213, 176);
    private static final Color GOLD = new Color(197, 153, 83);
    private static final Color GOLD_LIGHT = new Color(230, 190, 120);
    private static final Color WOOD_DARK = new Color(95, 55, 20);
    private static final Color WOOD_MID = new Color(130, 80, 30);
    private static final Color WOOD_LIGHT = new Color(170, 110, 50);

    private final Runnable onNewGame;
    private final Runnable onMainMenu;
    private final Runnable onQuit;

    private final boolean[] hover = new boolean[3];
    private final boolean[] press = new boolean[3];

    private String title = "";
    private String subtitle = "";

    private float alpha = 0f;
    private float slideY = 0f;
    private Timer animIn;
    private Timer animOut;

    // Layout — recomputed each paint
    private int bannerW, bannerH, bannerX, bannerY;
    private int knobR;
    private int btn0X, btn1X, btn2X, btnY;
    private int btnW, btnH;
    private int titleTextY, subtitleTextY;

    private Font titleFont;
    private Font subtitleFont;
    private Font btnFont;

    public EndGameOverlay(Runnable onNewGame, Runnable onMainMenu, Runnable onQuit) {
        this.onNewGame = onNewGame;
        this.onMainMenu = onMainMenu;
        this.onQuit = onQuit;
        setOpaque(false);
        setLayout(null);
        attachMouseListeners();
    }

    // Show
    public void show(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;

        hover[0] = hover[1] = hover[2] = false;
        press[0] = press[1] = press[2] = false;

        stopAll();

        alpha = 0f;
        slideY = 0f;

        setVisible(true);
        setEnabled(true);

        animIn = new Timer(16, e -> {
            alpha = Math.min(1f, alpha + 0.07f);
            slideY = Math.min(1f, slideY + 0.07f);
            repaint();
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

    // Layout
    private void recomputeLayout() {
        int pw = getWidth(), ph = getHeight();
        if (pw == 0 || ph == 0) return;

        bannerW = (int) (pw * 0.68);
        bannerW = Math.max(340, Math.min(580, bannerW));

        int titleSz = Math.max(15, bannerW / 18);
        int subtitleSz = Math.max(12, bannerW / 23);
        int btnSz = Math.max(10, bannerW / 26);

        titleFont = new Font("Serif", Font.BOLD, titleSz);
        subtitleFont = new Font("Serif", Font.BOLD, subtitleSz);
        btnFont = new Font("SansSerif", Font.BOLD, btnSz);

        btnW = Math.max(100, bannerW / 4);
        btnH = Math.max(38, titleSz + 18);
        knobR = Math.max(18, btnH / 2 + 6);  // knob radius just bigger than half btn height

        int paddingTop = Math.max(10, titleSz / 2);  // less top padding so text sits higher
        int lineGap = Math.max(4, titleSz / 4); // tighter gap between title and subtitle
        int sepGap = Math.max(28, titleSz + 14); // more space from subtitle down to buttons
        int btnPadBot = Math.max(20, titleSz + 6);

        bannerH = paddingTop
                + titleSz
                + lineGap
                + subtitleSz
                + sepGap
                + btnH
                + btnPadBot;
        bannerH = Math.max(150, bannerH);

        bannerX = (pw - bannerW) / 2;
        int finalY = (int) (ph * 0.30);
        int startY = -bannerH - knobR * 2 - 10;
        bannerY = (int) (startY + (finalY - startY) * easeOut(slideY));

        // Text positions
        int cursor = bannerY + paddingTop;
        titleTextY = cursor + titleSz;
        cursor = titleTextY + lineGap;
        subtitleTextY = cursor + subtitleSz;
        cursor = subtitleTextY + sepGap;
        btnY = cursor + btnH / 2;

        int margin = knobR + 16;
        int innerW = bannerW - margin * 2;
        int totalBtnW = btnW * 3;
        int spacing = (innerW - totalBtnW) / 2;  // gap between buttons
        btn0X = bannerX + margin + btnW / 2;
        btn1X = btn0X + btnW + spacing;
        btn2X = btn1X + btnW + spacing;
    }

    // Button rects for hit detection
    private Rectangle btnRect(int idx) {
        int cx = idx == 0 ? btn0X : idx == 1 ? btn1X : btn2X;
        return new Rectangle(cx - btnW / 2, btnY - btnH / 2, btnW, btnH);
    }

    private int hitBtn(Point p) {
        for (int i = 0; i < 3; i++) if (btnRect(i).contains(p)) return i;
        return -1;
    }

    // Paint
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (alpha <= 0f) return;

        recomputeLayout();
        Graphics2D g2 = (Graphics2D) g;
    }


    // Mouse listeners
    private void attachMouseListeners() {

    }

}
