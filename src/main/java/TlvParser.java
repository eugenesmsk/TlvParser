import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class processes the received data array and builds a tree of dependencies of TLV objects.
 * @author Evgeniy Smirnov
 */
public class TlvParser {

    private static final Logger logger = LogManager.getLogger(TlvParser.class);

    /**
     * Creates top of tree of dependencies of TLV objects.
     * Calls static method of Printer which print parsed data to System.out.
     *
     * @param data                          byte[] array to be parsed
     */
    public void getParseResult(byte[] data) {
        if (data.length == 0) {
            logger.error("Data array is empty. Check input file.");
            System.exit(1);
        }
        TlvObject treeTopTlv = parse(data);
        Printer.getResultString(treeTopTlv);
    }

    /**
     * Returns the tree top which have links to childs which already have links to their childs etc.
     * Start parsing zero-level received data. Adds the persed TLV objects of the zero level in the
     * list and in the for loop sends them to parse further if object in list have difinite length.
     * Indefinite length is parsing in <code>parseIndefinite(byte[] data, TlvObject parentTlv)</code>.
     * @param data  byte[] array to be parsed
     * @return      tree top TLV object
     */
    private TlvObject parse(byte[] data) {
        int level = 0;
        List<TlvObject> tlvObjects = new ArrayList<>();
        TlvObject mainTlvObject = new TlvObject();
        mainTlvObject.setLevel(level);
        addOneLevelTlvs(mainTlvObject, data, tlvObjects);

        for (TlvObject object : tlvObjects) {
            if (object.isDefinite()) {
                parseTlv(object);
            }
        }

        return mainTlvObject;
    }

    /**
     * Get value list (data) from parent TlvObject and fall on one
     * level to get list of TlvObjects of the next nesting level.
     * @param parentTlv     TlvObject which is received from zero nesting level
     */
    private void parseTlv(TlvObject parentTlv) {
        List<TlvObject> tlvObjects = new ArrayList<>();
        List<Byte> dataList = parentTlv.getValue();
        byte[] data = new byte[dataList.size()];

        for (int i = 0; i < data.length; i++) {
            data[i] = dataList.get(i);
        }

        addOneLevelTlvs(parentTlv, data, tlvObjects);
        buildThree(tlvObjects);
    }

    /**
     * Fall to next nesting level and calls <code>parseTlv(TlvObject parentTlv</code>
     * which get list of TlvObjects of the fall nesting level.
     * @param tlvObjects    List of TlvObjects which it got from <code>parseTlv(TlvObject parentTlv</code>
     */
    private void buildThree(List<TlvObject> tlvObjects) {
        for (TlvObject obj : tlvObjects) {
            if (obj.getType() == 1) {
                parseTlv(obj);
            }
        }
    }

    /**
     * Adds TlvObjects of one level, call methods for filling TlvObject's.
     * Adds relations between childs and parents. If Tlv object have indefinite
     * length, it calls <code>parseIndefinite(byte[] data, TlvObject parentTlv)</code>
     * @param parentTlv     parent TlvObject
     * @param data          data of current level
     * @param tlvObjects    link on tlvObjects list in which the objects are added at the current nesting level
     */
    private void addOneLevelTlvs(TlvObject parentTlv, byte[] data, List<TlvObject> tlvObjects) {
        int pointer = 0;
        while (pointer < data.length) {
            TlvObject tlvObject = createTlvObjectWithTag(data[pointer], new TlvObject());

            if (tlvObject.getIdentifier() == (byte) 0x1F) {
                try {
                    byte identifier = createSeveralBytesId(tlvObject, data, pointer);
                    tlvObject.setIdentifier(identifier);
                } catch (ArrayIndexOutOfBoundsException e) {
                    logger.error("Error while forming multiple-bytes identifier. There is not the next byte for forming" +
                            " multiple-bytes identifier");
                    System.exit(1);
                }
            }

            pointer += tlvObject.getTagBytesList().size();
            int length = getLengthFromData(tlvObject, data, pointer);
            tlvObject.setLength(length);
            pointer += tlvObject.getLengthBytesList().size();

            if (tlvObject.isDefinite()) {
                int level = parentTlv.getLevel() + 1;
                tlvObject.setLevel(level);
                List<Byte> value = getValueFromData(tlvObject, data, pointer);
                tlvObject.setValue(value);
            } else {
                int level = parentTlv.getLevel() + 1;
                tlvObject.setLevel(level);
                byte[] dataOfIndefiniteObject = Arrays.copyOfRange(data, pointer, data.length);
                parseIndefinite(dataOfIndefiniteObject, tlvObject);
            }

            tlvObjects.add(tlvObject);
            tlvObject.setTlvFather(parentTlv);
            parentTlv.addChild(tlvObject);

            pointer += tlvObject.getLength();

            if (!tlvObject.getTlvFather().isDefinite() && tlvObject.getType() == 0 && tlvObject.getLength() == 0) {
                break;
            }
        }
    }

