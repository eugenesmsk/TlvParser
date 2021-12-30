import java.util.List;

/**
 * This class is forming string of parsed result and print it.
 * @author Evgeniy Smirnov
 */
public class Printer {

    /**
     * Creates StringBuilde object, fill and print it.
     * @param treeTopNode main TlvObject - top of tree
     */
    public static void getResultString(TlvObject treeTopNode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        Printer.formString(stringBuilder, treeTopNode);
        System.out.println(stringBuilder);
    }

    /**
     * Recursive method which is forming string. It goes through the whole tree
     * and adds data to the StringBuilder
     * @param stringBuilder formed string
     * @param treeNode      current TlvObject
     */
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

    /**
     * Define string representation of TLV class
     * @param classOfTag    byte data of class
     * @return              string representation of TLV class
     */
    private static String defineStringClass(byte classOfTag) {
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

    /**
     * Define string representation of TLV tag
     * @param typeOfTag byte data of tag
     * @return          string representation of TLV tag
     */
    private static String defineTagType(byte typeOfTag) {
        if (typeOfTag == 0) {
            return "P";
        } else if (typeOfTag == 1) {
            return "C";
        } else {
            throw new IllegalArgumentException("Wrong type of tag");
        }
    }

    /**
     * Returns string for tag string which contains class, type (kind) and tag.
     * @param tlvObject current TlvObject
     * @return          string which contains class, type (kind) and tag
     */
    private static String getStringTag(TlvObject tlvObject) {
        String classOfTag = defineStringClass(tlvObject.getClassOfTag());
        String typeOfTag = defineTagType(tlvObject.getType());
        String hexTag = Converter.bytesToHex(tlvObject.getTagBytesList());
        int id = tlvObject.getIdentifier();
        return String.format("Tag (class: %s, kind: %s, id: %d) [%s]\n", classOfTag, typeOfTag, id, hexTag);
    }

    /**
     * Returns string for length string which may be definite of indefinite
     * @param tlvObject current TlvObject
     * @return          prepared string
     */
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

    /**
     * Returns string for value which contains number of nested on
     * next level tags (for constructed type) or hex data (in case primitive type)
     * @param tlvObject current tlvObject
     * @return          prepared string
     */
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
