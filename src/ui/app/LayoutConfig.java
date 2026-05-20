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

        sq = Math.max(40, Math.min(90, maxBoardSide / 10));
        frame = Math.max(28, sq / 2);
        padding = Math.max(15, sq / 5);
        boardPx = sq * 8;
        total = boardPx + frame * 2;
        boardSide = total + padding * 2;

        sideW = Math.max(160, sq * 3);
        pauseBtnR = Math.max(14, frame / 2 - 4);
        pieceFontSz = Math.max(20, sq - 8);
    }
}
