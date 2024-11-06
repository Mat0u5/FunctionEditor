package net.mat0u5.functioneditor.gui;

import net.mat0u5.functioneditor.files.ClientFile;

public interface IDirectoryNavigator {
    ClientFile getCurrentDirectory();

    void switchToDirectory(ClientFile dir);

    void switchToParentDirectory();

    void switchToRootDirectory();
}
