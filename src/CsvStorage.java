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
            // если пусто — удаляем запись
            all.remove(dateStr);
        } else {
            // иначе обновляем/добавляем
            all.put(dateStr, text);
        }

        // Перезаписываем файл
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
                    data.put(parts[0], parts[1]);
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

}
