package Records.FileRecords;

import java.io.IOException;
import java.nio.ByteBuffer;

public record HintFileRecord(long timeStamp, int keySize, int valueSize, int valuePosition, String key){
    public byte[] getBytes() throws IOException {
        int recordSize = Long.BYTES + Integer.BYTES * 3 + keySize;
        ByteBuffer byteBuffer = ByteBuffer.allocate(recordSize);
        byteBuffer.putLong(timeStamp).putInt(keySize).putInt(valueSize).putInt(valuePosition).put(key.getBytes());
        return byteBuffer.array();
    }
}
