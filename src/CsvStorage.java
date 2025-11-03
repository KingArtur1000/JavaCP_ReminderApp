import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CsvStorage {
    private final String fileName;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public CsvStorage(String fileName) {
        this.fileName = fileName;
        ensureCsvExists();
    }

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

    // Сохранение или удаление записи
    public void save(Date date, String text) {
        String dateStr = dateFormat.format(date);
        Map<String, String> all = loadAll();

        if (text == null || text.isEmpty()) {
            all.remove(dateStr);
        } else {
            // экранируем переносы строк
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

    public Map<String, String> loadAll() {
        Map<String, String> data = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    // при чтении восстанавливаем переносы строк
                    String restored = parts[1].replace("\\n", "\n");
                    data.put(parts[0], restored);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


    public String getByDate(Date date) {
        String key = dateFormat.format(date);
        return loadAll().getOrDefault(key, "");
    }

    public Set<String> getAllDates() {
        return new HashSet<>(loadAll().keySet());
    }


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
