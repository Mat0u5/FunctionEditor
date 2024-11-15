package net.mat0u5.functioneditor.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.*;
import fi.dy.masa.malilib.util.StringUtils;
import net.mat0u5.functioneditor.files.ClientFile;
import net.mat0u5.functioneditor.files.DataManagerClient;
import net.mat0u5.functioneditor.gui.widgets.WidgetFileBrowser;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import net.mat0u5.functioneditor.gui.widgets.WidgetFileBrowserBase.Client_DirectoryEntry;
import net.mat0u5.functioneditor.gui.widgets.WidgetFileBrowserBase.Client_WidgetDirectoryEntry;

/*
    Created by masa (https://github.com/maruohon/litematica),
    Modified by Mat0u5
*/
public class GuiFileBrowser extends GuiListBase<Client_DirectoryEntry, Client_WidgetDirectoryEntry, WidgetFileBrowser> {
    public GuiFileBrowser()
    {
        super(12,24);
        this.title = StringUtils.translate("litematica.gui.title.load_schematic");
    }

    @Override
    protected WidgetFileBrowser createListWidget(int listX, int listY)
    {
        // The width and height will be set to the actual values in initGui()

        return new WidgetFileBrowser(listX, listY, 100, 100, this, this.getSelectionListener());
    }

    @Override
    @Nullable
    protected ISelectionListener<Client_DirectoryEntry> getSelectionListener()
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

    public String getBrowserContext()
    {
        return "schematic_load";
    }


    public ClientFile getDefaultDirectory()
    {
        return DataManagerClient.getDatapackBaseDirectory();
    }


    public int getMaxInfoHeight()
    {
        return this.getBrowserHeight() + 10;
    }

    public void initGui()
    {
        super.initGui();

        int x = 12;
        int y = this.height - 40;
        int buttonWidth;
        String label;
        ButtonGeneric button;

        label = "temp1";
        String hover = "temp1hover";
        WidgetCheckBox checkbox = new WidgetCheckBox(x, y, Icons.CHECKBOX_UNSELECTED, Icons.CHECKBOX_SELECTED, label, hover);
        checkbox.setListener(new CheckboxListener());
        checkbox.setChecked(false, false);
        this.addWidget(checkbox);

        y = this.height - 26;
        x += this.createButton(x, y, -1, ButtonListener.Type.LOAD_SCHEMATIC) + 4;
        x += this.createButton(x, y, -1, ButtonListener.Type.MATERIAL_LIST) + 4;

        ButtonListenerChangeMenu.ButtonType type = ButtonListenerChangeMenu.ButtonType.LOADED_SCHEMATICS;
        label = StringUtils.translate(type.getLabelKey());
        buttonWidth = this.getStringWidth(label) + 30;
        button = new ButtonGeneric(x, y, buttonWidth, 20, label, type.getIcon());
        this.addButton(button, new ButtonListenerChangeMenu(type, this.getParent()));

        type = ButtonListenerChangeMenu.ButtonType.MAIN_MENU;
        label = StringUtils.translate(type.getLabelKey());
        buttonWidth = this.getStringWidth(label) + 20;
        x = this.width - buttonWidth - 10;
        button = new ButtonGeneric(x, y, buttonWidth, 20, label);
        this.addButton(button, new ButtonListenerChangeMenu(type, this.getParent()));
    }

    private int createButton(int x, int y, int width, ButtonListener.Type type)
    {
        ButtonListener listener = new ButtonListener(type);
        String label = StringUtils.translate(type.getTranslationKey());

        if (width == -1)
        {
            width = this.getStringWidth(label) + 10;
        }

        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, label);

        if (type == ButtonListener.Type.MATERIAL_LIST)
        {
            button.setHoverStrings("temp2");
        }

        this.addButton(button, listener);

        return width;
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final Type type;

        public ButtonListener(Type type)
        {
            this.type = type;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {

        }

        public enum Type
        {
            LOAD_SCHEMATIC  ("litematica.gui.button.load_schematic_to_memory"),
            MATERIAL_LIST   ("litematica.gui.button.material_list");

            private final String translationKey;

            private Type(String translationKey)
            {
                this.translationKey = translationKey;
            }

            public String getTranslationKey()
            {
                return this.translationKey;
            }
        }
    }

