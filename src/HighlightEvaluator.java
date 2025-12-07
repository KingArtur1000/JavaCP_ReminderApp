import com.toedter.calendar.IDateEvaluator;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Класс для подсветки дат в компоненте {@link com.toedter.calendar.JCalendar}.
 * Реализует интерфейс {@link IDateEvaluator}, позволяя выделять даты,
 * для которых существуют записи в хранилище.
 *
 * Основные возможности:
 * <ul>
 *     <li>Подсветка дат, содержащихся в множестве {@code dates}.</li>
 *     <li>Изменение цвета текста для выделенных дат.</li>
 *     <li>Отображение всплывающей подсказки ("Есть запись").</li>
 * </ul>
 */
public class HighlightEvaluator implements IDateEvaluator {

    /**
     * Множество дат в формате yyyy-MM-dd, которые должны быть подсвечены.
     */
    private final Set<String> dates;

    /**
     * Форматтер для преобразования объектов {@link Date} в строковый формат yyyy-MM-dd.
     */
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Конструктор.
     *
     * @param dates множество строковых дат (yyyy-MM-dd), которые будут подсвечиваться
     */
    public HighlightEvaluator(Set<String> dates) {
        this.dates = dates;
    }

    /**
     * Определяет, является ли указанная дата "особой" (требующей подсветки).
     *
     * @param date проверяемая дата
     * @return {@code true}, если дата содержится в множестве {@code dates}, иначе {@code false}
     */
    @Override
    public boolean isSpecial(Date date) {
        return dates.contains(df.format(date));
    }

    /**
     * Цвет текста для "особых" дат.
     *
     * @return зелёный цвет текста
     */
    @Override
    public Color getSpecialForegroundColor() {
        return new Color(0, 255, 56); // тёмно‑зелёный текст
    }

    /**
     * Цвет фона для "особых" дат.
     *
     * @return {@code null}, чтобы не изменять фон и избежать конфликта с выбором даты
     */
    @Override
    public Color getSpecialBackroundColor() {
        return null;
    }

    /**
     * Всплывающая подсказка для "особых" дат.
     *
     * @return строка "Есть запись"
     */
    @Override
    public String getSpecialTooltip() {
        return "Есть запись";
    }

    /**
     * Определяет, является ли дата недопустимой.
     *
     * @param date проверяемая дата
     * @return всегда {@code false}, так как недопустимых дат нет
     */
    @Override
    public boolean isInvalid(Date date) {
        return false;
    }

    /**
     * Цвет текста для недопустимых дат.
     *
     * @return {@code null}, так как недопустимых дат нет
     */
    @Override
    public Color getInvalidForegroundColor() {
        return null;
    }

    /**
     * Цвет фона для недопустимых дат.
     * <p><b>Примечание:</b> метод содержит опечатку в названии ("BackroundColor").</p>
     *
     * @return {@code null}, так как недопустимых дат нет
     */
    @Override
    public Color getInvalidBackroundColor() {
        return null;
    }

    /**
     * Всплывающая подсказка для недопустимых дат.
     *
     * @return {@code null}, так как недопустимых дат нет
     */
    @Override
    public String getInvalidTooltip() {
        return null;
    }
}
