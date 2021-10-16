package ua.kovalev;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class Logger {
    private PrintWriter printWriter;
    private File file;

    public Logger(File file) {
        this.file = file;
    }

    public void init() throws IOException{
        // создаю каталог если нет
        if (!file.exists()){
            file.createNewFile();
        }

        // открываю поток
        if(printWriter == null){
            printWriter = new PrintWriter(file);
        }
    }

    public void log(String text){
        printWriter.println(new Date() + ": " + text);
        printWriter.flush();
    }
}
