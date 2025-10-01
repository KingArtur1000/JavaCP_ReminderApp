import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.ChangeListener;
import javax.swing.table.JTableHeader;

public class FontSizeChangerFormDialog extends JDialog {
    private JSlider slider;

    public FontSizeChangerFormDialog(JFrame parent, JButton saveButton, JCalendar calendar, JTextArea textArea) {
        super(parent, "Изменение размера шрифта", true);

        setLayout(new BorderLayout(10, 10));
        setSize(400, 150);
        setLocationRelativeTo(parent);

        // Ползунок от 10 до 30, по умолчанию 16
        slider = new JSlider(JSlider.HORIZONTAL, 10, 30, 18);
        slider.setMajorTickSpacing(2);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        // Слушатель изменения значения
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

    /** Метод для применения размера шрифта к таблице */
    public static void applyFontSize(JButton saveButton, JCalendar calendar, JTextArea textArea, int size) {
        calendar.setFont(new Font("Arial", Font.PLAIN, size));
        saveButton.setFont(new Font("Arial", Font.PLAIN, size));
        textArea.setFont(new Font("Arial", Font.PLAIN, size + 4));
    }
}