    private static class CheckboxListener implements ISelectionListener<WidgetCheckBox>
    {
        @Override
        public void onSelectionChange(WidgetCheckBox entry)
        {

            //DataManager.setCreatePlacementOnLoad(entry.isChecked());
        }
    }

    public static class ButtonListenerChangeMenu implements IButtonActionListener
    {
        private final ButtonType type;
        @Nullable
        private final Screen parent;

        public ButtonListenerChangeMenu(ButtonType type, @Nullable Screen parent)
        {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            GuiBase gui = null;

            switch (this.type)
            {
                /*
                case AREA_EDITOR:
                    gui = DataManager.getSelectionManager().getEditGui();
                    break;
                case AREA_SELECTION_BROWSER:
                    gui = new GuiAreaSelectionManager();
                    break;
                case CONFIGURATION:
                    GuiBase.openGui(new GuiConfigs());
                    return;
                case LOAD_SCHEMATICS:
                    gui = new GuiSchematicLoad();
                    break;
                case LOADED_SCHEMATICS:
                    gui = new GuiSchematicLoadedList();
                    break;
                case MAIN_MENU:
                    gui = new GuiMainMenu();
                    break;
                case SCHEMATIC_MANAGER:
                    gui = new GuiSchematicManager();
                    break;
                case SCHEMATIC_PLACEMENTS:
                    gui = new GuiSchematicPlacementsList();
                    break;
                case TASK_MANAGER:
                    gui = new GuiTaskManager();
                    break;
                case SCHEMATIC_PROJECTS_MANAGER:
                    DataManager.getSchematicProjectsManager().openSchematicProjectsGui();
                    return;
                    */
            }

            if (gui != null)
            {
                gui.setParent(this.parent);
                GuiBase.openGui(gui);
            }
        }

        public enum ButtonType
        {
            // List loaded Schematics in SchematicHolder
            LOADED_SCHEMATICS           ("litematica.gui.button.change_menu.show_loaded_schematics", ButtonIcons.LOADED_SCHEMATICS),
            // List Schematics placements
            SCHEMATIC_PLACEMENTS        ("litematica.gui.button.change_menu.show_schematic_placements", ButtonIcons.SCHEMATIC_PLACEMENTS),
            // Open the Area Selection browser
            AREA_SELECTION_BROWSER      ("litematica.gui.button.change_menu.show_area_selections", ButtonIcons.AREA_SELECTION),
            // Open the Area Editor GUI
            AREA_EDITOR                 ("litematica.gui.button.change_menu.area_editor", ButtonIcons.AREA_EDITOR),
            // Load Schematics from file to memory
            LOAD_SCHEMATICS             ("litematica.gui.button.change_menu.load_schematics_to_memory", ButtonIcons.SCHEMATIC_BROWSER),
            // Edit Schematics (description or icon), or convert between formats
            SCHEMATIC_MANAGER           ("litematica.gui.button.change_menu.schematic_manager", ButtonIcons.SCHEMATIC_MANAGER),
            // Open the Task Manager
            TASK_MANAGER                ("litematica.gui.button.change_menu.task_manager", ButtonIcons.TASK_MANAGER),
            // Open the Schematic Projects browser
            SCHEMATIC_PROJECTS_MANAGER  ("litematica.gui.button.change_menu.schematic_projects_manager", ButtonIcons.SCHEMATIC_PROJECTS),
            // In-game Configuration GUI
            CONFIGURATION               ("litematica.gui.button.change_menu.configuration_menu", ButtonIcons.CONFIGURATION),
            // Switch to the Litematica main menu
            MAIN_MENU                   ("litematica.gui.button.change_menu.to_main_menu", null);

            private final String labelKey;
            private final ButtonIcons icon;

            private ButtonType(String labelKey, ButtonIcons icon)
            {
                this.labelKey = labelKey;
                this.icon = icon;
            }

            public String getLabelKey()
            {
                return this.labelKey;
            }

            public String getDisplayName()
            {
                return StringUtils.translate(this.getLabelKey());
            }

            public ButtonIcons getIcon()
            {
                return this.icon;
            }
        }
    }
}
