package net.mat0u5.functioneditor.gui;

import java.io.File;
import java.io.FileFilter;

public class FileEditorFilter implements FileFilter
{
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
