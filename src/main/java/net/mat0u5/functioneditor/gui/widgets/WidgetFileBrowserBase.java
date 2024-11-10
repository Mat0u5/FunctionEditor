package net.mat0u5.functioneditor.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.RenderUtils;

import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import net.mat0u5.functioneditor.files.*;
import net.mat0u5.functioneditor.gui.IDirectoryNavigator;
import net.mat0u5.functioneditor.gui.IFileBrowserIconProvider;
import net.mat0u5.functioneditor.gui.widgets.WidgetFileBrowserBase.Client_DirectoryEntry;
import net.mat0u5.functioneditor.gui.widgets.WidgetFileBrowserBase.Client_WidgetDirectoryEntry;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;

public class WidgetFileBrowserBase extends WidgetListBase<Client_DirectoryEntry, Client_WidgetDirectoryEntry> implements IDirectoryNavigator {
    protected static final FileFilter FILE_FILTER = FileFilters.FILE_FILTER_SUPPORTED;
    protected static final FileFilter DIRECTORY_FILTER = FileFilters.FILE_FILTER_DIRECTORIES;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected ClientFile currentDirectory;
    protected final String browserContext;
    protected final IFileBrowserIconProvider iconProvider;
    @Nullable
    protected WidgetDirectoryNavigation directoryNavigationWidget;

