package com.confusinguser.confusingaddons.gui.elements;

import com.confusinguser.confusingaddons.core.feature.Category;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class CategoryButton extends GuiButtonExt {
    private final Category category;

    public CategoryButton(int id, int xPos, int yPos, Category category) {
        super(id, xPos, yPos, category.getFriendlyName());
        this.category = category;
    }

    public CategoryButton(int id, int xPos, int yPos, int width, int height, Category category) {
        super(id, xPos, yPos, width, height, category.getFriendlyName());
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
