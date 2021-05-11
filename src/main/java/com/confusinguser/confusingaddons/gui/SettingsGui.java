package com.confusinguser.confusingaddons.gui;

import com.confusinguser.confusingaddons.core.feature.*;
import com.confusinguser.confusingaddons.gui.elements.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsGui extends GuiScreen {
    private Category category;

    private final List<GuiTextField> fieldsToDraw = new ArrayList<>();
    private final List<BetterGuiLabel> labelsToDraw = new ArrayList<>();

    private final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

    public final int BUTTON_WIDTH = 40;
    public final int BUTTON_SPACING_V = 24 / scaledResolution.getScaleFactor();
    public final int BUTTON_HEIGHT = 20;
    public final int MID_MARGIN = 20 / scaledResolution.getScaleFactor();
    public final int LEFT_MARGIN = 400 / scaledResolution.getScaleFactor();
    public final int TOP_MARGIN = 100 / scaledResolution.getScaleFactor();
    public final int BOTTOM_MARGIN = 60 / scaledResolution.getScaleFactor();


    public SettingsGui(Category category) {
        this.category = category;
    }

    @Override
    public void initGui() {
        clearElements();
        int currElemId = 0;
        List<Feature> features = Feature.features.stream().filter(feature -> feature.getFeatureType() != null).filter(feature -> category == feature.getFeatureType()).collect(Collectors.toList());
        for (int featureIndex = 0; featureIndex < features.size(); featureIndex++) {
            Feature feature = features.get(featureIndex);
            int xPos = (width + MID_MARGIN) / 2;
            int yPos = TOP_MARGIN + featureIndex * (BUTTON_SPACING_V + BUTTON_HEIGHT);
            this.buttonList.add(new FeatureButton(currElemId++, xPos, yPos, BUTTON_WIDTH, BUTTON_HEIGHT, feature));

            for (FeatureOption featureOption : feature.getOptions()) {
                xPos += BUTTON_WIDTH + BUTTON_SPACING_V;
                if (featureOption instanceof FeatureOptionSlider) {
                    this.buttonList.add(new FeatureGuiSlider(currElemId++, xPos, yPos, 150, 20, featureOption.getId() + " ", "", ((FeatureOptionSlider) featureOption).getMinVal(), ((FeatureOptionSlider) featureOption).getMaxVal(), ((FeatureOptionSlider) featureOption).getValue(), true, true, (FeatureOptionSlider) featureOption));

                } else if (featureOption instanceof FeatureOptionToggle) {
                    this.buttonList.add(new GuiButtonExt(currElemId++, xPos, yPos, 40, 20, featureOption.getId()));

                } else if (featureOption instanceof FeatureOptionText) {
                    this.fieldsToDraw.add(new FeatureTextField(currElemId++, xPos, yPos, 150, 20, featureOption));

                }
            }
        }

        if (category == Category.MAIN) {
            int widthMinusBWidth = width;
            for (Category category : Category.values()) {
                if (category == Category.MAIN) continue;
                widthMinusBWidth -= mc.fontRendererObj.getStringWidth(category.getFriendlyName()) + 12;
            }
            int spaceBetweenButtons = widthMinusBWidth / (Category.values().length - 1);
            Category[] values = Category.values();
            for (int i = 0; i < values.length; i++) {
                Category category = values[i];
                if (category == Category.MAIN) continue;
                this.buttonList.add(new CategoryButton(100+i,
                        spaceBetweenButtons / 2 + spaceBetweenButtons * (i-1),
                        height - BUTTON_HEIGHT - 10,
                        mc.fontRendererObj.getStringWidth(category.getFriendlyName()) + 12,
                        BUTTON_HEIGHT,
                        category));
            }
        } else {
            this.buttonList.add(new GuiButtonExt(100,
                    30,
                    height - BUTTON_HEIGHT - 10,
                    mc.fontRendererObj.getStringWidth("<- Back") + 12,
                    BUTTON_HEIGHT,
                    "<- Back"));
        }

        labelsToDraw.add(new BetterGuiLabel("§7Confusing§bAddons", (width - fontRendererObj.getStringWidth("§7Confusing§bAddons")) / 2, 60 / scaledResolution.getScaleFactor(), 0x000000));
        List<FeatureButton> buttons = buttonList.stream()
                .filter(b -> b instanceof FeatureButton)
                .map(b -> (FeatureButton) b)
                .filter(featureButton -> featureButton.getFeature().getFeatureType() != null)
                .collect(Collectors.toList());

        for (int featureIndex = 0; featureIndex < buttons.size(); featureIndex++) {
            Feature feature = buttons.get(featureIndex).getFeature();
            int xPos = (width - MID_MARGIN) / 2 - mc.fontRendererObj.getStringWidth(feature.getName());
            int yPos = TOP_MARGIN + 5 /* so text is centered in Y */ + featureIndex * (BUTTON_SPACING_V + BUTTON_HEIGHT);
            labelsToDraw.add(new FeatureGuiLabel(xPos, yPos, 0xFFFFFF, feature));
        }
    }

    private void clearElements() {
        this.buttonList.clear();
        this.labelsToDraw.clear();
        this.fieldsToDraw.clear();
    }

    private void changeCategory(Category category) {
        this.category = category;
        clearElements();
        initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiTextField field : this.fieldsToDraw) {
            field.drawTextBox();
        }

        for (BetterGuiLabel label : this.labelsToDraw) {
            label.drawLabel(mouseX, mouseY);
        }

        for (BetterGuiLabel label : this.labelsToDraw) {
            if (label.isHovering() && label instanceof FeatureGuiLabel) {
                Feature feature = ((FeatureGuiLabel) label).getFeature();
                if (feature.getDescription() == null || feature.getDescription().isEmpty()) continue;
                drawHoveringText(Arrays.asList(feature.getDescription().split("\n")), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof FeatureButton) {
            FeatureButton featureButton = (FeatureButton) button;
            featureButton.actionPerformed();
        } else if (button instanceof CategoryButton) {
            CategoryButton categoryButton = (CategoryButton) button;
            changeCategory(categoryButton.getCategory());
        } else if (button.id == 100) {
            changeCategory(Category.MAIN);
        }
    }
}
