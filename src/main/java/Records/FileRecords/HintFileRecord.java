package Records.FileRecords;

import java.io.IOException;
import java.nio.ByteBuffer;

public class HintFileRecord{
    private final long timeStamp;
    private final int keySize, valueSize, valuePosition;
    private final String key;
    public HintFileRecord(long timeStamp, int keySize, int valueSize, int valuePosition, String key){
        this.timeStamp = timeStamp;
        this.keySize = keySize;
        this.valueSize = valueSize;
        this.valuePosition = valuePosition;
        this.key = key;
    }
    public byte[] getBytes() {
        int recordSize = Long.BYTES + Integer.BYTES * 3 + keySize;
        ByteBuffer byteBuffer = ByteBuffer.allocate(recordSize);
        byteBuffer.putLong(timeStamp).putInt(keySize).putInt(valueSize).putInt(valuePosition).put(key.getBytes());
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
    public int valuePosition() {
        return valuePosition;
    }
    public String key() {
        return key;
    }
}
