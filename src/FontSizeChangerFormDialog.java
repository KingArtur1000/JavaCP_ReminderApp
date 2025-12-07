import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.ChangeListener;

/**
 * Диалоговое окно для изменения размера шрифта элементов интерфейса.
 * Позволяет пользователю выбрать размер шрифта с помощью ползунка
 * и применить его к основным компонентам приложения:
 * кнопке сохранения, календарю и текстовой области.
 *
 * Используется в {@link MainWindow} через меню "Настройки".
 */
public class FontSizeChangerFormDialog extends JDialog {

    /**
     * Ползунок для выбора размера шрифта.
     * Диапазон значений: от 10 до 30, по умолчанию 18.
     */
    private JSlider slider;

    /**
     * Конструктор диалогового окна изменения размера шрифта.
     *
     * @param parent     родительское окно (главное окно приложения)
     * @param saveButton кнопка сохранения, к которой применяется новый размер шрифта
     * @param calendar   календарь, к которому применяется новый размер шрифта
     * @param textArea   текстовая область, к которой применяется новый размер шрифта
     */
    public FontSizeChangerFormDialog(JFrame parent, JButton saveButton, JCalendar calendar, JTextArea textArea) {
        super(parent, "Изменение размера шрифта", true);

        setLayout(new BorderLayout(10, 10));
        setSize(400, 150);
        setLocationRelativeTo(parent);

        // Ползунок от 10 до 30, по умолчанию 18
        slider = new JSlider(JSlider.HORIZONTAL, 10, 30, 18);
        slider.setMajorTickSpacing(2);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        // Слушатель изменения значения ползунка
        ChangeListener listener = e -> {
            int size = slider.getValue();
            applyFontSize(saveButton, calendar, textArea, size);
        };
        slider.addChangeListener(listener);

        add(new JLabel("Выберите размер шрифта:", SwingConstants.CENTER), BorderLayout.NORTH);
        add(slider, BorderLayout.CENTER);

        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Применяет выбранный размер шрифта к основным элементам интерфейса.
     *
     * @param saveButton кнопка сохранения
     * @param calendar   календарь
     * @param textArea   текстовая область
     * @param size       выбранный размер шрифта
     */
    public static void applyFontSize(JButton saveButton, JCalendar calendar, JTextArea textArea, int size) {
        calendar.setFont(new Font("Arial", Font.PLAIN, size));
        saveButton.setFont(new Font("Arial", Font.PLAIN, size));
        textArea.setFont(new Font("Arial", Font.PLAIN, size + 4));
    }
}
