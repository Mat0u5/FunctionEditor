package net.mat0u5.functioneditor.files;

public class DataManagerClient {
    public static ClientFile ROOT = null;
    public static boolean hasRootDirectory() {
        return ROOT != null;
    }
    public static void setClientRootDirectory(ClientFile file) {
        System.out.println("Setting client root dir");
        ROOT = file;
    }
    public static ClientFile getClientRootDirectory() {
        System.out.println("Getting client root dir");
        return ROOT;
    }
    public static ClientFile getDatapackBaseDirectory() {
        return getClientRootDirectory();
    }
}
