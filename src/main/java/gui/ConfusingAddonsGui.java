package gui;

import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class ConfusingAddonsGui extends GuiScreen {

    public static final int BUTTON_SPACING = 150;
    public static final int BUTTON_WIDTH = 150;

    @Override
    public void initGui() {
        this.buttonList.clear();
        GuiButton featureButton;
        for (Feature feature : Feature.values())
            this.buttonList.add(new FeatureButton(feature.getId(), (width-(width/(BUTTON_SPACING+BUTTON_WIDTH)*(BUTTON_SPACING+BUTTON_WIDTH)-BUTTON_SPACING))/2 + feature.getId() % (width/(BUTTON_SPACING+BUTTON_WIDTH)) * (BUTTON_SPACING+BUTTON_WIDTH), 30 + BUTTON_SPACING * (feature.getId()/(width/(BUTTON_SPACING+BUTTON_WIDTH))), BUTTON_WIDTH, 110, feature.name(), feature));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button instanceof FeatureButton) {
            ((FeatureButton) button).feature.setStatus(!((FeatureButton) button).feature.isEnabled());
        }
    }
}
