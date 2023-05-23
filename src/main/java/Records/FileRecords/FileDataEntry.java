package Records.FileRecords;

import java.io.IOException;
import java.nio.ByteBuffer;

public record FileDataEntry(long timeStamp, int keySize, int valueSize, String key, String value){
    public byte[] getBytes() throws IOException {
        int recordSize = Long.BYTES + Integer.BYTES * 2 + keySize + valueSize;
        ByteBuffer byteBuffer = ByteBuffer.allocate(recordSize);
        byteBuffer.putLong(timeStamp).putInt(keySize).putInt(valueSize).put(key.getBytes()).put(value.getBytes());
        return byteBuffer.array();
    }
}
