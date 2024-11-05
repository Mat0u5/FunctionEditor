package net.mat0u5.functioneditor.gui;

import java.io.File;

/*
    Created by masa (https://github.com/maruohon/litematica),
    Modified by Mat0u5
*/
public enum FileType
{
    INVALID,
    UNKNOWN,
    JSON,
    MCFUNCTION,
    ZIP,
    MCMETA;


    public static FileType fromFile(File file) {
        if (file.isFile() && file.canRead())
        {
            String name = file.getName();
            return fromString(name);
        }
        return INVALID;
    }
    public static FileType fromString(String fileName) {

        if (fileName.endsWith(".mcfunction"))
        {
            return MCFUNCTION;
        }
        else if (fileName.endsWith(".json"))
        {
            return JSON;
        }
        else if (fileName.endsWith(".zip"))
        {
            return ZIP;
        }
        else if (fileName.endsWith(".mcmeta"))
        {
            return MCMETA;
        }
        return UNKNOWN;
    }
}

