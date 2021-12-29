import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader {

    static byte[] getData() throws IOException {
        return DataLoader.downloadData();
    }

    private static byte[] downloadData() throws IOException {
        String cleanedLine;
        String inputFile = "C:\\Users\\eugen\\Desktop\\task\\data\\my.hex";
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            cleanedLine = builder.toString().replaceAll("[\\n\\t\\r\\s]", "");
            return hexStringToByteArray(cleanedLine);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        // Тут проверить, что четное количество
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
