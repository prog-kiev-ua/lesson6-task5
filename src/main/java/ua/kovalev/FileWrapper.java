package ua.kovalev;

public class FileWrapper {
    private String name;
    private long length;

    public FileWrapper() {
        super();
    }

    public FileWrapper(String name, long length) {
        super();
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