    public WidgetFileBrowserBase(int x, int y, int width, int height, String browserContext, ClientFile defaultDirectory, @Nullable ISelectionListener<Client_DirectoryEntry> selectionListener, IFileBrowserIconProvider iconProvider) {
        super(x, y, width, height, selectionListener);
        this.browserContext = browserContext;
        this.allowKeyboardNavigation = true;
        this.currentDirectory = defaultDirectory;
        this.iconProvider = iconProvider;

        this.setSize(width, height);
        this.updateDirectoryNavigationWidget();
    }

    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
        if (super.onKeyTyped(keyCode, scanCode, modifiers)) {
            return true;
        } else if ((keyCode == 259 || keyCode == 263) && !this.currentDirectoryIsRoot()) {
            this.switchToParentDirectory();
            return true;
        } else if ((keyCode == 262 || keyCode == 257) && this.getLastSelectedEntry() != null && ((Client_DirectoryEntry)this.getLastSelectedEntry()).getType() == Client_DirectoryEntryType.DIRECTORY) {
            this.switchToDirectory(
                    this.getLastSelectedEntry().getDirectory()
                    //new File(((Client_DirectoryEntry)this.getLastSelectedEntry()).getDirectory(), ((Client_DirectoryEntry)this.getLastSelectedEntry()).getName())
            );
            return true;
        } else {
            return false;
        }
    }

    public void drawContents(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawOutlinedBox(this.posX, this.posY, this.browserWidth, this.browserHeight, -1342177280, -6710887);
        super.drawContents(drawContext, mouseX, mouseY, partialTicks);
        this.drawAdditionalContents(mouseX, mouseY, drawContext);
    }

    protected void drawAdditionalContents(int mouseX, int mouseY, DrawContext drawContext) {
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.browserWidth = this.getBrowserWidthForTotalWidth(width);
        this.browserEntryWidth = this.browserWidth - 14;
    }

    protected int getBrowserWidthForTotalWidth(int width) {
        return width - 6;
    }

    protected void updateDirectoryNavigationWidget() {
        int x = this.posX + 2;
        int y = this.posY + 4;
        this.directoryNavigationWidget = new WidgetDirectoryNavigation(x, y, this.browserEntryWidth, 14, this.currentDirectory, this.getRootDirectory(), this, this.iconProvider);
        this.browserEntriesOffsetY = this.directoryNavigationWidget.getHeight() + 3;
        this.widgetSearchBar = this.directoryNavigationWidget;
    }

    public void refreshEntries() {
        this.updateDirectoryNavigationWidget();
        this.refreshBrowserEntries();
    }

    protected void refreshBrowserEntries() {
        this.listContents.clear();
        ClientFile dir = this.currentDirectory;
        if (dir.isDirectory() && dir.canRead()) {
            if (this.hasFilter()) {
                this.addFilteredContents(dir);
            } else {
                this.addNonFilteredContents(dir);
            }
        }

        this.reCreateListEntryWidgets();
    }

    protected void addNonFilteredContents(ClientFile dir) {
        List<Client_DirectoryEntry> list = new ArrayList<>();
        this.addMatchingEntriesToList("dir", dir, list, (String)null, (String)null);
        Collections.sort(list);
        this.listContents.addAll(list);
        list.clear();
        this.addMatchingEntriesToList("file", dir, list, (String)null, (String)null);
        Collections.sort(list);
        this.listContents.addAll(list);
    }

    protected void addFilteredContents(ClientFile dir) {
        String filterText = this.widgetSearchBar.getFilter();
        List<Client_DirectoryEntry> list = new ArrayList<>();
        this.addFilteredContents(dir, filterText, list, (String)null);
        this.listContents.addAll(list);
    }

    protected void addFilteredContents(ClientFile dir, String filterText, List<Client_DirectoryEntry> listOut, @Nullable String prefix) {
        List<Client_DirectoryEntry> list = new ArrayList<>();
        this.addMatchingEntriesToList("dir", dir, list, filterText, prefix);
        Collections.sort(list);
        listOut.addAll(list);
        list.clear();

        for (ClientFile subDir : this.getSubDirectories(dir)) {
            String pre;
            if (prefix != null) {
                pre = prefix + subDir.getName() + "/";
            } else {
                pre = subDir.getName() + "/";
            }

            this.addFilteredContents(subDir, filterText, list, pre);
            Collections.sort(list);
            listOut.addAll(list);
            list.clear();
        }

        this.addMatchingEntriesToList("file", dir, list, filterText, prefix);
        Collections.sort(list);
        listOut.addAll(list);
    }

    protected void addMatchingEntriesToList(String filter, ClientFile dir, List<Client_DirectoryEntry> list, @Nullable String filterText, @Nullable String displayNamePrefix) {

        CompletableFuture<List<ClientFile>> future = dir.listFiles("dir");

        try {
            List<ClientFile> var6 = future.get();
            int var7 = var6.size();

            for(int var8 = 0; var8 < var7; ++var8) {
                ClientFile file = var6.get(var8);
                String name = FileUtils.getNameWithoutExtension(file.getName().toLowerCase());
                if (filterText == null || this.matchesFilter(name, filterText)) {
                    list.add(new Client_DirectoryEntry(Client_DirectoryEntryType.fromFile(file), dir, file.getName(), displayNamePrefix));
                }
            }
        } catch (Exception e) {}
    }

    protected List<ClientFile> getSubDirectories(ClientFile dir) {
        List<ClientFile> dirs = new ArrayList<>();
        CompletableFuture<List<ClientFile>> future = dir.listFiles("dir");
        try {
            System.out.println("trying future...");
            List<ClientFile> var3 = future.get(); // This will block until the future is complete
            int var4 = var3.size();

            for(int var5 = 0; var5 < var4; ++var5) {
                ClientFile file = var3.get(var5);
                dirs.add(file);
            }

            return dirs;
        } catch (Exception e) {}
        System.out.println("test??");
        return new ArrayList<>();
    }

    protected ClientFile getRootDirectory() {
        return DataManagerClient.getDatapackBaseDirectory();
    }

    protected FileFilter getDirectoryFilter() {
        return DIRECTORY_FILTER;
    }

    protected Client_WidgetDirectoryEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, Client_DirectoryEntry entry) {
        return new Client_WidgetDirectoryEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, entry, listIndex, this, this.iconProvider);
    }

    protected boolean currentDirectoryIsRoot() {
        return this.currentDirectory.equals(getRootDirectory());
    }

    public ClientFile getCurrentDirectory() {
        return this.currentDirectory;
    }

    public void switchToDirectory(ClientFile dir) {
        this.clearSelection();
        this.currentDirectory = FileUtils.getCanonicalFileIfPossible(dir);
        this.refreshEntries();
        this.resetScrollbarPosition();
    }

    public void switchToRootDirectory() {
        this.switchToDirectory(this.getRootDirectory());
    }

    public void switchToParentDirectory() {
        this.currentDirectory.getParentFile().thenAccept(parent ->{
            if (!this.currentDirectoryIsRoot() && parent != null && this.currentDirectory.getAbsolutePath().contains(this.getRootDirectory().getAbsolutePath())) {
                this.switchToDirectory(parent);
            } else {
                this.switchToRootDirectory();
            }
        });
    }
    public static class Client_DirectoryEntry  implements Comparable<Client_DirectoryEntry> {
        private final Client_DirectoryEntryType type;
        private final ClientFile dir;
        private final String name;
        @Nullable
        private final String displaynamePrefix;

        public Client_DirectoryEntry(Client_DirectoryEntryType type, ClientFile dir, String name, @Nullable String displaynamePrefix) {
            this.type = type;
            this.dir = dir;
            this.name = name;
            this.displaynamePrefix = displaynamePrefix;
        }

        public Client_DirectoryEntryType getType() {
            return this.type;
        }

        public ClientFile getDirectory() {
            return this.dir;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public String getDisplayNamePrefix() {
            return this.displaynamePrefix;
        }

        public String getDisplayName() {
            return this.displaynamePrefix != null ? this.displaynamePrefix + this.name : this.name;
        }

        public int compareTo(Client_DirectoryEntry other) {
            return this.name.toLowerCase(Locale.US).compareTo(other.getName().toLowerCase(Locale.US));
        }
    }

    public static enum Client_DirectoryEntryType {
        INVALID,
        DIRECTORY,
        FILE;

        private Client_DirectoryEntryType() {
        }

        public static Client_DirectoryEntryType fromFile(ClientFile file) {
            if (!file.exists()) {
                return INVALID;
            } else {
                return file.isDirectory() ? DIRECTORY : FILE;
            }
        }
    }
    public static class Client_WidgetDirectoryEntry extends WidgetListEntryBase<Client_DirectoryEntry> {
        protected final IDirectoryNavigator navigator;
        protected final Client_DirectoryEntry entry;
        protected final IFileBrowserIconProvider iconProvider;
        protected final boolean isOdd;

        public Client_WidgetDirectoryEntry(int x, int y, int width, int height, boolean isOdd, Client_DirectoryEntry entry, int listIndex, IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider) {
            super(x, y, width, height, entry, listIndex);
            this.isOdd = isOdd;
            this.entry = entry;
            this.navigator = navigator;
            this.iconProvider = iconProvider;
        }

        public Client_DirectoryEntry getDirectoryEntry() {
            return this.entry;
        }

        protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton) {
            if (this.entry.getType() == Client_DirectoryEntryType.DIRECTORY) {
                this.navigator.switchToDirectory(new ClientFile(this.entry.getDirectory(),this.entry.getName()));
                return true;
            } else {
                return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
            }
        }

        public void render(int mouseX, int mouseY, boolean selected, DrawContext drawContext) {
            if (!selected && !this.isMouseOver(mouseX, mouseY)) {
                if (this.isOdd) {
                    RenderUtils.drawRect(this.x, this.y, this.width, this.height, 553648127);
                } else {
                    RenderUtils.drawRect(this.x, this.y, this.width, this.height, 956301311);
                }
            } else {
                RenderUtils.drawRect(this.x, this.y, this.width, this.height, 1895825407);
            }

            IGuiIcon icon = null;
            if (Objects.requireNonNull(this.entry.getType()) == Client_DirectoryEntryType.DIRECTORY) {
                icon = this.iconProvider.getIconDirectory();
            } else {
                icon = this.iconProvider.getIconForFileType(FileType.fromString(this.entry.getName()));
            }

            int iconWidth = icon != null ? icon.getWidth() : 0;
            int xOffset = iconWidth + 2;
            if (icon != null) {
                RenderUtils.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.bindTexture(icon.getTexture());
                icon.renderAt(this.x, this.y + (this.height - icon.getHeight()) / 2, (float)(this.zLevel + 10), false, false);
            }

            if (selected) {
                RenderUtils.drawOutline(this.x, this.y, this.width, this.height, -286331154);
            }

            int yOffset = (this.height - this.fontHeight) / 2 + 1;
            this.drawString(this.x + xOffset + 2, this.y + yOffset, -1, this.getDisplayName(), drawContext);
            super.render(mouseX, mouseY, selected, drawContext);
        }

        protected String getDisplayName() {
            return this.entry.getType() == Client_DirectoryEntryType.DIRECTORY ? this.entry.getDisplayName() : FileUtils.getNameWithoutExtension(this.entry.getDisplayName());
        }
    }

}
