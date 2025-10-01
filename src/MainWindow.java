import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Set;

public class MainWindow extends JFrame {
    private final CsvStorage storage = new CsvStorage("data.csv");
    private JCalendar calendar;
    private JTextArea textArea;
    private JButton saveButton;

    // Множество дат для подсветки и один общий Evaluator
    private Set<String> highlightedDates;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }

    public MainWindow() {
        setTitle("Каталог компьютерной техники");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();

        saveButton.addActionListener(this::saveData);
        // событие при выборе даты
        calendar.addPropertyChangeListener("calendar", evt -> loadForSelectedDate());


        // Обработчик события нажатия Enter, будучи внутри textArea
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();

        // Переопределяем действие на Enter
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "insertBullet");

        actionMap.put("insertBullet", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int pos = textArea.getCaretPosition();
                    // вставляем символ • и перенос строки
                    textArea.getDocument().insertString(pos, "\n• ", null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        // 🔹 Проверка сегодняшней даты
        checkTodayPlan();
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


    private JMenuBar getBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu settingsMenu = new JMenu("Настройки");
        JMenu whatMenu = new JMenu("?");

        // Меню вкладки -Настройки-
        JMenuItem fontSizeItem = new JMenuItem("Размер шрифта");

        // Меню вкладки -?-
        JMenuItem aboutAuthorItem = new JMenuItem("Об авторе");

        // Привязываемся к событиям
        aboutAuthorItem.addActionListener(e -> {JOptionPane.showMessageDialog(this, "Автор: Борсук Р.А.!");});
        fontSizeItem.addActionListener(e -> {
            FontSizeChangerFormDialog dialog = new FontSizeChangerFormDialog(this, saveButton, calendar, textArea);
            dialog.setVisible(true);
        });

        // Привязываем элементы к вкладке -Настройки-
        settingsMenu.add(fontSizeItem);

        // Привязываем элементы к вкладке -?-
        whatMenu.add(aboutAuthorItem);

        // Привязываем вкладки к панели меню
        menuBar.add(settingsMenu);
        menuBar.add(whatMenu);
        return menuBar;
    }


    private void initUI() {
        // Создание объектов меню
        JMenuBar menuBar = getBar();

        // Левая часть: календарь
        calendar = new JCalendar();
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.add(calendar, BorderLayout.CENTER);

        // Правая часть: текст + кнопка
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 22));
        JScrollPane scrollPane = new JScrollPane(textArea);

        saveButton = new JButton("Сохранить");
        saveButton.setBackground(new Color(95, 212, 124));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.PLAIN, 18));

        // Разделитель правой части: сверху - окно ввода текста, снизу - кнопка сохранения
        JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, saveButton);
        rightPane.setResizeWeight(0.85); // 85% сверху, 15% снизу
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
        HighlightEvaluator evaluator = new HighlightEvaluator(highlightedDates);
        calendar.getDayChooser().addDateEvaluator(evaluator);
        calendar.getDayChooser().repaint();
        calendar.setFont(new Font("Arial", Font.PLAIN, 18));
        calendar.revalidate();
        calendar.repaint();
        calendar.setCalendar(calendar.getCalendar()); // форс обновления


        // Добавляем на форму основной splitPane и отображаем на форме :)
        add(splitPane, BorderLayout.CENTER);
        setVisible(true);

        setJMenuBar(menuBar);
    }


    private void loadForSelectedDate() {
        Date selectedDate = calendar.getDate();
        String text = storage.getByDate(selectedDate);
        textArea.setText(text == null ? "" : text);
    }

    private void checkTodayPlan() {
        Date today = new Date();
        String text = storage.getByDate(today);
        if (text != null && !text.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Сегодня у вас запланировано: " + text,
                    "Напоминание",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

}
