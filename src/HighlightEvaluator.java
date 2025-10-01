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
        return Color.WHITE;
    }

    // В JCalendar 1.4 именно "Backround" (с опечаткой)
    @Override
    public Color getSpecialBackroundColor() {
        return new Color(95, 212, 124); // зелёный фон
    }

    @Override
    public String getSpecialTooltip() {
        return "Есть запись";
    }

    @Override public boolean isInvalid(Date date) { return false; }
    @Override public Color getInvalidForegroundColor() { return null; }
    @Override public Color getInvalidBackroundColor() { return null; }
    @Override public String getInvalidTooltip() { return null; }
}
