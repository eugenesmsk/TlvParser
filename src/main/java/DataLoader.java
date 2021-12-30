import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class provides static method to get data.
 * @author Evgeniy Smirnov
 */
public class DataLoader {

    private static final Logger logger = LogManager.getLogger(DataLoader.class);

    static byte[] getData() {
        return DataLoader.downloadData();
    }

    private static byte[] downloadData() {
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
            return Converter.hexStringToByteArray(cleanedLine);
        } catch (IOException e) {
            logger.error(e);
        }
        return new byte[0];
    }
}
