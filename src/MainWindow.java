import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JCalendar;

public class MainWindow {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Параметры окна
            JFrame frame = new JFrame("Reminder App");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);


            // Левая часть: календарь
            JCalendar calendar = new JCalendar();
            JPanel calendarPanel = new JPanel(new BorderLayout());
            calendarPanel.add(calendar, BorderLayout.CENTER);


            // Правая часть: текст + кнопка
            JTextArea textArea = new JTextArea("Rostiiiiick");
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(textArea);

            JButton saveButton = new JButton("Сохранить");

            // Разделитель правой части: сверху - окно ввода текста, снизу - кнопка сохранения
            JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, saveButton);
            rightPane.setResizeWeight(0.85); // 80% сверху, 20% снизу
            rightPane.setOneTouchExpandable(false);


            // Главный SplitPane: делим окно на 2 части:
            JSplitPane splitPane = new JSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT,
                    calendarPanel,
                    rightPane
            );
            splitPane.setResizeWeight(0.7); // 70% слева, 30% справа
            splitPane.setOneTouchExpandable(false); // стрелочки для сворачивания

            // Добавляем на форму основной splitPane и отображаем на форме :)
            frame.add(splitPane, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
