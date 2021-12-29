import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TlvParser{


    public String getParseResult(byte[] data) {
        TlvObject treeTopTlv = parse(data);
        return Printer.getResultString(treeTopTlv);
    }

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

    private void parseTlv(TlvObject parentTlv) {
        List<TlvObject> tlvObjects = new ArrayList<>();

        // Забираем данные из объекта
        List<Byte> dataList = parentTlv.getValue();
        byte[] data = new byte[dataList.size()];

        for (int i = 0; i < data.length; i++) {
            data[i] = dataList.get(i);
        }

        addOneLevelTlvs(parentTlv, data, tlvObjects);
        buildThree(tlvObjects);
    }

    private void buildThree(List<TlvObject> tlvObjects) {
        for (TlvObject obj : tlvObjects) {
            if (obj.getType() == 1) {
                parseTlv(obj);
            }
        }
    }

    private void addOneLevelTlvs(TlvObject parentTlv, byte[] data, List<TlvObject> tlvObjects) {
        int pointer = 0;
        while (pointer < data.length) {

            TlvObject tlvObject = createTlvObjectWithTag(data[pointer], new TlvObject());

            // Формирование идентификатора из нескольких байт
            if (tlvObject.getIdentifier() == (byte) 0x1F) {
                byte identifier = createSeveralBytesId(tlvObject, data, pointer);
                tlvObject.setIdentifier(identifier);
            }

            pointer += tlvObject.getTagBytesList().size();
            // Заполнение длины тега
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
                //Все данные, начиная с неопределенной длины
                byte[] dataOfIndefiniteObject = Arrays.copyOfRange(data, pointer, data.length);
                parseIndefinite(dataOfIndefiniteObject, tlvObject);
            }

            // Добавляем Tlv объект в список объектов
            tlvObjects.add(tlvObject);
            tlvObject.setTlvFather(parentTlv);
            parentTlv.addChild(tlvObject);


            pointer += tlvObject.getLength();

            if (!tlvObject.getTlvFather().isDefinite() && tlvObject.getType() == 0 && tlvObject.getLength() == 0) {
                break;
            }
        }
    }


    private void parseIndefinite(byte[] data, TlvObject parentTlv) {
        List<TlvObject> indefiniteObjectList = new ArrayList<>();

        addOneLevelTlvs(parentTlv, data, indefiniteObjectList);
        for (TlvObject object : indefiniteObjectList) {
            if (object.getType() == 1 && object.isDefinite() || !object.isDefinite() && object.getLength() == 0) {
                parseTlv(object);
            }
            parentTlv.setLength(parentTlv.getLength() + object.getLengthBytesList().size() + object.getTagBytesList().size() + object.getLength());
        }
    }


    private TlvObject createTlvObjectWithTag(byte oneTagByte, TlvObject tlvObject) {
        tlvObject.setClassOfTag((byte) ((oneTagByte & 0xC0) >> 6));
        tlvObject.addTagByte(oneTagByte);
        tlvObject.setType((byte) ((oneTagByte & 0x20) >> 5));
        tlvObject.setIdentifier((byte) (oneTagByte & 0x1F));
        return tlvObject;
    }


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

    private byte getMultipleByteIdentifier(byte hexByte) {
        return (byte) (hexByte & 0x7F);
    }


    private int getLengthFromData(TlvObject tlvObject, byte[] data, int pointer) {
        int length = 0;
        if (data[pointer] == (byte) 0x80) {
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
        return length;
    }


    private List<Byte> getValueFromData(TlvObject tlvObject, byte[] data, int pointer) {
        List<Byte> value = new ArrayList<>();
        for (int j = 0; j < tlvObject.getLength(); j++) {
            value.add(data[pointer + j]);
        }
        return value;
    }


    private int getSeveralBytesLength(TlvObject tlvObject, byte[] data, int pointer) {
        int numOfLenBytes = data[pointer] & 0x7F;
        pointer++;
        int length = 0;
        for (int j = pointer; j < pointer + numOfLenBytes; j++) {
            length = (length * 0x100) + (data[j] & 0xFF);
            tlvObject.addLengthByte(data[j]);
        }
        return length;
    }

}



