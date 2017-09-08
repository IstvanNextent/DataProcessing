package hu.nextent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static final String CONFIGURATION_FILE_PATH = "config.properties";
    public static final String INPUT_DIRECTORY_PATH = "input";

    private static final Map<String, String> filesAndRegexp = new HashMap<>();
    private static boolean isFinished = false;

    public static void main(String[] args) {

        final BlockingQueue<String> bq = new ArrayBlockingQueue<String>(1024);
        final ExecutorService executor = Executors.newFixedThreadPool(5);

        try {
            readConfigurationFile();

            Runnable consumer = () -> {
                try {
                    do {
                        String line = bq.take();
                        for (String key : filesAndRegexp.keySet()) {

                            Pattern pattern = Pattern.compile(filesAndRegexp.get(key));
                            Matcher matcher = pattern.matcher(line);

                            if (matcher.find()) {
                                BufferedWriter bw = FileWriterFactory.getFileWriterByName(key).getBufferedWriter();
                                bw.append(line);
                                bw.append('\n');
                                bw.flush();
                            }

                        }
                    } while (bq.size() > 0 || !isFinished);

                    executor.shutdownNow();

                    FileWriterFactory.closeAllFile();

                } catch (IOException | InterruptedException e) {
                    System.out.println(e.getStackTrace());
                }
            };
            executor.execute(consumer);

            readAllFilesAllLines(bq);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Running finished successfully!");
    }

    private static void readAllFilesAllLines(BlockingQueue<String> bq) {
        try {
            for (Path path : getAllFilesPath()) {
                BufferedReader br = new BufferedReader(new FileReader(path.toFile()));

                try {
                    String line = br.readLine();

                    while (line != null) {
                        bq.put(line);
                        line = br.readLine();
                    }
                } finally {
                    br.close();
                }
            }

        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        } catch (InterruptedException e) {
            assert false;
        }
        isFinished = true;
    }

    public static List<Path> getAllFilesPath() throws IOException {
        return Files.list(Paths.get(INPUT_DIRECTORY_PATH))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
    }

    public static void readConfigurationFile()  throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(CONFIGURATION_FILE_PATH));

        try {
            String line = br.readLine();

            while (line != null) {
                String[] splittedLine = line.split(":");

                filesAndRegexp.put(splittedLine[0], splittedLine[1]);

                line = br.readLine();
            }
        } finally {
            br.close();
        }

    }
}
