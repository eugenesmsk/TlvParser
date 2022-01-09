import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

/**
 * This class provides conversion from bytes list to hex and fro hex string to byte array/
 * @author Evgeniy Smirnov
 */
public class Converter {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final Logger logger = LogManager.getLogger(DataLoader.class);

    /**
     * Static method which converts bytes list (<code>List<Byte></code>) to hex-string representation
     * @param bytes data which needs to convert
     * @return      string of conveted data
     */
    public static String bytesToHex(List<Byte> bytes) {
        try {
            char[] hexChars = new char[bytes.size() * 2];
            for (int j = 0; j < bytes.size(); j++) {
                int v = bytes.get(j) & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars);
        } catch (NullPointerException e) {
            logger.error("Input List<Byte> in bytesToHex() is null");
            System.exit(1);
        }
        return "";
    }

    /**
     * Static method which converts hex-string to byte array.
     * @param s input hex string
     * @return  byte array of string representation
     */
    public static byte[] hexStringToByteArray(String s) {
        try {
            try {
                if (s.length() % 2 != 0) throw new StringIndexOutOfBoundsException();
                else {
                    int len = s.length();
                    byte[] data = new byte[len / 2];
                    for (int i = 0; i < len; i += 2) {
                        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                + Character.digit(s.charAt(i + 1), 16));
                    }
                    return data;
                }
            } catch (StringIndexOutOfBoundsException e) {
                logger.error("Odd number of characters in the hex line " +
                        "of the string (hexStringToByteArray): {}", s.length());
                System.exit(1);

            }
        } catch (NullPointerException ex) {
            logger.error("Input string in hexStringToByteArray() is null");
            System.exit(1);
        }
        return new byte[0];
    }
}
