import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class provides static method to get data.
 * @author Evgeniy Smirnov
 */
public class DataLoader {

    private static final Logger logger = LogManager.getLogger(DataLoader.class);

    static byte[] getData(String inputFile) {
        return DataLoader.downloadData(inputFile);
    }

    private static byte[] downloadData(String inputFile) {


        String cleanedLine;

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
            System.exit(1);
        }
        return new byte[0];
    }
}
