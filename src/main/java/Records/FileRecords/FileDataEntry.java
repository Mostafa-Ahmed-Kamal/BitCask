package Records.FileRecords;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FileDataEntry{
    private final long timeStamp;
    private final int keySize, valueSize;
    private final String key, value;
    public FileDataEntry(long timeStamp, int keySize, int valueSize, String key, String value){
        this.timeStamp = timeStamp;
        this.keySize = keySize;
        this.valueSize = valueSize;
        this.key = key;
        this.value = value;
    }
    public byte[] getBytes() {
        int recordSize = Long.BYTES + Integer.BYTES * 2 + keySize + valueSize;
        ByteBuffer byteBuffer = ByteBuffer.allocate(recordSize);
        byteBuffer.putLong(timeStamp).putInt(keySize).putInt(valueSize).put(key.getBytes()).put(value.getBytes());
        return byteBuffer.array();
    }
    public long timeStamp() {
        return timeStamp;
    }
    public int keySize() {
        return keySize;
    }
    public int valueSize() {
        return valueSize;
    }
    public String key() {
        return key;
    }
    public String value() {
        return value;
    }
}
