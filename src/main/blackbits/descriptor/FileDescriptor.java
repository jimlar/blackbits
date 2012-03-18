package blackbits.descriptor;



public class FileDescriptor {
    private String[] path;
    private long length;

    public FileDescriptor(String[] path, long length) {
        this.path = path;
        this.length = length;
    }

    public String[] getPath() {
        return path;
    }

    public long getLength() {
        return length;
    }
}
