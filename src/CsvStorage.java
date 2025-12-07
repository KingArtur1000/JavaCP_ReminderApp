import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Класс для хранения и управления данными в формате CSV.
 * Используется для сохранения заметок, связанных с датами,
 * а также для их загрузки и выборки по различным критериям.
 *
 * Формат CSV:
 * <pre>
 * date;text
 * 2025-12-07;Заметка
 * </pre>
 */
public class CsvStorage {

    /**
     * Имя файла CSV, в котором хранятся данные.
     */
    private final String fileName;

    /**
     * Формат даты для ключей в CSV (год-месяц-день).
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Конструктор. Создаёт объект хранилища и проверяет наличие CSV-файла.
     * Если файл отсутствует, создаётся новый с заголовком.
     *
     * @param fileName имя CSV-файла
     */
    public CsvStorage(String fileName) {
        this.fileName = fileName;
        ensureCsvExists();
    }

    /**
     * Проверяет наличие CSV-файла. Если файл отсутствует,
     * создаёт новый и записывает заголовок "date;text".
     */
    private void ensureCsvExists() {
        File file = new File(fileName);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("date;text");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Сохраняет или удаляет запись для указанной даты.
     * Если текст пустой — запись удаляется.
     * Если текст непустой — сохраняется с экранированием переносов строк.
     *
     * @param date дата записи
     * @param text текст заметки
     */
    public void save(Date date, String text) {
        String dateStr = dateFormat.format(date);
        Map<String, String> all = loadAll();

        if (text == null || text.isEmpty()) {
            all.remove(dateStr);
        } else {
            String escaped = text.replace("\n", "\\n");
            all.put(dateStr, escaped);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("date;text");
            for (Map.Entry<String, String> entry : all.entrySet()) {
                writer.println(entry.getKey() + ";" + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Загружает все записи из CSV в виде карты "строка даты → текст".
     * При чтении переносы строк восстанавливаются.
     *
     * @return карта всех записей
     */
    public Map<String, String> loadAll() {
        Map<String, String> data = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            reader.readLine(); // пропускаем заголовок
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    String restored = parts[1].replace("\\n", "\n");
                    data.put(parts[0], restored);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Получает запись по конкретной дате.
     *
     * @param date дата
     * @return текст заметки или пустая строка, если записи нет
     */
    public String getByDate(Date date) {
        String key = dateFormat.format(date);
        return loadAll().getOrDefault(key, "");
    }

    /**
     * Возвращает множество всех дат, для которых есть записи.
     *
     * @return множество строковых дат
     */
    public Set<String> getAllDates() {
        return new HashSet<>(loadAll().keySet());
    }

    /**
     * Загружает все записи в виде карты "Date → текст".
     *
     * @return карта всех записей с объектами Date
     */
    public Map<Date, String> getAllAsDates() {
        Map<Date, String> result = new TreeMap<>();
        Map<String, String> all = loadAll();
        for (Map.Entry<String, String> entry : all.entrySet()) {
            try {
                Date d = dateFormat.parse(entry.getKey());
                result.put(d, entry.getValue());
            } catch (Exception ignored) {}
        }
        return result;
    }

    /**
     * Возвращает записи за указанный период.
     *
     * @param from начало периода (включительно)
     * @param to конец периода (включительно)
     * @return карта записей в пределах периода
     */
    public Map<Date, String> getByPeriod(Date from, Date to) {
        Map<Date, String> all = getAllAsDates();
        Map<Date, String> result = new LinkedHashMap<>();
        for (Map.Entry<Date, String> entry : all.entrySet()) {
            Date d = entry.getKey();
            if (!d.before(from) && !d.after(to)) {
                result.put(d, entry.getValue());
            }
        }
        return result;
    }

    /**
     * Возвращает ближайшую будущую дату с записью,
     * начиная от указанной даты.
     *
     * @param from дата отсчёта
     * @return ближайшая запись или null, если её нет
     */
    public Map.Entry<Date, String> getNearestFutureDate(Date from) {
        Map<Date, String> all = getAllAsDates();
        for (Map.Entry<Date, String> entry : all.entrySet()) {
            if (!entry.getKey().before(from)) {
                return entry; // TreeMap гарантирует ближайшую
            }
        }
        return null;
    }
}
