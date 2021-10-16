package ua.kovalev;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File foldreLog = new File("LOGS");
        if(!foldreLog.exists()) foldreLog.mkdir();
        Logger logger = new Logger(new File("LOGS", "log.txt"));
        try {
            logger.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runnable monitor = new MonitorRunnable(new File("folder1"), logger, 1000);
        Thread thread = new Thread(monitor);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
