package net.mat0u5.functioneditor.gui;

import java.io.File;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetDirectoryEntry;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase.DirectoryEntry;

/*
    Created by masa (https://github.com/maruohon/litematica),
    Modified by Mat0u5
*/
public abstract class GuiBrowserBase extends GuiListBase<DirectoryEntry, WidgetDirectoryEntry, WidgetSchematicBrowser>
{
    public GuiBrowserBase(int browserX, int browserY)
    {
        super(browserX, browserY);
    }


    @Override
    protected WidgetSchematicBrowser createListWidget(int listX, int listY)
    {
        // The width and height will be set to the actual values in initGui()
        return new WidgetSchematicBrowser(listX, listY, 100, 100, this, this.getSelectionListener());
    }

    /**
     * This is the string the DataManager uses for saving/loading/storing the last used directory
     * for each browser GUI type/contet.
     * @return
     */
    public abstract String getBrowserContext();

    public abstract File getDefaultDirectory();

    @Override
    protected ISelectionListener<DirectoryEntry> getSelectionListener()
    {
        return null;
    }

    @Override
    protected int getBrowserWidth()
    {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 70;
    }

    public int getMaxInfoHeight()
    {
        return this.getBrowserHeight();
    }
}
