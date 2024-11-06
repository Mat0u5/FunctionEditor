package net.mat0u5.functioneditor.gui;

import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;
import fi.dy.masa.malilib.util.InfoUtils;
import net.mat0u5.functioneditor.files.ClientFile;
import org.jetbrains.annotations.Nullable;

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
            //TODO - send packet to server
            /*
            File file = new File(this.dir, string);
            if (file.exists()) {
                InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.error.file_or_directory_already_exists", new Object[]{file.getAbsolutePath()});
                return false;
            } else if (!file.mkdirs()) {
                InfoUtils.showGuiOrActionBarMessage(MessageType.ERROR, "malilib.error.failed_to_create_directory", new Object[]{file.getAbsolutePath()});
                return false;
            } else {
                if (this.navigator != null) {
                    this.navigator.switchToDirectory(file);
                }

                InfoUtils.showGuiOrActionBarMessage(MessageType.SUCCESS, "malilib.message.directory_created", new Object[]{string});
            }
            */
            return true;
        }
    }
}