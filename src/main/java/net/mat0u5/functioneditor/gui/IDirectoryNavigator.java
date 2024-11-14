package net.mat0u5.functioneditor.gui;

import net.mat0u5.functioneditor.files.ClientFile;

import java.util.concurrent.CompletableFuture;

public interface IDirectoryNavigator {
    ClientFile getCurrentDirectory();

    CompletableFuture<Void> switchToDirectory(ClientFile dir);

    CompletableFuture<Void> switchToParentDirectory();

    CompletableFuture<Void> switchToRootDirectory();
}
