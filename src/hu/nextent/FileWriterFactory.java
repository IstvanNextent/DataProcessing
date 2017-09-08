package hu.nextent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileWriterFactory {

    private static final Map<String, UniqeFileWriter> fileWriters = new HashMap<>();

    public static UniqeFileWriter getFileWriterByName(String name) {
        UniqeFileWriter currentFile = fileWriters.get(name);
        if (currentFile == null) {
            try {
                currentFile = new UniqeFileWriter(name);
                fileWriters.put(name, currentFile);
            } catch (IOException e) {
                System.out.println("Error while creating new file! " + e.getStackTrace());
            }
        }
        return currentFile;
    }

    public static void closeAllFile() {
        for (String key : fileWriters.keySet()) {
            fileWriters.get(key).closeFileWriter();
        }
    }
}
