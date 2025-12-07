import javax.swing.*;
import java.awt.*;

public class SplashScreenWindow extends JWindow {
    public SplashScreenWindow() {
        // Панель с текстом
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));

        JLabel label = new JLabel(
                "<html><center><h2 style='color:black;'>Курсовой проект<br>" +
                        "Борсук Ростислав Александрович<br>" +
                        "гр. 10702423</h2></center></html>",
                SwingConstants.CENTER
        );
        label.setFont(new Font("Arial", Font.BOLD, 22));

        panel.add(label, BorderLayout.CENTER);
        getContentPane().add(panel);

        setSize(500, 300);
        setLocationRelativeTo(null);
    }

    public void showSplash(int millis) {
        setVisible(true);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
        setVisible(false);
        dispose();
    }
}
