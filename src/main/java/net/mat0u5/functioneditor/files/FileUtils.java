package net.mat0u5.functioneditor.files;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static String getJoinedTrailingPathElements(ClientFile file, ClientFile rootPath, int maxStringLength, String separator) {
        String path = "";
        if (maxStringLength <= 0) {
            return "...";
        } else {
            while(file != null) {
                String name = file.getName();
                if (!path.isEmpty()) {
                    path = name + separator + path;
                } else {
                    path = name;
                }

                int len = path.length();
                if (len > maxStringLength) {
                    String var10000 = path.substring(len - maxStringLength, len);
                    path = "... " + var10000;
                    break;
                }

                if (file.equals(rootPath)) {
                    break;
                }

                file = file.getParentFile();
            }

            return path;
        }
    }

    public static ClientFile getCanonicalFileIfPossible(ClientFile file) {
        try {
            ClientFile fileCan = file.getCanonicalFile();
            if (fileCan != null) {
                file = fileCan;
            }
        } catch (IOException var2) {
        }

        return file;
    }
    public static String getNameWithoutExtension(String name) {
        int i = name.lastIndexOf(".");
        return i != -1 ? name.substring(0, i) : name;
    }
}