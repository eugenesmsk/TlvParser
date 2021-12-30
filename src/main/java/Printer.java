import java.util.List;

public class Printer {

    public static void getResultString(TlvObject treeTopNode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        Printer.formString(stringBuilder, treeTopNode);
        System.out.println(stringBuilder);
    }

    private static void formString(StringBuilder stringBuilder, TlvObject treeNode) {
        for (int i = 0; i < treeNode.getChilds().size(); i++) {

            TlvObject child = treeNode.getChilds().get(i);
            int level = child.getLevel();

            stringBuilder.append(" ".repeat(Math.max(0, level * 2)))
                    .append("TLV #").append(i + 1).append("\n")
                    .append(" ".repeat(Math.max(0, level * 2)))
                    .append(getStringTag(child))
                    .append(" ".repeat(Math.max(0, level * 2)))
                    .append(getStringLength(child))
                    .append(" ".repeat(Math.max(0, level * 2)))
                    .append(getStringValue(child));

            if (child.getChilds().size() != 0) {
                formString(stringBuilder, child);
            }

        }
    }

    private static String defineStringClass(byte classOfTag) throws IllegalArgumentException {
        if (classOfTag == 0) {
            return "U";
        } else if (classOfTag == 1) {
            return "A";
        } else if (classOfTag == 2) {
            return "C";
        } else if (classOfTag == 3) {
            return "P";
        } else {
            throw new IllegalArgumentException("Wrong class of tag");
        }
    }

    private static String defineTagType(byte typeOfTag) {
        if (typeOfTag == 0) {
            return "P";
        } else if (typeOfTag == 1) {
            return "C";
        } else {
            throw new IllegalArgumentException("Wrong type of tag");
        }
    }

    private static String getStringTag(TlvObject tlvObject) {
        String classOfTag = defineStringClass(tlvObject.getClassOfTag());
        String typeOfTag = defineTagType(tlvObject.getType());
        String hexTag = Converter.bytesToHex(tlvObject.getTagBytesList());
        int id = tlvObject.getIdentifier();
        return String.format("Tag (class: %s, kind: %s, id: %d) [%s]\n", classOfTag, typeOfTag, id, hexTag);
    }

    private static String getStringLength(TlvObject tlvObject) {
        int length = tlvObject.getLength();
        List<Byte> lengthBytesList = tlvObject.getLengthBytesList();
        String hexLength = Converter.bytesToHex(lengthBytesList);
        if (tlvObject.isDefinite()) {
            return String.format("Length: %d [%s]\n", length, hexLength);
        } else {
            return String.format("Length: INDEFINITE [%s]\n", hexLength);
        }
    }

    private static String getStringValue(TlvObject tlvObject) {

        if (tlvObject.getChilds().size() == 0) {
            List<Byte> value = tlvObject.getValue();
            String hexValue = Converter.bytesToHex(value);
            return String.format("Value: [%s]\n", hexValue);
        } else {
            int childNum = tlvObject.getChilds().size();
            return String.format("Value: (%d TLVs)\n", childNum);
        }
    }
}
