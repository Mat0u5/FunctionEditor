package net.mat0u5.functioneditor.utils;

import fi.dy.masa.malilib.gui.interfaces.IDirectoryCache;

import java.io.File;

public class DataManager implements IDirectoryCache {
    private static final DataManager INSTANCE = new DataManager();
    public static File getRootDirectory() {
        return new File(".");
    }
    public static File getSchematicsBaseDirectory() {
        return getRootDirectory();
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
