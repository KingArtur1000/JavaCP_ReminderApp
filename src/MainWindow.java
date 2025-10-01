import com.toedter.calendar.JCalendar;
import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Set;

public class MainWindow {
    private final CsvStorage storage = new CsvStorage("data.csv");
    private JCalendar calendar;
    private JTextArea textArea;
    private JButton saveButton;

    // Множество дат для подсветки и один общий Evaluator
    private Set<String> highlightedDates;
    private HighlightEvaluator evaluator;

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

        storage.save(selectedDate, text);

        String dateKey = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        if (text.isEmpty()) {
            highlightedDates.remove(dateKey); // убираем подсветку
            calendar.setCalendar(calendar.getCalendar()); // форс обновления
        } else {
            highlightedDates.add(dateKey);    // добавляем подсветку
            calendar.setCalendar(calendar.getCalendar()); // форс обновления
        }


        JOptionPane.showMessageDialog(null, text.isEmpty() ? "Удалено!" : "Сохранено!");
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


        // Применяем особый стиль к датам, где есть запись
        highlightedDates = storage.getAllDates();
        System.out.println(highlightedDates);
        evaluator = new HighlightEvaluator(highlightedDates);
        calendar.getDayChooser().addDateEvaluator(evaluator);
        calendar.getDayChooser().repaint();
        calendar.setFont(new Font("Arial", Font.PLAIN, 18));
        calendar.revalidate();
        calendar.repaint();
        calendar.setCalendar(calendar.getCalendar()); // форс обновления


        // Добавляем на форму основной splitPane и отображаем на форме :)
        frame.add(splitPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }


    private void loadForSelectedDate() {
        Date selectedDate = calendar.getDate();
        String text = storage.getByDate(selectedDate);
        textArea.setText(text == null ? "" : text);
    }
}
