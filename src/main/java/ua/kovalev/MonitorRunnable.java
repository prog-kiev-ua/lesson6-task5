package ua.kovalev;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class MonitorRunnable implements Runnable {
    private File folderMonitor;
    private long timeOut;
    private Logger logger;
    private FileWrapper [] scanFiles;

    public MonitorRunnable(File folderMonitor, Logger logger, long timeOut) {
        this.folderMonitor = folderMonitor;
        this.timeOut = timeOut;
        this.logger = logger;
    }

    @Override
    public void run() {
        if (!folderMonitor.exists() || folderMonitor.isFile()) {
            System.out.println("Не верно указан каталог для мониторинга");
        }

        Comparator sortFilesComparator = (o1, o2) -> ((FileWrapper) o1).getName().compareTo(((FileWrapper) o2).getName());

        scanFiles = getArrayFileWrappers(folderMonitor.listFiles());

        Arrays.sort(scanFiles, sortFilesComparator);

        try {
            Thread.sleep(timeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            FileWrapper [] newScanFiles = getArrayFileWrappers(folderMonitor.listFiles());
            Arrays.sort(newScanFiles, sortFilesComparator);

            FileWrapper[] newFiles = new FileWrapper[newScanFiles.length];
            FileWrapper[] deletedFiles = new FileWrapper[scanFiles.length];

            int delInd = 0;
            int newInd = 0;

            for (int i = 0; i < scanFiles.length || i < newScanFiles.length; i++) {
                if (i >= scanFiles.length) {
                    newFiles[newInd++] = newScanFiles[i];
                    continue;
                }

                if (i >= newScanFiles.length) {
                    deletedFiles[delInd++] = scanFiles[i];
                    continue;
                }

                if (scanFiles[i].getName().equals(newScanFiles[i].getName())) {
                    if (scanFiles[i].getLength() != newScanFiles[i].getLength())
                        logger.log(String.format("файл [%s] был изменён", scanFiles[i].getName()));
                    continue;
                }

                deletedFiles[delInd++] = scanFiles[i];
                newFiles[newInd++] = newScanFiles[i];
            }

            if (deletedFiles.length > 0) {
                for (int i = 0; i < deletedFiles.length; i++) {
                    if (deletedFiles[i] == null) break;
                    switch (checkFile(newScanFiles, deletedFiles[i])) {
                        case DIFFERENT:
                            logger.log(String.format("файл [%s] был изменён", deletedFiles[i].getName()));
                            break;
                        case ABSENT:
                            logger.log(String.format("файл [%s] был удалён", deletedFiles[i].getName()));
                            break;
                    }
                }
            }

            if (newFiles.length > 0) {
                for (int i = 0; i < newFiles.length; i++) {
                    if (newFiles[i] == null) break;
                    switch (checkFile(scanFiles, newFiles[i])) {
                        case DIFFERENT:
                            logger.log(String.format("файл [%s] был изменён", newFiles[i].getName()));
                            break;
                        case ABSENT:
                            logger.log(String.format("файл [%s] был добавлен", newFiles[i].getName()));
                            break;
                    }
                }
            }

            scanFiles = newScanFiles;

            try {
                Thread.sleep(timeOut);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Result checkFile(FileWrapper[] array, FileWrapper file) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].getName().equals(file.getName())) {
                if (array[i].getLength() != file.getLength()) {
                    return Result.DIFFERENT;
                }
                return Result.SAME;
            }
        }
        return Result.ABSENT;
    }

    private FileWrapper[] getArrayFileWrappers(File[] files){
        FileWrapper [] fileWrappers = new FileWrapper[files.length];
        for (int i = 0; i < files.length; i++) {
            fileWrappers[i] = new FileWrapper(files[i].getName(), files[i].length());
        }
        return fileWrappers;
    }
}