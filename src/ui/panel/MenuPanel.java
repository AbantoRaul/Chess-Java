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

    private static final int CARD_W = 280;
    private static final int CARD_H = 210;
    private static final int MEDALLION_R = 40;
    private static final int BTN_W = 190;
    private static final int BTN_H = 44;

    public MenuPanel(Runnable onPlay, LayoutConfig cfg) {
        this.onPlay = onPlay;
        int w = cfg.boardSide + cfg.sideW;
        int h = cfg.boardSide;

        setPreferredSize(new Dimension(w, h));
        setLayout(null);
        initAnimation();
        setupMouseListeners();
    }

    private void initAnimation() {
        animTimer = new Timer(30, e -> {
            knightOffset += knightDir * 0.55f;
            if (knightOffset >  5f) knightDir = -1f;
            if (knightOffset < -5f) knightDir =  1f;
            repaint();
        });
        animTimer.start();
    }

    public void stopAnimation() { if (animTimer != null) animTimer.stop(); }
    public void startAnimation() { if (animTimer != null) animTimer.start(); }

    private int cardX() { return getWidth()  / 2 - CARD_W / 2; }
    private int cardY() { return getHeight() / 2 - CARD_H / 2; }

    private Rectangle getBtnRect() {
        int cx = getWidth() / 2;
        int by = cardY() + CARD_H - BTN_H - 55;
        return new Rectangle(cx - BTN_W/2, by, BTN_W, BTN_H);
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (getBtnRect().contains(e.getPoint())) { btnPress = true; repaint(); }
            }

            @Override public void mouseReleased(MouseEvent e) {
                boolean was = btnPress;
                btnPress = false; repaint();
                if (was && getBtnRect().contains(e.getPoint())) onPlay.run();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                boolean over = getBtnRect().contains(e.getPoint());
                if (over != btnHover) { btnHover = over; repaint(); }
                setCursor(over ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawChessboardBg(g2);
        drawCard(g2);

    }

    private void drawChessboardBg(Graphics2D g2) {
        int sq = 80;
        int cols = getWidth() / sq + 2;
        int rows = getHeight() / sq + 2;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2.setColor((r + c) % 2 == 0 ? SQ_LIGHT : SQ_DARK);
                g2.fillRect(c * sq, r * sq, sq, sq);
            }
        }

        int w = getWidth(), h = getHeight();
        RadialGradientPaint vig = new RadialGradientPaint(
                new Point2D.Float(w/2f, h/2f), Math.max(w, h) * 0.6f,
                new float[]{0f, 1f},
                new Color[]{new Color(0,0,0,0), new Color(0,0,0,90)});
        g2.setPaint(vig);
        g2.fillRect(0, 0, w, h);
    }

    private void drawCard(Graphics2D g2) {
        int cx = getWidth()  / 2;
        int x = cardX();
        int y = cardY();
        int w = CARD_W;
        int h = CARD_H;

        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(x + 7, y + 10, w, h, 22, 22);

        // Red card body
        GradientPaint bodyGp = new GradientPaint(x, y, BANNER_RED, x, y + h, BANNER_RED_DARK);
        g2.setPaint(bodyGp);
        g2.fillRoundRect(x, y, w, h, 22, 22);

        // Gold inner border
        g2.setColor(GOLD);
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawRoundRect(x + 5, y + 5, w - 10, h - 10, 16, 16);
        g2.setStroke(new BasicStroke(1f));

        // Top zigzag strip
        drawZigzagRow(g2, cx, y + 16, w - 24);
        // Bottom zigzag strip
        drawZigzagRow(g2, cx, y + h - 10, w - 24);

        // Cream ribbon (upper portion of card)
        int ribbonCY = y + 72;
        drawRibbon(g2, cx, ribbonCY, w + 36, 50);

        // CHESS title
        drawTitle(g2, cx, ribbonCY);

        // START button
        Rectangle btn = getBtnRect();
        drawWoodButton(g2, btn.x + btn.width/2, btn.y + btn.height/2,
                btn.width, btn.height, "START", btnHover, btnPress);

        // Knight medallion (floating above card top)
        int knightCY = y - MEDALLION_R + 12 + (int) knightOffset;
        drawKnightMedallion(g2, cx, knightCY);
    }

    // Ribbon with V-notch side tails
    private void drawRibbon(Graphics2D g2, int cx, int cy, int w, int h) {

    }


}
