import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Главное окно приложения "Каталог компьютерной техники".
 * <p>
 * Реализует графический интерфейс с календарём, текстовой областью для заметок
 * и меню управления. Поддерживает сохранение и загрузку данных в формате CSV,
 * подсветку дат с записями, а также напоминания о ближайших событиях.
 * </p>
 *
 * Основные возможности:
 * <ul>
 *     <li>Выбор даты в календаре и работа с заметками для этой даты.</li>
 *     <li>Сохранение заметок в файл {@code data.csv} или экспорт в другой CSV.</li>
 *     <li>Загрузка данных из выбранного CSV-файла.</li>
 *     <li>Подсветка дат с записями.</li>
 *     <li>Меню с настройками (изменение размера шрифта), списком памятных дат и информацией об авторе.</li>
 *     <li>Отображение SplashScreen при запуске.</li>
 * </ul>
 */
public class MainWindow extends JFrame {
    /**
     * Хранилище данных по умолчанию (файл {@code data.csv}).
     */
    private final CsvStorage storage = new CsvStorage("data.csv");

    /**
     * Компонент календаря для выбора даты.
     */
    private JCalendar calendar;

    /**
     * Текстовая область для ввода заметок.
     */
    private JTextArea textArea;

    /**
     * Кнопка сохранения заметки.
     */
    private JButton saveButton;

    /**
     * Множество дат, для которых есть записи (используется для подсветки).
     */
    private Set<String> highlightedDates;

    /**
     * Точка входа в приложение.
     * <p>
     * Сначала отображает {@link SplashScreenWindow}, затем через 3 секунды
     * открывает основное окно {@link MainWindow}.
     * </p>
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        SplashScreenWindow splash = new SplashScreenWindow();
        splash.setVisible(true);

        // отдельный поток, чтобы не блокировать EDT
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {}
            SwingUtilities.invokeLater(() -> {
                splash.dispose();
                new MainWindow();
            });
        }).start();
    }


    /**
     * Конструктор главного окна.
     * <p>
     * Инициализирует интерфейс, настраивает обработчики событий
     * и проверяет наличие заметки на сегодняшнюю дату.
     * </p>
     */
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

