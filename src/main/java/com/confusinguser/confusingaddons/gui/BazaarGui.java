package com.confusinguser.confusingaddons.gui;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class BazaarGui extends GuiChest {
    private final int inventoryRows;
    private static final ResourceLocation BAZAAR_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    public BazaarGui(IInventory upperInv, IInventory lowerInv) {
        super(upperInv, lowerInv);
        inventoryRows = lowerInv.getSizeInventory() / 9;
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.mc.getTextureManager().bindTexture(BAZAAR_GUI_TEXTURE);
        int x = (this.width + this.xSize) / 2 + 10;
        int y = (this.height - this.ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, 30, 80);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    }
}
