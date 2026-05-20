package ui.app;

public class LayoutConfig {
    // Board geometry
    public final int sq; // square size in pixels
    public final int frame; // wooden frame thickness
    public final int padding; // outer padding around frame
    public final int boardPx; // sq * 8
    public final int total; // boardPx + frame * 2
    public final int boardSide; // total + padding * 2

    // Side panel
    public final int sideW;  // side panel width

    // Pause button
    public final int pauseBtnR; // pause button radius
    public final int pieceFontSz; // piece font size

    public LayoutConfig(int usableW, int usableH) {
        int maxBoardSide = (int)(Math.min(usableW * 0.78, usableH * 0.92));

    }
}
