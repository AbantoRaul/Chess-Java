package ui.app;

import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessApp {

    private static final int MIN_W = 775;
    private static final int MIN_H = 663;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

            Insets insets = Toolkit.getDefaultToolkit()
                    .getScreenInsets(GraphicsEnvironment
                            .getLocalGraphicsEnvironment()
                            .getDefaultScreenDevice()
                            .getDefaultConfiguration());

            int usableW = screen.width  - insets.left - insets.right;
            int usableH = screen.height - insets.top  - insets.bottom;

            JFrame frame = new JFrame("Chess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);

            // Pass full usable screen size so LayoutConfig scales correctly
            GameController controller = new GameController(frame, usableW, usableH);
            frame.setContentPane(controller.buildMainPanel());

            frame.setMinimumSize(new Dimension(MIN_W, MIN_H));
            frame.setSize(MIN_W, MIN_H);
            frame.getContentPane().setMinimumSize(new Dimension(MIN_W, MIN_H));

            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    int w = frame.getWidth();
                    int h = frame.getHeight();
                    boolean changed = false;

                    if (w < MIN_W) { w = MIN_W; changed = true; }
                    if (h < MIN_H) { h = MIN_H; changed = true; }

                    if (changed) frame.setSize(w, h);
                }
            });

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}