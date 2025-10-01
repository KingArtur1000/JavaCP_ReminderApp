import com.toedter.calendar.IDateEvaluator;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class HighlightEvaluator implements IDateEvaluator {
    private final Set<String> dates; // yyyy-MM-dd
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public HighlightEvaluator(Set<String> dates) {
        this.dates = dates;
    }

    @Override
    public boolean isSpecial(Date date) {
        return dates.contains(df.format(date));
    }

    @Override
    public Color getSpecialForegroundColor() {
        return new Color(0, 255, 56); // тёмно‑зелёный текст
    }


    @Override
    public Color getSpecialBackroundColor() {
        return null; // фон не трогаем, чтобы не конфликтовать с выбором
    }


    @Override
    public String getSpecialTooltip() {
        return "Есть запись";
    }

    @Override public boolean isInvalid(Date date) { return false; }
    @Override public Color getInvalidForegroundColor() { return null; }
    // Да-да опечатка - BackroundColor !!!
    @Override public Color getInvalidBackroundColor() { return null; }
    @Override public String getInvalidTooltip() { return null; }
}
