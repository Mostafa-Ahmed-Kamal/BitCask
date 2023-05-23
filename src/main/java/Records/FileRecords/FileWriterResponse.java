package Records.FileRecords;

public class FileWriterResponse{
    private final String filePath;
    private final int valuePosition;
    public FileWriterResponse(String filePath, int valuePosition){
        this.filePath = filePath;
        this.valuePosition = valuePosition;
    }
    public String filePath() {
        return filePath;
    }
    public int valuePosition() {
        return valuePosition;
    }
}
