package FileManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class FileManager {
    public static void createDirectory(String path){
        File directory = new File(path);
        if (!directory.exists()){
            if(directory.mkdir())
                System.out.println("directory created under the name: "+path);
            else
                System.out.println("failed to create directory");
        }
    }
    public static File[] sortFilesByTime(File[] files, boolean ascendingOrder){
        Arrays.sort(files, (f1, f2)->{
            long l1 = Long.parseLong(f1.getName().split("\\.")[0]);
            long l2 = Long.parseLong(f2.getName().split("\\.")[0]);
            return (int) (ascendingOrder?(l1-l2):(l2-l1));
        });
        return files;
    }
    public static byte[] readRandomAccessedBytes(String fileName, int dataPosition, int dataSize) throws IOException {
        byte[] data = new byte[dataSize];
        RandomAccessFile randomAccessFile = new RandomAccessFile(new File(fileName),"r");
        randomAccessFile.seek(dataPosition);
        randomAccessFile.read(data,0,dataSize);
        randomAccessFile.close();
        return data;
    }
}
