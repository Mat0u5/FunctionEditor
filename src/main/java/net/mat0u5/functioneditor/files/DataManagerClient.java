package net.mat0u5.functioneditor.files;

public class DataManagerClient {
    public static ClientFile ROOT;
    public static ClientFile getClientRootDirectory() {
        return ROOT;
    }
    public static ClientFile getDatapackBaseDirectory() {
        return getClientRootDirectory();
    }
}