    /**
     * Parsing of indefinite length TlvObject. Get data from first byte of indefinite object to the last byte,
     * fall to the lext level and add all TlvObjects to list. In case of meeting another indefinite TlvObject's
     * happens the same.
     * @param data      data which starts from first byte of indefinite object
     * @param parentTlv parent indefinite TlvObject which is parent to <code>indefiniteObjectList</code>
     */
    private void parseIndefinite(byte[] data, TlvObject parentTlv) {
        List<TlvObject> indefiniteObjectList = new ArrayList<>();
        addOneLevelTlvs(parentTlv, data, indefiniteObjectList);
        for (TlvObject object : indefiniteObjectList) {
            if (object.getType() == 1 && object.isDefinite() || !object.isDefinite() && object.getLength() == 0) {
                try {
                    parseTlv(object);
                } catch (NullPointerException e) {
                    logger.error("Error while parsing of indefinite length TLV: there is not childs at indefinite TLV");
                    System.exit(1);
                }
            }
            parentTlv.setLength(parentTlv.getLength() + object.getLengthBytesList().size()
                    + object.getTagBytesList().size() + object.getLength());
        }
        try {
            TlvObject lastObject = parentTlv.getChilds().get(parentTlv.getChilds().size() - 1);
            if (lastObject.getType() != 0 && lastObject.getLength() != 0) {
                logger.error("Error while parsing indefinite length TLV. No found final tag of TLV");
                System.exit(1);
            }
        } catch (IndexOutOfBoundsException e) {
            logger.error("Error while parsing of indefinite length TLV: there is not childs at indefinite TLV");
            System.exit(1);
        }
    }

    /**
     *
     * Get and parse data from one-byte tag and then save it to TlvObject.
     * @param oneTagByte tag byte
     * @param tlvObject  current TlvObject
     * @return           TlvObject with filled class, type and identifier.
     */
    private TlvObject createTlvObjectWithTag(byte oneTagByte, TlvObject tlvObject) {
        tlvObject.setClassOfTag((byte) ((oneTagByte & 0xC0) >> 6));
        tlvObject.addTagByte(oneTagByte);
        tlvObject.setType((byte) ((oneTagByte & 0x20) >> 5));
        tlvObject.setIdentifier((byte) (oneTagByte & 0x1F));
        return tlvObject;
    }

    /**
     * Returns identifier in case several-bytes id and adds every byte to <code>addTagByte</code> list.
     * In case of 8th bit of additional byte is not 0, current byte adds to the identifier.
     * @param tlvObject current TlvObject
     * @param data      current level data
     * @param pointer   offset of data list
     * @return          identifier of TlvObject
     */
    private byte createSeveralBytesId(TlvObject tlvObject, byte[] data, int pointer) {
        int numOfIdBytes = 1;
        pointer++;
        tlvObject.addTagByte(data[pointer]);
        byte additionalByte = getMultipleByteIdentifier(data[pointer]);
        numOfIdBytes++;
        while (((additionalByte & 0x80) >> 7) != 0) {
            pointer++;
            tlvObject.addTagByte(data[pointer]);
            numOfIdBytes++;
            additionalByte = getMultipleByteIdentifier(data[pointer]);
        }
        byte identifier = tlvObject.getIdentifier();
        for (int j = pointer - numOfIdBytes + 2; j < pointer + 1; j++) {
            identifier = (byte) (((identifier & 0x7F) * 0x100) + (data[j] & 0x7F));
        }
        return identifier;
    }

    /**
     * Returns additional byte of multiple-byte identifier from data list from 1 to 7 bit.
     * 8th bit is 0
     * @param hexByte   next identifier byte
     * @return          return 1-7 bit of current byte
     */
    private byte getMultipleByteIdentifier(byte hexByte) {
        return (byte) (hexByte & 0x7F);
    }

    /**
     * Returns length of TlvObject received from data and define length type of TlvObject:
     * defined or indefined.
     * @param tlvObject current TlvObject
     * @param data      current level data
     * @param pointer   offset of data list
     * @return          length of TlvObject
     */
    private int getLengthFromData(TlvObject tlvObject, byte[] data, int pointer) {
        int length = 0;
        try {
            if (data[pointer] == (byte) 0x80) {

                if (tlvObject.getType() == 0) {
                    logger.error("Indefinite length in Primitive TLVs with id: {}", tlvObject.getIdentifier());
                    System.exit(1);
                }
                tlvObject.addLengthByte(data[pointer]);
                tlvObject.setDefinite(false);
            } else {
                tlvObject.setDefinite(true);
                tlvObject.addLengthByte(data[pointer]);
                if ((data[pointer] & 0x80) >> 7 == 1) {
                    length = getSeveralBytesLength(tlvObject, data, pointer);
                } else if ((data[pointer] & 0x80) >> 7 == 0) {
                    length = (byte) (data[pointer] & 0x7F);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Wrong input data. Can't get tag length. {}", e.getMessage());
            System.exit(1);
        }

        return length;
    }

    /**
     * Returns value list of data of current tlvObject
     * @param tlvObject current TlvObject
     * @param data      current level data
     * @param pointer   offset of data list
     * @return          value list of data
     */
    private List<Byte> getValueFromData(TlvObject tlvObject, byte[] data, int pointer) {
        List<Byte> value = new ArrayList<>();
        for (int j = 0; j < tlvObject.getLength(); j++) {
            value.add(data[pointer + j]);
        }
        return value;
    }

    /**
     * Returns length which consist several bytes and adds this bytes to TlvObvect
     * @param tlvObject current TlvObject
     * @param data      current level data
     * @param pointer   offset of data list
     * @return          length
     */
    private int getSeveralBytesLength(TlvObject tlvObject, byte[] data, int pointer) {
        int numOfLenBytes = data[pointer] & 0x7F;
        pointer++;
        int length = 0;
        for (int j = pointer; j < pointer + numOfLenBytes; j++) {
            length = (length * 0x100) + (data[j] & 0xFF);
            tlvObject.addLengthByte(data[j]);
            if (j != pointer + numOfLenBytes - 1 && (data[j] & 0x80) >> 7 == 1) {
                logger.error("Wrong multiple byte length (not the last byte contains 8th bit with 0 value)." +
                        " Length string is {}", Converter.bytesToHex(tlvObject.getLengthBytesList()));
                System.exit(1);
            }
        }
        return length;
    }
}



