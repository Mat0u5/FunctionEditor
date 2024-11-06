package net.mat0u5.functioneditor.gui;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import net.mat0u5.functioneditor.files.ClientFile;
import net.mat0u5.functioneditor.files.FileType;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface IFileBrowserIconProvider {
    IGuiIcon getIconRoot();

    IGuiIcon getIconUp();

    IGuiIcon getIconCreateDirectory();

    IGuiIcon getIconSearch();

    IGuiIcon getIconDirectory();

    @Nullable
    IGuiIcon getIconForFile(File file);
    @Nullable
    IGuiIcon getIconForClientFile(ClientFile file);
    @Nullable
    IGuiIcon getIconForFileType(FileType fileType);
}
