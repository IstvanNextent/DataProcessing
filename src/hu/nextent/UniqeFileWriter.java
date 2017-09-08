package hu.nextent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UniqeFileWriter {

    private String fileName;
    private BufferedWriter bufferedWriter = null;

    public UniqeFileWriter(String fileName) throws IOException {
        this.fileName = fileName;

        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file, true);

        this.bufferedWriter = new BufferedWriter(fw);
    }

    public void closeFileWriter() {
        try {
            if( bufferedWriter !=null ) {
                bufferedWriter.close();
            }
        } catch(Exception ex){
            System.out.println("Error in closing the BufferedWriter" + ex);
        }
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
