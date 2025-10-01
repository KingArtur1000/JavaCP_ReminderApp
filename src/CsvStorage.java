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

    // Создание файла, если его нет
    private void ensureCsvExists() {
        File file = new File(fileName);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("date;text"); // заголовок
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Сохранение записи
    public void save(Date date, String text) {
        String dateStr = dateFormat.format(date);
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            writer.println(dateStr + ";" + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка всех записей в Map<дата, текст>
    public Map<String, String> loadAll() {
        Map<String, String> data = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            reader.readLine(); // пропускаем заголовок
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

    // Получение текста по дате
    public String getByDate(Date date) {
        String dateStr = dateFormat.format(date);
        Map<String, String> all = loadAll();
        return all.getOrDefault(dateStr, "");
    }
}
