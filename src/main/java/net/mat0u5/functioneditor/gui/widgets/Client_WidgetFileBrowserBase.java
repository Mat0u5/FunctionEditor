package net.mat0u5.functioneditor.gui.widgets;

import fi.dy.masa.malilib.gui.interfaces.*;
import fi.dy.masa.malilib.gui.widgets.WidgetDirectoryNavigation;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.FileUtils;
import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.*;

import net.mat0u5.functioneditor.gui.FileEditorFilter;
import net.mat0u5.functioneditor.gui.widgets.Client_WidgetFileBrowserBase.Client_DirectoryEntry;
import net.mat0u5.functioneditor.gui.widgets.Client_WidgetFileBrowserBase.Client_WidgetDirectoryEntry;
import net.mat0u5.functioneditor.gui.widgets.Client_WidgetFileBrowserBase.Client_DirectoryEntryType;
import net.mat0u5.functioneditor.gui.widgets.Client_WidgetFileBrowserBase.Client_FileFilterDirectories;
import net.mat0u5.functioneditor.utils.DataManager;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;

public class Client_WidgetFileBrowserBase extends WidgetListBase<Client_DirectoryEntry, Client_WidgetDirectoryEntry> implements IDirectoryNavigator {
    protected static final FileFilter FILE_FILTER = new FileEditorFilter();
    protected static final FileFilter DIRECTORY_FILTER = new Client_FileFilterDirectories();
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected final IDirectoryCache cache;
    protected File currentDirectory;
    protected final String browserContext;
    protected final IFileBrowserIconProvider iconProvider;
    @Nullable
    protected WidgetDirectoryNavigation directoryNavigationWidget;

    public Client_WidgetFileBrowserBase(int x, int y, int width, int height, IDirectoryCache cache, String browserContext, File defaultDirectory, @Nullable ISelectionListener<Client_DirectoryEntry> selectionListener, IFileBrowserIconProvider iconProvider) {
        super(x, y, width, height, selectionListener);
        this.cache = cache;
        this.browserContext = browserContext;
        this.currentDirectory = this.cache.getCurrentDirectoryForContext(this.browserContext);
        this.iconProvider = iconProvider;
        this.allowKeyboardNavigation = true;
        if (this.currentDirectory == null) {
            this.currentDirectory = defaultDirectory;
        }

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
            this.switchToDirectory(new File(((Client_DirectoryEntry)this.getLastSelectedEntry()).getDirectory(), ((Client_DirectoryEntry)this.getLastSelectedEntry()).getName()));
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
        File dir = this.currentDirectory;
        if (dir.isDirectory() && dir.canRead()) {
            if (this.hasFilter()) {
                this.addFilteredContents(dir);
            } else {
                this.addNonFilteredContents(dir);
            }
        }

        this.reCreateListEntryWidgets();
    }

    protected void addNonFilteredContents(File dir) {
        List<Client_DirectoryEntry> list = new ArrayList();
        this.addMatchingEntriesToList(this.getDirectoryFilter(), dir, list, (String)null, (String)null);
        Collections.sort(list);
        this.listContents.addAll(list);
        list.clear();
        this.addMatchingEntriesToList(FILE_FILTER, dir, list, (String)null, (String)null);
        Collections.sort(list);
        this.listContents.addAll(list);
    }

    protected void addFilteredContents(File dir) {
        String filterText = this.widgetSearchBar.getFilter();
        List<Client_DirectoryEntry> list = new ArrayList();
        this.addFilteredContents(dir, filterText, list, (String)null);
        this.listContents.addAll(list);
    }

    protected void addFilteredContents(File dir, String filterText, List<Client_DirectoryEntry> listOut, @Nullable String prefix) {
        List<Client_DirectoryEntry> list = new ArrayList();
        this.addMatchingEntriesToList(this.getDirectoryFilter(), dir, list, filterText, prefix);
        Collections.sort(list);
        listOut.addAll(list);
        list.clear();
        Iterator var6 = this.getSubDirectories(dir).iterator();

        while(var6.hasNext()) {
            File subDir = (File)var6.next();
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

        this.addMatchingEntriesToList(FILE_FILTER, dir, list, filterText, prefix);
        Collections.sort(list);
        listOut.addAll(list);
    }

    protected void addMatchingEntriesToList(FileFilter filter, File dir, List<Client_DirectoryEntry> list, @Nullable String filterText, @Nullable String displayNamePrefix) {
        File[] var6 = dir.listFiles(filter);
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            File file = var6[var8];
            String name = FileUtils.getNameWithoutExtension(file.getName().toLowerCase());
            if (filterText == null || this.matchesFilter(name, filterText)) {
                list.add(new Client_DirectoryEntry(Client_DirectoryEntryType.fromFile(file), dir, file.getName(), displayNamePrefix));
            }
        }

    }

    protected List<File> getSubDirectories(File dir) {
        List<File> dirs = new ArrayList();
        File[] var3 = dir.listFiles(DIRECTORY_FILTER);
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            File file = var3[var5];
            dirs.add(file);
        }

        return dirs;
    }

    protected File getRootDirectory() {
        //TODO
        return DataManager.getRootDirectory();
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

    public File getCurrentDirectory() {
        return this.currentDirectory;
    }

    public void switchToDirectory(File dir) {
        this.clearSelection();
        this.currentDirectory = FileUtils.getCanonicalFileIfPossible(dir);
        this.cache.setCurrentDirectoryForContext(this.browserContext, dir);
        this.refreshEntries();
        this.resetScrollbarPosition();
    }

    public void switchToRootDirectory() {
        this.switchToDirectory(this.getRootDirectory());
    }

    public void switchToParentDirectory() {
        File parent = this.currentDirectory.getParentFile();
        if (!this.currentDirectoryIsRoot() && parent != null && this.currentDirectory.getAbsolutePath().contains(this.getRootDirectory().getAbsolutePath())) {
            this.switchToDirectory(parent);
        } else {
            this.switchToRootDirectory();
        }

    }
    public static class Client_DirectoryEntry  implements Comparable<Client_DirectoryEntry> {
        private final Client_DirectoryEntryType type;
        private final File dir;
        private final String name;
        @Nullable
        private final String displaynamePrefix;

        public Client_DirectoryEntry(Client_DirectoryEntryType type, File dir, String name, @Nullable String displaynamePrefix) {
            this.type = type;
            this.dir = dir;
            this.name = name;
            this.displaynamePrefix = displaynamePrefix;
        }

        public Client_DirectoryEntryType getType() {
            return this.type;
        }

        public File getDirectory() {
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

        public File getFullPath() {
            return new File(this.dir, this.name);
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

        public static Client_DirectoryEntryType fromFile(File file) {
            if (!file.exists()) {
                return INVALID;
            } else {
                return file.isDirectory() ? DIRECTORY : FILE;
            }
        }
    }

    public static class Client_FileFilterDirectories implements FileFilter {
        public Client_FileFilterDirectories() {
        }

        public boolean accept(File pathName) {
            return pathName.isDirectory() && !pathName.getName().startsWith(".");
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
                this.navigator.switchToDirectory(new File(this.entry.getDirectory(), this.entry.getName()));
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
            switch (this.entry.getType()) {
                case DIRECTORY -> icon = this.iconProvider.getIconDirectory();
                default -> icon = this.iconProvider.getIconForFile(this.entry.getFullPath());
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
