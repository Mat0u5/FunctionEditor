package net.mat0u5.functioneditor.gui;

import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;
import fi.dy.masa.malilib.util.InfoUtils;
import net.mat0u5.functioneditor.files.ClientFile;
import net.mat0u5.functioneditor.network.NetworkHandlerClient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DirectoryCreator implements IStringConsumerFeedback {
    protected final ClientFile dir;
    @Nullable
    protected final IDirectoryNavigator navigator;

    public DirectoryCreator(ClientFile dir, @Nullable IDirectoryNavigator navigator) {
        this.dir = dir;
        this.navigator = navigator;
    }

    public boolean setString(String string) {
        if (string.isEmpty()) {
            InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.error.invalid_directory", new Object[]{string});
            return false;
        } else {
            NetworkHandlerClient.requestServerFileAsync("create_dir", List.of(this.dir.getAbsolutePath(),string)).thenAccept(file -> {
                System.out.println("test");
                this.navigator.switchToDirectory(file);
            });
            return true;
        }
    }
}