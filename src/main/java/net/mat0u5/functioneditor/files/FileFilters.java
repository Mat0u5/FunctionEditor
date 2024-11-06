package net.mat0u5.functioneditor.files;

import java.io.File;
import java.io.FileFilter;

public class FileFilters {
    public static final FileFilter FILE_FILTER_SUPPORTED = new FileFilterSupported();
    public static final FileFilter FILE_FILTER_DIRECTORIES = new FileFilterDirectories();
    private static class FileFilterDirectories implements FileFilter {
        @Override
        public boolean accept(File pathName)
        {
            return pathName.isDirectory() && !pathName.getName().startsWith(".");
        }
    }
    private static class FileFilterSupported implements FileFilter {
        @Override
        public boolean accept(File pathName)
        {
            String name = pathName.getName();
            return  name.endsWith(".mcfunction") ||
                    name.endsWith(".zip") ||
                    name.endsWith(".json") ||
                    name.endsWith(".mcmeta");
        }
    }
}
