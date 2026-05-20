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
        int hw = w / 2;
        int hh  = h / 2;
        int notch = 10;
        int tail = 20;

        int[] xs = {
                cx - hw,           // A: left inner top (ribbon body starts here)
                cx + hw,           // B: right inner top
                cx + hw + tail,    // C: right tail tip
                cx + hw,           // D: right inner bottom
                cx - hw,           // E: left inner bottom
                cx - hw - tail,    // F: left tail tip
        };
        int[] ys = {
                cy - hh,           // A top
                cy - hh,           // B top
                cy,                // C mid (tail point)
                cy + hh,           // D bottom
                cy + hh,           // E bottom
                cy,                // F mid (tail point)
        };

        // cream fill
        GradientPaint gp = new GradientPaint(cx, cy - hh, BANNER_CREAM,
                cx, cy + hh, BANNER_CREAM_DK);
        g2.setPaint(gp);
        g2.fillPolygon(xs, ys, 6);

        // gold border around entire ribbon
        g2.setColor(GOLD);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawPolygon(xs, ys, 6);
        g2.setStroke(new BasicStroke(1f));

    }

    private void drawTitle(Graphics2D g2, int cx, int cy) {
        g2.setFont(TITLE_FONT);
        FontRenderContext frc = g2.getFontRenderContext();
        String text = "CHESS";
        Rectangle2D tb = TITLE_FONT.getStringBounds(text, frc);
        int tx = (int)(cx - tb.getWidth() / 2);
        int ty = (int)(cy - tb.getHeight() / 2 - tb.getY());

        // emboss shadow
        g2.setColor(new Color(120, 80, 20, 130));
        g2.drawString(text, tx + 2, ty + 2);

        // gradient gold text
        GradientPaint tgp = new GradientPaint(tx, ty - (int)tb.getHeight(), GOLD_LIGHT, tx, ty, GOLD);
        g2.setPaint(tgp);
        g2.drawString(text, tx, ty);

    }

    private void drawKnightMedallion(Graphics2D g2, int cx, int cy) {
        int r = MEDALLION_R;

        g2.setColor(new Color(30, 15, 5));
        g2.fillOval(cx - r - 4, cy - r - 4, (r+4)*2, (r+4)*2);

        // body
        GradientPaint gp = new GradientPaint(cx - r, cy - r, new Color(160, 105, 45),
                cx + r, cy + r, WOOD_DARK);
        g2.setPaint(gp);
        g2.fillOval(cx - r, cy - r, r*2, r*2);

        // outer gold ring
        g2.setColor(GOLD);
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval(cx - r, cy - r, r*2, r*2);

        // inner subtle ring
        g2.setColor(new Color(197, 153, 83, 100));
        g2.setStroke(new BasicStroke(1f));
        g2.drawOval(cx - r + 6, cy - r + 6, (r-6)*2, (r-6)*2);
        g2.setStroke(new BasicStroke(1f));

        // knight glyph
        Font kf = findEmojiFont(38);
        g2.setFont(kf);
        FontRenderContext frc = g2.getFontRenderContext();
        String glyph = "\u265E";
        Rectangle2D kb = kf.getStringBounds(glyph, frc);
        int gx = (int)(cx - kb.getWidth()/2  - kb.getX());
        int gy = (int)(cy - kb.getHeight()/2 - kb.getY());
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(glyph, gx + 1, gy + 1);
        g2.setColor(new Color(230, 200, 150));
        g2.drawString(glyph, gx, gy);
    }

    // Zigzag row: alternating up/down triangles
    private void drawZigzagRow(Graphics2D g2, int cx, int y, int totalWidth) {
        g2.setColor(GOLD);
        g2.setStroke(new BasicStroke(1.5f));
        int step = 10;
        int zh = 5;
        int count = totalWidth / step;
        int startX = cx - (count * step) / 2;

        for (int i = 0; i < count; i++) {
            int x0 = startX + i * step;
            int xm = x0 + step / 2;
            int x1 = x0 + step;
            // alternate peak up / peak down for a classic zigzag
            int peakY = (i % 2 == 0) ? y - zh : y + zh;
            g2.drawLine(x0, y, xm, peakY);
            g2.drawLine(xm, peakY, x1, y);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawWoodButton(Graphics2D g2, int cx, int cy, int w, int h,
                                String label, boolean hover, boolean press) {
        int x = cx - w/2;
        int y = cy - h/2;
        int arc = h;

        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(x + 3, y + 5, w, h, arc, arc);

        Color top = press ? WOOD_DARK : hover ? WOOD_LIGHT : new Color(145, 90, 35);
        Color bot = press ? WOOD_MID  : hover ? WOOD_MID   : WOOD_DARK;
        GradientPaint gp = new GradientPaint(x, y, top, x, y + h, bot);
        g2.setPaint(gp);
        g2.fillRoundRect(x, y, w, h, arc, arc);

    }






}
