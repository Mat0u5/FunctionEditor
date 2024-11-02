package net.mat0u5.functioneditor.gui;


import net.mat0u5.functioneditor.network.NetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.List;

public class FunctionEditScreen extends Screen {
    private final String originalRecieveType;
    private final String originalFunction;
    private final List<String> originalLines;
    private TextFieldWidget textField; // Text field for editing function data

    public FunctionEditScreen(String recieveType, String function, List<String> lines) {
        super(Text.of("Edit Function Data")); // Set the screen title
        this.originalRecieveType = recieveType;
        this.originalFunction = function;
        this.originalLines = lines;
    }

    @Override
    protected void init() {
        // Create and set up the text field for editing
        textField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 50, 200, 20, Text.of("Function Data"));
        textField.setText(originalLines.toString()); // Set the initial text to the original data
        this.addDrawableChild(textField); // Add the text field to the screen

        // Create the save button using the builder pattern
        this.addDrawableChild(ButtonWidget.builder(Text.of("Save"), button -> {
            // When clicked, send the edited data back to the server
            NetworkHandler.sendFunctionDataToServer(originalRecieveType, originalFunction, List.of(textField.getText()));
            this.close(); // Close the screen after saving
        }).dimensions(this.width / 2 - 100, this.height / 2, 200, 20).build());

        // Create the cancel button using the builder pattern
        this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), button -> {
            this.close(); // Just close the screen
        }).dimensions(this.width / 2 - 100, this.height / 2 + 30, 200, 20).build());
    }

    @Override
    public void close() {
        super.close();
        MinecraftClient.getInstance().setScreen(null); // Set the screen back to the previous one
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta); // Render the background
        super.render(context, mouseX, mouseY, delta); // Render the screen's elements
        textField.render(context, mouseX, mouseY, delta); // Render the text field
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true; // Allow closing the screen with the ESC key
    }
}
