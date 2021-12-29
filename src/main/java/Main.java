import java.io.IOException;

class Main {
    public static void main(String[] args) throws IOException {
        byte[] data = DataLoader.getData();
        TlvParser.parse(data);
        System.out.println();
    }
}