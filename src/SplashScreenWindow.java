import javax.swing.*;
import java.awt.*;

/**
 * Класс для отображения стартового окна (Splash Screen) при запуске приложения.
 * Используется для демонстрации информации о курсовом проекте перед открытием
 * основного окна {@link MainWindow}.
 *
 * В окне отображается текст:
 * <pre>
 * Курсовой проект
 * Борсук Ростислав Александрович
 * гр. 10702423
 * </pre>
 */
public class SplashScreenWindow extends JWindow {

    /**
     * Конструктор. Создаёт окно SplashScreen с текстовой панелью,
     * устанавливает размеры и позицию по центру экрана.
     */
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

    /**
     * Отображает SplashScreen на указанное количество миллисекунд,
     * затем скрывает и освобождает ресурсы окна.
     *
     * <p><b>Важно:</b> использование {@link Thread#sleep(long)} блокирует поток
     * отрисовки Swing (EDT), поэтому рекомендуется вызывать этот метод
     * в отдельном потоке или использовать {@link javax.swing.Timer}.</p>
     *
     * @param millis время отображения окна в миллисекундах
     */
    public void showSplash(int millis) {
        setVisible(true);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
        setVisible(false);
        dispose();
    }
}
