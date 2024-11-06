package net.mat0u5.functioneditor.files;

import fi.dy.masa.malilib.gui.interfaces.IDirectoryCache;

import java.io.File;

public class DataManager implements IDirectoryCache {
    private static final DataManager INSTANCE = new DataManager();
    public static File getRootDirectory() {
        try {
            File file = new File(".").getCanonicalFile();
            return file;
        }catch(Exception e) {
            return new File(".");
        }
    }
    public static ClientFile getClientRootDirectory() {
        return new ClientFile(getRootDirectory());
    }
    public static ClientFile getDatapackBaseDirectory() {
        return getClientRootDirectory();
    }
    public static IDirectoryCache getDirectoryCache() {
        return INSTANCE;
    }

    @Override
    public File getCurrentDirectoryForContext(String s) {
        return null;
    }

    @Override
    public void setCurrentDirectoryForContext(String s, File file) {

    }
}
