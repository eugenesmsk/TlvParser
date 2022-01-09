import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;

class Main {
    public static void main(String[] args) {

        for(String arg : args) {
            System.out.printf("------Begin %s--------", arg);
            byte[] data = DataLoader.getData(arg);
            TlvParser tlvParser = new TlvParser();
            tlvParser.getParseResult(data);
            System.out.println("--------End file---------\n\n");
        }
    }
}