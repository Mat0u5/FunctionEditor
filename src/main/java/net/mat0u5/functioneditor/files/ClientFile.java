package net.mat0u5.functioneditor.files;

import fi.dy.masa.malilib.gui.GuiBase;
import net.mat0u5.functioneditor.gui.GuiFileBrowser;
import net.mat0u5.functioneditor.network.NetworkHandlerClient;
import net.mat0u5.functioneditor.network.packets.FileDataPayload;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClientFile {
    private File tempFile;

    private String path;
    private String name;
    private boolean canRead;
    private boolean isFile;
    private boolean exists;
    private boolean isDirectory;
    private boolean isNull;

    public ClientFile(String path) {
        this.path = path;
        tempFile = new File(path).getAbsoluteFile();
    }
    public ClientFile(ClientFile dir, String file) {
        this.path = dir.getAbsolutePath()+"\\"+file;
        tempFile = new File(path);
    }
    public ClientFile(FileDataPayload fileDataPayload) {
        this.isNull = fileDataPayload == null;
        if (isNull) return;
        this.path = fileDataPayload.path();
        List<Boolean> boolList = fileDataPayload.fileInfo();
        this.name = fileDataPayload.name();
        this.canRead = boolList.get(0);
        this.isFile = boolList.get(1);
        this.exists = boolList.get(2);
        this.isDirectory = boolList.get(3);
        //TEMP
        //tempFile = new File(this.path);
    }
    /*
    public ClientFile(File file) {
        //TODO - Remove
        tempFile = file;
        this.isNull = file == null;
        if (isNull) return;
        this.path = file.getPath();
        this.name = file.getName();
        this.canRead = file.canRead();
        this.isFile = file.isFile();
        this.exists = file.exists();
        this.isDirectory = file.isDirectory();
    }*/

    public boolean isNull() {
        return isNull;
    }
    public String getAbsolutePath() {
        return path;
    }
    public String getName() {
        return name;
    }
    public boolean canRead() {
        return canRead;
    }
    public boolean isFile() {
        return isFile;
    }
    public boolean exists() {
        return exists;
    }
    public boolean isDirectory() {
        return isDirectory;
    }
    public CompletableFuture<ClientFile> getParentFile() {
        //return new ClientFile(tempFile.getParentFile());
        return NetworkHandlerClient.requestServerFileAsync("file_data_getparent",path);
    }
    public ClientFile getCanonicalFile() {
        //return new ClientFile(tempFile.getCanonicalFile());
        //ClientFile should already be a canonical file
        return this;
    }
    public CompletableFuture<List<ClientFile>> listFiles(String filter) {
        /*
        File[] listFiles = tempFile.listFiles(filter);
        ClientFile[] result = new ClientFile[listFiles.length];
        for (int i = 0; i < listFiles.length; i++) {
            result[i] = new ClientFile(listFiles[i]);
        }
        return result;
         */
        String type = filter.equalsIgnoreCase("dir") ? "file_list_dir" : "file_list_files";
        return NetworkHandlerClient.requestServerListFileAsync(type, path);
    }
}
