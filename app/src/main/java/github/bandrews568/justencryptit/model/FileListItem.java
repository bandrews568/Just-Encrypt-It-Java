package github.bandrews568.justencryptit.model;


import androidx.annotation.NonNull;



public class FileListItem {
    private String filename,location;
    private long time;
    private long size;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @NonNull
    @Override
    public String toString() {
        return "FileListItem{" +
                "filename='" + filename + '\'' +
                ", location='" + location + '\'' +
                ", time=" + time +
                ", size=" + size +
                '}';
    }
}