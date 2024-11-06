package net.mat0u5.functioneditor.files;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

public class ClientFile {
    public static String path;
    public static File tempFile;

    public ClientFile(String path) {
        this.path = path;
        tempFile = new File(path);
    }
    public ClientFile(File file) {
        if (file == null) return;
        this.path = file.getPath();
        tempFile = file;
    }

    public ClientFile getParentFile() {
        return new ClientFile(tempFile.getParentFile());
    }
    public String getAbsolutePath() {
        //return path;
        return tempFile.getAbsolutePath();
    }
    public String getName() {
        return tempFile.getName();
    }
    public boolean canRead() {
        return tempFile.canRead();
    }
    public boolean isFile() {
        return tempFile.isFile();
    }
    public boolean exists() {
        return tempFile.exists();
    }
    public boolean isDirectory() {
        return tempFile.isDirectory();
    }
    public ClientFile getCanonicalFile() throws IOException {
        return new ClientFile(tempFile.getCanonicalFile());
    }
    public ClientFile[] listFiles(FileFilter filter) {
        File[] listFiles = tempFile.listFiles(filter);
        ClientFile[] result = new ClientFile[listFiles.length];
        for (int i = 0; i < listFiles.length; i++) {
            result[i] = new ClientFile(listFiles[i]);
        }
        return result;
    }
}
