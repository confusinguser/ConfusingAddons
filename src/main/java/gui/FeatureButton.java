package gui;

import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class FeatureButton extends GuiButton {
    Feature feature;

    public FeatureButton(int buttonId, int x, int y, String buttonText, Feature feature) {
        super(buttonId, x, y, buttonText);
        this.feature = feature;
    }

    public FeatureButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Feature feature) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.feature = feature;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (feature.isEnabled())
                this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + 130, this.yPosition + 110, 0x88008800, 0x88008800);
            else
                this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + 130, this.yPosition + 110, 0x88880000, 0x88880000);
            GlStateManager.popMatrix();
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 0xA0A0A0;
            }
            else if (this.hovered)
            {
                j = 0xFFFFA0;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
        }
    }
}
