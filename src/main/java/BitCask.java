import FileManager.FileManager;
import FileManager.FileReader;
import FileManager.FileWriter;
import Records.FileRecords.FileDataEntry;
import Records.FileRecords.FileWriterResponse;
import Records.FileRecords.HintFileRecord;
import Records.MapRecords.KeyValueMetaData;
import SystemConfigs.EnvironmentVariables;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BitCask {
    private final FileWriter fileWriter;
    private final FileReader fileReader;
    private HashMap<String, KeyValueMetaData> keyDirectory;
    private final HashMap<String,String> cache;
    private final String workingDirectory;
    private final long mergeInterval;
    public BitCask() throws Exception {
        EnvironmentVariables environmentVariables = EnvironmentVariables.getEnvironmentVariablesInstance();
        workingDirectory = environmentVariables.FILES_DIRECTORY;
        mergeInterval = environmentVariables.MERGE_INTERVAL;
        FileManager.createDirectory(workingDirectory);
        keyDirectory = new HashMap<>();
        fileReader = new FileReader();
        cache = new HashMap<>();
        build();
        fileWriter = new FileWriter(workingDirectory);
        scheduleMergeOperation(mergeInterval);
    }
    private void scheduleMergeOperation(long mergeInterval){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                this.merge();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }, mergeInterval, mergeInterval, TimeUnit.SECONDS);
    }
    private String get(String key, HashMap<String, KeyValueMetaData> keyDirectoryMap) throws Exception {
        if (!keyDirectoryMap.containsKey(key)){
            throw new Exception("Key does not exist");
        }
        KeyValueMetaData metaData = keyDirectoryMap.get(key);
        byte[] valueBytes = FileManager.readRandomAccessedBytes(metaData.filePath(), metaData.valuePosition(), metaData.valueSize());
        return new String(valueBytes);
    }
    public String get(String key) throws Exception {
        if (cache.containsKey(key))return cache.get(key);
        String value = get(key,keyDirectory);
        cache.put(key,value);
        return value;
    }
    public void put(String key, String value) throws IOException {
        cache.remove(key);
        FileDataEntry data = new FileDataEntry(
                System.currentTimeMillis(), key.getBytes().length, value.getBytes().length, key, value);
        FileWriterResponse response = fileWriter.writeRecordToActiveFile(data);
        KeyValueMetaData metaData = new KeyValueMetaData(
                response.filePath(), response.valuePosition(), data.valueSize(), data.timeStamp());
        keyDirectory.put(key,metaData);
    }
    private void build() throws Exception {
        recoverFromFailure();
        keyDirectory.clear();
        File directory = new File(workingDirectory);
        File[] files = FileManager.sortFilesByTime(directory.listFiles(),false);
        assert files != null;
        processFiles(Arrays.stream(files).toList(),keyDirectory);
    }
    private void processFiles(List<File> files, HashMap<String, KeyValueMetaData> keyDirectoryMap) throws IOException {
        List<File> hintFiles = files.stream().filter(file -> file.getName().endsWith(".hint")).toList();
        List<File> dataFiles = files.stream().filter(file -> file.getName().endsWith(".data")).toList();
        for (File dataFile:dataFiles){
            String fileName = dataFile.getName().split("\\.")[0];
            Optional<File> hintFile = hintFiles.stream().filter(file -> file.getName().split("\\.")[0].equals(fileName)).findFirst();
            if (hintFile.isPresent()){
                fileReader.processHintFilesToKeyMap(hintFile.get(),keyDirectoryMap);
            }
            else{
                fileReader.processDataFilesToKeyMap(dataFile,keyDirectoryMap);
            }
        }
    }
    private void recoverFromFailure() throws Exception {
        File directory = new File(workingDirectory);
        File[] files = directory.listFiles();
        assert files != null;
        List<File> corruptedFiles = Arrays.stream(files).filter(file -> file.getName().endsWith("#")).toList();
        for (File corruptedFile:corruptedFiles){
            if(!corruptedFile.delete())
                throw new Exception("Couldn't perform deletion on a corrupted file");
        }
    }
    public void merge() throws Exception {
        File directory = new File(workingDirectory);
        File[] files = FileManager.sortFilesByTime(directory.listFiles(),false);
        List<File> filesToMerge = Arrays.stream(files).filter(file->!file.getPath().equals(fileWriter.activeFile.getPath())).toList();
        if (filesToMerge==null || filesToMerge.size()<2)return;
        HashMap<String, KeyValueMetaData> keyDirectoryReplica = new HashMap<>();
        processFiles(filesToMerge,keyDirectoryReplica);
        File mostRecentMergedFile = filesToMerge.get(0);
        String mergeFilePath = mostRecentMergedFile.getParent()+"/"+
                (Long.parseLong(mostRecentMergedFile.getName().split("\\.")[0])+1);
        File mergeFile = new File(mergeFilePath+".data#");
        File hintFile = new File(mergeFilePath+".hint#");
        mergeFile.createNewFile();
        hintFile.createNewFile();
        for (Map.Entry<String, KeyValueMetaData> entry:keyDirectoryReplica.entrySet()){
            KeyValueMetaData metaData = entry.getValue();
            String value = get(entry.getKey(),keyDirectoryReplica);
            FileDataEntry dataRecord = new FileDataEntry(
                    metaData.timeStamp(), //timestamp
                    entry.getKey().getBytes().length, //keySize
                    value.getBytes().length, //valueSize
                    entry.getKey(), //key
                    value //value
            );
            FileWriterResponse response = fileWriter.writeDataRecord(dataRecord,mergeFile);
            HintFileRecord hintRecord = new HintFileRecord(
                    metaData.timeStamp(),
                    entry.getKey().getBytes().length, //keySize
                    value.getBytes().length, //valueSize
                    response.valuePosition(),
                    entry.getKey()
                    );
            fileWriter.writeHintRecord(hintRecord,hintFile);
        }
        File mergeFileRename = new File(mergeFile.getPath().replace("#",""));
        File hintFileRename = new File(hintFile.getPath().replace("#",""));
        boolean renameMergeFile = mergeFile.renameTo(mergeFileRename);
        boolean renameHintFile = hintFile.renameTo(hintFileRename);
        if (!renameMergeFile || ! renameHintFile){
            throw new Exception("Couldn't perform merge due to file renaming issue");
        }
        keyDirectoryReplica.clear();
        fileReader.processDataFilesToKeyMap(fileWriter.activeFile,keyDirectoryReplica);
        fileReader.processHintFilesToKeyMap(hintFileRename,keyDirectoryReplica);
        keyDirectory = keyDirectoryReplica;
        for (File file : filesToMerge){
            file.delete();
        }
    }

//    public static void main(String[] args) throws Exception {
//        BitCask bitCask = new BitCask();
//        Random random = new Random();
////        while(true){
////            bitCask.put("randomgiberish",""+random.nextInt(1234516));
////            Thread.sleep(100);
////            System.out.println(bitCask.get("randomgiberish"));
////        }
////        bitCask.put("hamada","1");
////        bitCask.put("h","2");
////        bitCask.put("test","3");
////        bitCask.put("ttt","4");
////        bitCask.put("new","5");
//        bitCask.merge();
//        System.out.println(bitCask.get("hamada"));
//        System.out.println(bitCask.get("h"));
//        System.out.println(bitCask.get("test"));
//        System.out.println(bitCask.get("ttt"));
//        System.out.println(bitCask.get("new"));
//    }
}
