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

}
