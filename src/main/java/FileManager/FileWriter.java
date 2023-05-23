package FileManager;

import Records.FileRecords.FileDataEntry;
import Records.FileRecords.FileWriterResponse;
import Records.FileRecords.HintFileRecord;
import SystemConfigs.Variables;

import java.io.*;

public class FileWriter {
    public File activeFile;
    private final String workingDirectory;
    public FileWriter(String workingDirectory) throws IOException {
        this.workingDirectory = workingDirectory;
        setActiveFile();
        activeFile.createNewFile();
    }
    private void writeRecord(byte[] record, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file,true);
        fileOutputStream.write(record);
        fileOutputStream.close();
    }
    public FileWriterResponse writeDataRecord(FileDataEntry data, File file) throws IOException {
        byte[] record = data.getBytes();
        int relativeValuePosition = record.length - data.value().getBytes().length;
        int valuePosition = (int) file.length() + relativeValuePosition;
        writeRecord(record,file);
        return new FileWriterResponse(file.getPath(),valuePosition);
    }
    public void writeHintRecord(HintFileRecord data, File file) throws IOException {
        byte[] record = data.getBytes();
        writeRecord(record,file);
    }
    public FileWriterResponse writeRecordToActiveFile(FileDataEntry data) throws IOException {
        setActiveFile();
        return writeDataRecord(data,activeFile);
    }
    private void setActiveFile() throws IOException {
        if (activeFile==null || activeFile.length()>=Variables.MAX_FILE_SIZE)
            activeFile = new File(workingDirectory + "/" + System.currentTimeMillis() + ".data");
    }

}
