package FileManager;


import Records.MapRecords.KeyValueMetaData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

public class FileReader {
    public void processHintFilesToKeyMap(File file, HashMap<String, KeyValueMetaData> keyDirectoryMap) throws IOException {
        byte[] fileBytes = new byte[(int)file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(fileBytes);
        int readPointer = 0;
        while(readPointer<fileBytes.length){
            long timeStamp = ByteBuffer.wrap(fileBytes,readPointer,Long.BYTES).getLong();
            readPointer+=Long.BYTES;
            int keySize = ByteBuffer.wrap(fileBytes,readPointer,Integer.BYTES).getInt();
            readPointer+=Integer.BYTES;
            int valueSize = ByteBuffer.wrap(fileBytes,readPointer,Integer.BYTES).getInt();
            readPointer+=Integer.BYTES;
            int valuePosition = ByteBuffer.wrap(fileBytes,readPointer,Integer.BYTES).getInt();
            readPointer+=Integer.BYTES;
            String key = new String(Arrays.copyOfRange(fileBytes,readPointer,readPointer+keySize));
            readPointer+=keySize;
            if(!keyDirectoryMap.containsKey(key)){
                KeyValueMetaData metaData = new KeyValueMetaData(file.getPath().replace(".hint",".data"),valuePosition,valueSize,timeStamp);
                keyDirectoryMap.put(key,metaData);
            }
        }
        fileInputStream.close();
    }
    public void processDataFilesToKeyMap(File file, HashMap<String, KeyValueMetaData> keyDirectoryMap) throws IOException {
        byte[] fileBytes = new byte[(int)file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(fileBytes);
        int readPointer = 0;
        while(readPointer<fileBytes.length){
            long timeStamp = ByteBuffer.wrap(fileBytes,readPointer,Long.BYTES).getLong();
            readPointer+=Long.BYTES;
            int keySize = ByteBuffer.wrap(fileBytes,readPointer,Integer.BYTES).getInt();
            readPointer+=Integer.BYTES;
            int valueSize = ByteBuffer.wrap(fileBytes,readPointer,Integer.BYTES).getInt();
            readPointer+=Integer.BYTES;
            String key = new String(Arrays.copyOfRange(fileBytes,readPointer,readPointer+keySize));
            readPointer+=keySize;
            String value = new String(Arrays.copyOfRange(fileBytes,readPointer,readPointer+valueSize));
            int valuePosition = readPointer;
            readPointer+=valueSize;
            if(!keyDirectoryMap.containsKey(key) || keyDirectoryMap.get(key).filePath().equals(file.getPath())){
                KeyValueMetaData metaData = new KeyValueMetaData(file.getPath(),valuePosition,valueSize,timeStamp);
                keyDirectoryMap.put(key,metaData);
            }
        }
        fileInputStream.close();
    }
}
