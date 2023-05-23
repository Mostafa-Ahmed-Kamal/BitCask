package Records.MapRecords;

public class KeyValueMetaData{
    private final String filePath;
    private final int valuePosition,valueSize;
    private final long timeStamp;

    public KeyValueMetaData(String filePath, int valuePosition, int valueSize, long timeStamp) {
        this.filePath = filePath;
        this.valuePosition = valuePosition;
        this.valueSize = valueSize;
        this.timeStamp = timeStamp;
    }
    public String filePath() {
        return filePath;
    }
    public int valuePosition() {
        return valuePosition;
    }
    public int valueSize() {
        return valueSize;
    }
    public long timeStamp() {
        return timeStamp;
    }
}