        // Если текстовое поле пустое, то вставляем "• " в начало строки
        textArea.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Событие: textArea получил фокус
                if (textArea.getText().isEmpty()) {
                    textArea.setText("• ");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Событие: textArea потерял фокус
                if (Objects.equals(textArea.getText(), "• ")) {
                    textArea.setText("");
                }
            }
        });



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


    /**
     * Сохраняет данные для выбранной даты в {@code data.csv}.
     * Обновляет подсветку дат и показывает сообщение пользователю.
     *
     * @param e событие нажатия кнопки
     */
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

    /**
     * Создаёт и настраивает меню приложения.
     * <p>
     * Включает вкладки:
     * <ul>
     *     <li>Файл (сохранение, загрузка, выход)</li>
     *     <li>Настройки (изменение размера шрифта)</li>
     *     <li>Дополнительно (список дат, ближайшая дата)</li>
     *     <li>? (об авторе)</li>
     * </ul>
     * </p>
     *
     * @return панель меню {@link JMenuBar}
     */
    private JMenuBar getBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenu settingsMenu = new JMenu("Настройки");
        JMenu extraMenu = new JMenu("Дополнительно");
        JMenu whatMenu = new JMenu("?");

        // Меню вкладки -Настройки-
        JMenuItem fontSizeItem = new JMenuItem("Размер шрифта");

        // Меню вкладки -?-
        JMenuItem aboutAuthorItem = new JMenuItem("Об авторе");

        // Меню вкладки -Дополнительно-
        JMenuItem listItem = new JMenuItem("Список памятных дат");
        listItem.addActionListener(e -> showDatesList());
        extraMenu.add(listItem);

        //Меню вкладки -Файл-
        JMenuItem fileSave = new JMenuItem("Сохранить");
        JMenuItem fileSaveAs = new JMenuItem("Сохранить как");
        JMenuItem fileLoadAs = new JMenuItem("Загрузить");
        JMenuItem exitProgram = new JMenuItem("Выход");
        exitProgram.setForeground(new Color(220, 53, 69));
        exitProgram.setFont(new Font("Arial", Font.BOLD, 12));

        JMenuItem nearestItem = new JMenuItem("Ближайшая памятная дата");
        nearestItem.addActionListener(e -> showNearestDate());
        extraMenu.add(nearestItem);

        // Привязываемся к событиям
        aboutAuthorItem.addActionListener(e -> {JOptionPane.showMessageDialog(this, "Автор: Борсук Р.А.");});
        fontSizeItem.addActionListener(e -> {
            FontSizeChangerFormDialog dialog = new FontSizeChangerFormDialog(this, saveButton, calendar, textArea);
            dialog.setVisible(true);
        });
        exitProgram.addActionListener(e -> System.exit(0));
        fileSave.addActionListener(e -> {
            // всегда сохраняем в data.csv в рабочей папке
            File file = new File("data.csv");
            CsvStorage defaultStorage = new CsvStorage(file.getAbsolutePath());

            Date selectedDate = calendar.getDate();
            String text = textArea.getText().trim();

            defaultStorage.save(selectedDate, text);

            // обновляем подсветку
            String dateKey = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
            if (text.isEmpty()) {
                highlightedDates.remove(dateKey);
            } else {
                highlightedDates.add(dateKey);
            }
            calendar.setCalendar(calendar.getCalendar()); // форс обновления

            JOptionPane.showMessageDialog(this, "Данные сохранены в data.csv");
        });


        fileSaveAs.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Сохранить все данные как CSV");
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV файлы", "csv"));
            chooser.setSelectedFile(new File("data.csv"));

            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String path = file.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv")) {
                    file = new File(path + ".csv");
                }

                // Загружаем все данные из текущего storage
                Map<String, String> allData = storage.loadAll();

                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("date;text");
                    for (Map.Entry<String, String> entry : allData.entrySet()) {
                        String escaped = entry.getValue().replace("\n", "\\n");
                        writer.println(entry.getKey() + ";" + escaped);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Ошибка при сохранении!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this, "Все данные сохранены в: " + file.getName());
            }
        });



        fileLoadAs.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Загрузить данные из CSV");
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV файлы", "csv"));

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                CsvStorage customStorage = new CsvStorage(file.getAbsolutePath());

                Date selectedDate = calendar.getDate();
                String text = customStorage.getByDate(selectedDate);
                textArea.setText(text == null ? "" : text);

                // Обновляем подсветку дат
                highlightedDates = customStorage.getAllDates();
                HighlightEvaluator evaluator = new HighlightEvaluator(highlightedDates);
                calendar.getDayChooser().addDateEvaluator(evaluator);
                calendar.setCalendar(calendar.getCalendar());

                JOptionPane.showMessageDialog(this, "Данные загружены из: " + file.getName());
            }
        });



        // Привязываем элементы к вкладке -Файл-
        fileMenu.add(fileSave);
        fileMenu.add(fileSaveAs);
        fileMenu.add(fileLoadAs);
        fileMenu.add(exitProgram);

        // Привязываем элементы к вкладке -Настройки-
        settingsMenu.add(fontSizeItem);

        // Привязываем элементы к вкладке -?-
        whatMenu.add(aboutAuthorItem);

        // Привязываем вкладки к панели меню
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        menuBar.add(extraMenu);
        menuBar.add(whatMenu);
        return menuBar;
    }


    /**
     * Инициализирует графический интерфейс:
     * <ul>
     *     <li>Создаёт календарь и текстовую область.</li>
     *     <li>Добавляет кнопку сохранения.</li>
     *     <li>Настраивает разделители {@link JSplitPane}.</li>
     *     <li>Загружает данные для текущей даты.</li>
     *     <li>Применяет подсветку дат с записями.</li>
     * </ul>
     */
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

    /**
     * Загружает заметку для выбранной даты и отображает её в текстовой области.
     */
    private void loadForSelectedDate() {
        Date selectedDate = calendar.getDate();
        String text = storage.getByDate(selectedDate);
        textArea.setText(text == null ? "" : text);
    }

    /**
     * Проверяет наличие заметки на сегодняшнюю дату.
     * Если заметка есть — показывает напоминание.
     */
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

    /**
     * Показывает список всех памятных дат в диалоговом окне.
     */
    private void showDatesList() {
        // Для простоты пока берём все даты (можно расширить до выбора диапазона)
        Map<Date, String> data = storage.getAllAsDates();
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет памятных дат.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        for (Map.Entry<Date, String> entry : data.entrySet()) {
            sb.append(fmt.format(entry.getKey()))
                    .append(" — ")
                    .append(entry.getValue())
                    .append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Список памятных дат", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Показывает ближайшую будущую памятную дату.
     */
    private void showNearestDate() {
        Map.Entry<Date, String> nearest = storage.getNearestFutureDate(new Date());
        if (nearest == null) {
            JOptionPane.showMessageDialog(this, "Ближайших дат нет.");
            return;
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        JOptionPane.showMessageDialog(
                this,
                "Ближайшая дата: " + fmt.format(nearest.getKey()) +
                        "\nЗапланировано: " + nearest.getValue(),
                "Напоминание",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
}
