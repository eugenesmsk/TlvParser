import java.util.ArrayList;
import java.util.List;

public class TlvObject {


    private List<Byte> tagBytesList;
    private List<Byte> lengthBytesList;


    private int currentLevelLength;


    private byte classEncoding;
    private byte type;
    private byte identifier;
    private int length;
    private boolean definite;
    private int level;
    private List<Byte> data;
    private List<TlvObject> childs;
    private TlvObject tlvFather;
    private boolean hasChild;

    public TlvObject() {
        this.length = 0;
        this.tagBytesList = new ArrayList<>();
        this.lengthBytesList = new ArrayList<>();
        this.childs = new ArrayList<>();
    }

    public void addChild(TlvObject child) {
        childs.add(child);
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

    public void setClassEncoding(byte classEncoding) {
        this.classEncoding = classEncoding;
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
