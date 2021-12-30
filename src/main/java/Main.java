import java.io.IOException;

class Main {
    public static void main(String[] args) throws IOException {
        byte[] data = DataLoader.getData();
        TlvParser tlvParser = new TlvParser();
        tlvParser.getParseResult(data);
    }
}