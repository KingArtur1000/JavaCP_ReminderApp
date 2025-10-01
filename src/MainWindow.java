import com.toedter.calendar.JCalendar;
import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;

public class MainWindow {
    private final CsvStorage storage = new CsvStorage("data.csv");
    private JCalendar calendar;
    private JTextArea textArea;
    private JButton saveButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }

    public MainWindow() {
        initUI();

        saveButton.addActionListener(this::saveData);
        // событие при выборе даты
        calendar.addPropertyChangeListener("calendar", evt -> loadForSelectedDate());
    }


    private void saveData(ActionEvent e) {
        Date selectedDate = calendar.getDate();
        String text = textArea.getText().trim();
        if (!text.isEmpty()) {
            storage.save(selectedDate, text);
            JOptionPane.showMessageDialog(null, "Сохранено!");
        }
    }

    private void initUI() {
        // Параметры окна
        JFrame frame = new JFrame("Reminder App");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);


        // Левая часть: календарь
        calendar = new JCalendar();
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.add(calendar, BorderLayout.CENTER);


        // Правая часть: текст + кнопка
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        saveButton = new JButton("Сохранить");
        saveButton.setBackground(new Color(95, 212, 124));
        saveButton.setForeground(Color.WHITE);

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


        // Загружаем данные для сегодняшней даты
        loadForSelectedDate();

        // Добавляем на форму основной splitPane и отображаем на форме :)
        frame.add(splitPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }


    private void loadForSelectedDate() {
        Date selectedDate = calendar.getDate();
        String text = storage.getByDate(selectedDate);
        textArea.setText(text);
    }
}
