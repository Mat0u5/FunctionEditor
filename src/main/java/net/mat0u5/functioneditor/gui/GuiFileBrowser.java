package net.mat0u5.functioneditor.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiStringListSelection;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.interfaces.IStringListConsumer;
import fi.dy.masa.malilib.gui.widgets.WidgetCheckBox;
import fi.dy.masa.malilib.gui.widgets.WidgetFileBrowserBase;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;

/*
    Created by masa (https://github.com/maruohon/litematica),
    Modified by Mat0u5
*/
public class GuiFileBrowser extends GuiBrowserBase {
    public GuiSchematicLoad()
    {
        super(12, 24);

        this.title = StringUtils.translate("litematica.gui.title.load_schematic");
    }

    @Override
    public String getBrowserContext()
    {
        return "schematic_load";
    }

    @Override
    public File getDefaultDirectory()
    {
        return DataManager.getSchematicsBaseDirectory();
    }

    @Override
    public int getMaxInfoHeight()
    {
        return this.getBrowserHeight() + 10;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = 12;
        int y = this.height - 40;
        int buttonWidth;
        String label;
        ButtonGeneric button;

        label = StringUtils.translate("litematica.gui.label.schematic_load.checkbox.create_placement");
        String hover = StringUtils.translate("litematica.gui.label.schematic_load.hoverinfo.create_placement");
        WidgetCheckBox checkbox = new WidgetCheckBox(x, y, Icons.CHECKBOX_UNSELECTED, Icons.CHECKBOX_SELECTED, label, hover);
        checkbox.setListener(new CheckboxListener());
        checkbox.setChecked(DataManager.getCreatePlacementOnLoad(), false);
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
        ButtonListener listener = new ButtonListener(type, this);
        String label = StringUtils.translate(type.getTranslationKey());

        if (width == -1)
        {
            width = this.getStringWidth(label) + 10;
        }

        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, label);

        if (type == ButtonListener.Type.MATERIAL_LIST)
        {
            button.setHoverStrings(StringUtils.translate("litematica.gui.button.hover.material_list_shift_to_select_sub_regions"));
        }

        this.addButton(button, listener);

        return width;
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final Type type;
        private final GuiSchematicLoad gui;

        public ButtonListener(Type type, GuiSchematicLoad gui)
        {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            WidgetFileBrowserBase.DirectoryEntry entry = this.gui.getListWidget().getLastSelectedEntry();

            if (entry == null)
            {
                this.gui.addMessage(Message.MessageType.ERROR, "litematica.error.schematic_load.no_schematic_selected");
                return;
            }

            File file = entry.getFullPath();

            if (file.exists() == false || file.isFile() == false || file.canRead() == false)
            {
                this.gui.addMessage(Message.MessageType.ERROR, "litematica.error.schematic_load.cant_read_file", file.getName());
                return;
            }

            this.gui.setNextMessageType(Message.MessageType.ERROR);
            LitematicaSchematic schematic = null;
            FileType fileType = FileType.fromFile(entry.getFullPath());
            boolean warnType = false;

            if (fileType == FileType.LITEMATICA_SCHEMATIC)
            {
                schematic = LitematicaSchematic.createFromFile(entry.getDirectory(), entry.getName());
            }
            else if (fileType == FileType.SCHEMATICA_SCHEMATIC)
            {
                schematic = WorldUtils.convertSchematicaSchematicToLitematicaSchematic(entry.getDirectory(), entry.getName(), false, this.gui);
                warnType = true;
            }
            else if (fileType == FileType.VANILLA_STRUCTURE)
            {
                schematic = WorldUtils.convertStructureToLitematicaSchematic(entry.getDirectory(), entry.getName());
                warnType = true;
            }
            else if (fileType == FileType.SPONGE_SCHEMATIC)
            {
                schematic = WorldUtils.convertSpongeSchematicToLitematicaSchematic(entry.getDirectory(), entry.getName());
                warnType = true;
            }
            else
            {
                this.gui.addMessage(Message.MessageType.ERROR, "litematica.error.schematic_load.unsupported_type", file.getName());
            }

            if (schematic != null)
            {
                if (this.type == Type.LOAD_SCHEMATIC)
                {
                    SchematicHolder.getInstance().addSchematic(schematic, true);
                    this.gui.addMessage(Message.MessageType.SUCCESS, "litematica.info.schematic_load.schematic_loaded", file.getName());

                    if (DataManager.getCreatePlacementOnLoad())
                    {
                        BlockPos pos = BlockPos.ofFloored(this.gui.mc.player.getPos());
                        String name = schematic.getMetadata().getName();
                        boolean enabled = GuiBase.isShiftDown() == false;

                        SchematicPlacementManager manager = DataManager.getSchematicPlacementManager();
                        SchematicPlacement placement = SchematicPlacement.createFor(schematic, pos, name, enabled, enabled);
                        manager.addSchematicPlacement(placement, true);
                        manager.setSelectedSchematicPlacement(placement);
                    }
                }
                else if (this.type == Type.MATERIAL_LIST)
                {
                    if (GuiBase.isShiftDown())
                    {
                        MaterialListCreator creator = new MaterialListCreator(schematic);
                        GuiStringListSelection gui = new GuiStringListSelection(schematic.getAreas().keySet(), creator);
                        gui.setTitle(StringUtils.translate("litematica.gui.title.material_list.select_schematic_regions", schematic.getMetadata().getName()));
                        gui.setParent(GuiUtils.getCurrentScreen());
                        GuiBase.openGui(gui);
                    }
                    else
                    {
                        MaterialListSchematic materialList = new MaterialListSchematic(schematic, true);
                        DataManager.setMaterialList(materialList); // Remember the last opened material list for the hotkey to (re-) open it
                        GuiBase.openGui(new GuiMaterialList(materialList));
                    }
                }

                if (warnType)
                {
                    InfoUtils.showGuiOrInGameMessage(Message.MessageType.WARNING, 15000, "litematica.message.warn.schematic_load_non_litematica");
                }
            }
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
            DataManager.setCreatePlacementOnLoad(entry.isChecked());
        }
    }

    private static class MaterialListCreator implements IStringListConsumer
    {
        private final LitematicaSchematic schematic;

        public MaterialListCreator(LitematicaSchematic schematic)
        {
            this.schematic = schematic;
        }

        @Override
        public boolean consume(Collection<String> strings)
        {
            MaterialListSchematic materialList = new MaterialListSchematic(this.schematic, strings, true);
            DataManager.setMaterialList(materialList); // Remember the last opened material list for the hotkey to (re-) open it
            GuiBase.openGui(new GuiMaterialList(materialList));

            return true;
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