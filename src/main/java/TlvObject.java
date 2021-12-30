import java.util.ArrayList;
import java.util.List;

/**
 * Represent TlvObjects. After parsing data it has parent TlvObject and list of childs.
 * @author Evgeniy Smirnov
 */
public class TlvObject {

    private final List<Byte> tagBytesList;
    private final List<Byte> lengthBytesList;
    private final List<TlvObject> childs;
    private byte classOfTag;
    private byte type;
    private byte identifier;
    private int length;
    private boolean definite;
    private List<Byte> data;
    private int level;
    private TlvObject tlvFather;

    public TlvObject() {
        this.length = 0;
        this.tagBytesList = new ArrayList<>();
        this.lengthBytesList = new ArrayList<>();
        this.childs = new ArrayList<>();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void addChild(TlvObject child) {
        childs.add(child);
    }

    public List<TlvObject> getChilds() {
        return childs;
    }

    public void addLengthByte(byte lengthByte) {
        lengthBytesList.add(lengthByte);
    }

    public List<Byte> getLengthBytesList() {
        return lengthBytesList;
    }

    public void addTagByte(byte tagByte) {
        tagBytesList.add(tagByte);
    }

    public List<Byte> getTagBytesList() {
        return tagBytesList;
    }

    public TlvObject getTlvFather() {
        return tlvFather;
    }

    public void setTlvFather(TlvObject tlvFather) {
        this.tlvFather = tlvFather;
    }

    public byte getClassOfTag() {
        return classOfTag;
    }

    public void setClassOfTag(byte classEncoding) {
        this.classOfTag = classEncoding;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getIdentifier() {
        return identifier;
    }

    public void setIdentifier(byte identifier) {
        this.identifier = identifier;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public List<Byte> getValue() {
        return data;
    }

    public void setValue(List<Byte> value) {
        this.data = value;
    }

    public boolean isDefinite() {
        return definite;
    }

    public void setDefinite(boolean definite) {
        this.definite = definite;
    }
}
