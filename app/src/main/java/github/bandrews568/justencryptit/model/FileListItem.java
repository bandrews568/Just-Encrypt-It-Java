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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileListItem that = (FileListItem) o;

        if (time != that.time) return false;
        if (size != that.size) return false;
        if (filename != null ? !filename.equals(that.filename) : that.filename != null)
            return false;
        return location != null ? location.equals(that.location) : that.location == null;
    }

    @Override
    public int hashCode() {
        int result = filename != null ? filename.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
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