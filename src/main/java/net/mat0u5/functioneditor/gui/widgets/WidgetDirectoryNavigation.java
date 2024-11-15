package net.mat0u5.functioneditor.gui.widgets;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextInputFeedback;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.widgets.WidgetIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import java.util.Arrays;

import net.mat0u5.functioneditor.files.ClientFile;
import net.mat0u5.functioneditor.files.FileUtils;
import net.mat0u5.functioneditor.gui.DirectoryCreator;
import net.mat0u5.functioneditor.gui.IDirectoryNavigator;
import net.mat0u5.functioneditor.gui.IFileBrowserIconProvider;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;

public class WidgetDirectoryNavigation extends WidgetSearchBar {
    protected final ClientFile currentDir;
    protected final ClientFile rootDir;
    protected final IDirectoryNavigator navigator;
    protected final WidgetIcon iconRoot;
    protected final WidgetIcon iconUp;
    protected final WidgetIcon iconCreateDir;

    public WidgetDirectoryNavigation(int x, int y, int width, int height, ClientFile currentDir, ClientFile rootDir, IDirectoryNavigator navigator, IFileBrowserIconProvider iconProvider) {//
        super(x, y, width, height, 0, iconProvider.getIconSearch(), LeftRight.RIGHT);
        this.currentDir = currentDir;
        this.rootDir = rootDir;
        this.navigator = navigator;
        this.iconRoot = new WidgetIcon(x, y + 1, iconProvider.getIconRoot());
        x += this.iconRoot.getWidth() + 2;
        this.iconUp = new WidgetIcon(x, y + 1, iconProvider.getIconUp());
        x += this.iconUp.getWidth() + 2;
        this.iconCreateDir = new WidgetIcon(x, y + 1, iconProvider.getIconCreateDirectory());
    }

    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton) {
        if (!this.searchOpen) {
            WidgetIcon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);
            if (hoveredIcon == this.iconRoot) {
                this.navigator.switchToRootDirectory();
                return true;
            }

            if (hoveredIcon == this.iconUp) {
                this.navigator.switchToParentDirectory();
                return true;
            }

            if (hoveredIcon == this.iconCreateDir) {
                String title = "functioneditor.create_directory";
                DirectoryCreator creator = new DirectoryCreator(this.currentDir, this.navigator);
                GuiTextInputFeedback gui = new GuiTextInputFeedback(256, title, "", GuiUtils.getCurrentScreen(), creator);
                GuiBase.openGui(gui);
                return true;
            }
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Nullable
    protected WidgetIcon getHoveredIcon(int mouseX, int mouseY) {
        if (!this.searchOpen) {
            if (this.iconRoot.isMouseOver(mouseX, mouseY)) {
                return this.iconRoot;
            }

            if (this.iconUp.isMouseOver(mouseX, mouseY)) {
                return this.iconUp;
            }

            if (this.iconCreateDir.isMouseOver(mouseX, mouseY)) {
                return this.iconCreateDir;
            }
        }

        return null;
    }

    public void render(int mouseX, int mouseY, boolean selected, DrawContext drawContext) {
        super.render(mouseX, mouseY, selected, drawContext);
        if (!this.searchOpen) {
            WidgetIcon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);
            this.iconRoot.render(false, hoveredIcon == this.iconRoot);
            this.iconUp.render(false, hoveredIcon == this.iconUp);
            this.iconCreateDir.render(false, hoveredIcon == this.iconCreateDir);
            int pathStartX = this.iconCreateDir.getX() + this.iconCreateDir.getWidth() + 6;
            RenderUtils.drawRect(pathStartX, this.y, this.width - pathStartX - 2, this.height, 553648127);
            int textColor = -1061109568;
            int maxLen = (this.width - 40) / this.getStringWidth("a") - 4;
            String path = FileUtils.getJoinedTrailingPathElements(this.currentDir, this.rootDir, maxLen, " / ");
            this.drawString(pathStartX + 3, this.y + 3, textColor, path, drawContext);
        }

    }

    public void postRenderHovered(int mouseX, int mouseY, boolean selected, DrawContext drawContext) {
        super.postRenderHovered(mouseX, mouseY, selected, drawContext);
        if (!this.searchOpen) {
            WidgetIcon hoveredIcon = this.getHoveredIcon(mouseX, mouseY);
            if (hoveredIcon == this.iconRoot) {
                RenderUtils.drawHoverText(mouseX, mouseY, Arrays.asList("Switch dir to root"), drawContext);
            } else if (hoveredIcon == this.iconUp) {
                RenderUtils.drawHoverText(mouseX, mouseY, Arrays.asList("Switch dir to parent"), drawContext);
            } else if (hoveredIcon == this.iconCreateDir) {
                RenderUtils.drawHoverText(mouseX, mouseY, Arrays.asList("Create directory"), drawContext);
            }
        }

    }
}
