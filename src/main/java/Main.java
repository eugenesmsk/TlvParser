import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;

class Main {
    public static void main(String[] args) {

        System.out.println("Path to file with input data: ");
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        String inputFile = scanner.nextLine();

        byte[] data = DataLoader.getData(inputFile);

        TlvParser tlvParser = new TlvParser();
        tlvParser.getParseResult(data);
    }
}