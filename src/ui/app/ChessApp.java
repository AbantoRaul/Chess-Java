package ui.app;

import ui.controller.GameController;

import javax.swing.*;
import java.awt.*;

public class ChessApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // usable screen area excluding taskbar
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

            GameController controller = new GameController(frame, usableW, usableH);

            frame.setContentPane(controller.buildMainPanel());

            frame.pack();
            frame.setMinimumSize(new Dimension(640, 520));



        });
    }
}
