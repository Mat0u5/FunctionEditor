package net.mat0u5.functioneditor.gui.widgets;

import java.io.FileFilter;

import net.mat0u5.functioneditor.files.ClientFile;
import net.mat0u5.functioneditor.files.DataManagerClient;
import net.mat0u5.functioneditor.files.FileFilters;
import net.mat0u5.functioneditor.gui.GuiFileBrowser;
import net.mat0u5.functioneditor.gui.Icons;

import net.minecraft.client.gui.DrawContext;

import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import org.jetbrains.annotations.Nullable;

public class WidgetFileBrowser extends WidgetFileBrowserBase
{
    protected static final FileFilter FILE_FILTER = FileFilters.FILE_FILTER_SUPPORTED;

    protected final GuiFileBrowser parent;
    protected final int infoWidth;
    protected final int infoHeight;

    public WidgetFileBrowser(int x, int y, int width, int height, GuiFileBrowser parent, @Nullable ISelectionListener<Client_DirectoryEntry> selectionListener)
    {
        super(x, y, width, height, parent.getBrowserContext(),
                parent.getDefaultDirectory(), selectionListener, Icons.FILE_ICON_LITEMATIC);

        this.title = StringUtils.translate("litematica.gui.title.schematic_browser");
        this.infoWidth = 170;
        this.infoHeight = 290;
        this.parent = parent;
    }

    @Override
    protected int getBrowserWidthForTotalWidth(int width)
    {
        return super.getBrowserWidthForTotalWidth(width) - this.infoWidth;
    }

    @Override
    public void close()
    {
        super.close();
    }

    @Override
    protected ClientFile getRootDirectory()
    {
        return DataManagerClient.getDatapackBaseDirectory();
    }

    protected FileFilter getFileFilter()
    {
        return FILE_FILTER;
    }

    @Override
    protected void drawAdditionalContents(int mouseX, int mouseY, DrawContext drawContext)
    {
        this.drawSelectedFileInfo(this.getLastSelectedEntry(), drawContext);
    }

    protected void drawSelectedFileInfo(@Nullable Client_DirectoryEntry entry, DrawContext drawContext)
    {
        int x = this.posX + this.totalWidth - this.infoWidth;
        int y = this.posY;
        int height = Math.min(this.infoHeight, this.parent.getMaxInfoHeight());

        RenderUtils.drawOutlinedBox(x, y, this.infoWidth, height, 0xA0000000, COLOR_HORIZONTAL_BAR);

        if (entry == null)
        {
            return;
        }

        /*
        SchematicMetadata meta = this.getSchematicMetadata(entry);

        if (meta != null)
        {
            RenderUtils.color(1f, 1f, 1f, 1f);

            x += 3;
            y += 3;
            int textColor = 0xC0C0C0C0;
            int valueColor = 0xFFFFFFFF;

            String str = StringUtils.translate("litematica.gui.label.schematic_info.name");
            this.drawString(drawContext, str, x, y, textColor);
            y += 12;

            this.drawString(drawContext, meta.getName(), x + 4, y, valueColor);
            y += 12;

            str = StringUtils.translate("litematica.gui.label.schematic_info.schematic_author", meta.getAuthor());
            this.drawString(drawContext, str, x, y, textColor);
            y += 12;

            String strDate = DATE_FORMAT.format(new Date(meta.getTimeCreated()));
            str = StringUtils.translate("litematica.gui.label.schematic_info.time_created", strDate);
            this.drawString(drawContext, str, x, y, textColor);
            y += 12;

            if (meta.hasBeenModified())
            {
                strDate = DATE_FORMAT.format(new Date(meta.getTimeModified()));
                str = StringUtils.translate("litematica.gui.label.schematic_info.time_modified", strDate);
                this.drawString(drawContext, str, x, y, textColor);
                y += 12;
            }

            str = StringUtils.translate("litematica.gui.label.schematic_info.region_count", meta.getRegionCount());
            this.drawString(drawContext, str, x, y, textColor);
            y += 12;

            if (this.parent.height >= 340)
            {
                str = StringUtils.translate("litematica.gui.label.schematic_info.total_volume", meta.getTotalVolume());
                this.drawString(drawContext, str, x, y, textColor);
                y += 12;

                str = StringUtils.translate("litematica.gui.label.schematic_info.total_blocks", meta.getTotalBlocks());
                this.drawString(drawContext, str, x, y, textColor);
                y += 12;

                str = StringUtils.translate("litematica.gui.label.schematic_info.enclosing_size");
                this.drawString(drawContext, str, x, y, textColor);
                y += 12;

                Vec3i areaSize = meta.getEnclosingSize();
                String tmp = String.format("%d x %d x %d", areaSize.getX(), areaSize.getY(), areaSize.getZ());
                this.drawString(drawContext, tmp, x + 4, y, valueColor);
                y += 12;
            }
            else
            {
                str = StringUtils.translate("litematica.gui.label.schematic_info.total_blocks_and_volume", meta.getTotalBlocks(), meta.getTotalVolume());
                this.drawString(drawContext, str, x, y, textColor);
                y += 12;

                Vec3i areaSize = meta.getEnclosingSize();
                String tmp = String.format("%d x %d x %d", areaSize.getX(), areaSize.getY(), areaSize.getZ());
                str = StringUtils.translate("litematica.gui.label.schematic_info.enclosing_size_value", tmp);
                this.drawString(drawContext, str, x, y, textColor);
                y += 12;
            }

            //y += 12;

            Pair<Identifier, NativeImageBackedTexture> pair = this.cachedPreviewImages.get(entry.getFullPath());

            if (pair != null)
            {
                y += 14;

                int iconSize = pair.getRight().getImage().getWidth();
                boolean needsScaling = height < this.infoHeight;

                RenderUtils.color(1f, 1f, 1f, 1f);

                if (needsScaling)
                {
                    iconSize = height - y + this.posY - 6;
                }

                RenderUtils.drawOutlinedBox(x + 4, y, iconSize, iconSize, 0xA0000000, COLOR_HORIZONTAL_BAR);

                drawContext.drawTexture(pair.getLeft(), x + 4, y, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);
            }
        }*/
    }
}

