package com.confusinguser.confusingaddons.asm.hooks;

import com.confusinguser.confusingaddons.utils.LangUtils;
import com.confusinguser.confusingaddons.utils.LinkPreviewBuilder;
import com.confusinguser.confusingaddons.utils.Utils;
import dev.conorthedev.mediamod.gui.util.DynamicTextureWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GuiConfirmOpenLinkHook {

    private final static ResourceLocation LINK_PREVIEW_BACKGROUND = new ResourceLocation("confusingaddons:link_preview_background.png");
    private static ResourceLocation currentImageLocation;
    private static BufferedImage currentImage;

    public static void drawScreen(GuiConfirmOpenLink thiz, String linkText) { // TODO make title continue multiple rows if no desc
        // Draw text
        LinkPreviewBuilder.Link link;
        try {
            link = LinkPreviewBuilder.extractLinkPreviewInfo(new URL(linkText));
        } catch (IOException e) {
            // Not even a valid link, or no connection
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (link == null) return;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);


        // Backgound box
        float boxScale = 2;
        int startLinkBoxX = thiz.width / 2 - (int) (104.5f * boxScale);
        int startLinkBoxY = thiz.height / 6 + 120;

        GlStateManager.pushMatrix();
        GlStateManager.scale(boxScale, boxScale, 1);
        thiz.mc.getTextureManager().bindTexture(LINK_PREVIEW_BACKGROUND);
        thiz.drawTexturedModalRect(
                Utils.scaledCoords(startLinkBoxX, boxScale),
                Utils.scaledCoords(startLinkBoxY, boxScale),
                0, 0, 209, 65);

        // Image
        float scale = 1f;
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        if (currentImageLocation == null) {
            BufferedImage image;
            try {
                image = DynamicTextureWrapper.getImage(new URL(link.getImage()));
                if (image.getHeight() != 1) {
                    double aspectRatio = (double) image.getWidth() / image.getHeight();
                    image = Utils.resize(image, Utils.scaledCoords((float) (114 * aspectRatio * boxScale), scale), Utils.scaledCoords(114 * boxScale, scale));
                    currentImage = image;
                    currentImageLocation = FMLClientHandler.instance().getClient()
                            .getTextureManager().getDynamicTextureLocation(link.getImage(), new DynamicTexture(image));
                }
            } catch (MalformedURLException ignored) {
            }
        }
        if (currentImageLocation != null && currentImage != null) {
            thiz.mc.getTextureManager().bindTexture(currentImageLocation);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            int imageWidth = Utils.scaledCoords(currentImage.getWidth() / 2f, scale);
            int imageHeight = Utils.scaledCoords(currentImage.getHeight() / 2f, scale);
            int cropAmount = imageWidth - imageHeight;
            Utils.drawTexturedModalRectResizedTexture(
                    Utils.scaledCoords(startLinkBoxX + 7f, scale),
                    Utils.scaledCoords(startLinkBoxY + 8f, scale),
                    cropAmount / 2, 0,
                    imageWidth - cropAmount / 2,
                    imageHeight,
                    imageWidth,
                    imageHeight);
        }

        int textStartX = startLinkBoxX + (int) (65 * boxScale);

        // Title
        scale = 1.0f;
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1);

        thiz.drawString(Minecraft.getMinecraft().fontRendererObj,
                "§l" +
                        LangUtils.cutOffString(link.getTitle(),
                                Utils.scaledCoords(139f * boxScale, scale), 1,
                                s -> LangUtils.getStringWidth(s) + LangUtils.countBoldifiableCharacters(s)), // Because it's bold
                Utils.scaledCoords(textStartX, scale),
                Utils.scaledCoords(startLinkBoxY + 12, scale),
                0xFFFFFF);

        GlStateManager.popMatrix();

        // Description & other text
        String descCutOff = LangUtils.cutOffString(link.getDescription(), Utils.scaledCoords(139f * boxScale, scale), 4,
                LangUtils::getStringWidth);
        String[] split = descCutOff.split("\n");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            thiz.drawString(Minecraft.getMinecraft().fontRendererObj,
                    "§7" + s,
                    textStartX,
                    startLinkBoxY + 28 + i * 10,
                    0xFFFFFF);
        }

        if (link instanceof LinkPreviewBuilder.YoutubeLink) {
            LinkPreviewBuilder.YoutubeLink youtubeLink = (LinkPreviewBuilder.YoutubeLink) link;
            String durationText;
            if (youtubeLink.getLengthSeconds() / 60 >= 60)
                durationText = youtubeLink.getLengthSeconds() / 60 / 60 + ":" + youtubeLink.getLengthSeconds() / 60 + ":" + youtubeLink.getLengthSeconds() % 60 + " h";
            else durationText = youtubeLink.getLengthSeconds() / 60 + ":" + youtubeLink.getLengthSeconds() % 60 + " min";
            String qualityText = youtubeLink.getQuality() + "p";
            String channelText = "by " + youtubeLink.getChannel();
            String viewsText = youtubeLink.getViews() + " views";

            float gapsize = 139f * boxScale;
            gapsize -= LangUtils.getStringWidth(durationText);
            gapsize -= LangUtils.getStringWidth(qualityText);
            gapsize -= LangUtils.getStringWidth(channelText);
            gapsize -= LangUtils.getStringWidth(viewsText);
            gapsize /= 3;

            int vidAttribX = textStartX;
            thiz.drawString(Minecraft.getMinecraft().fontRendererObj,
                    "§7" + durationText,
                    vidAttribX,
                    startLinkBoxY + (int) (65 * boxScale) - 20,
                    0xFFFFFF);
            thiz.drawString(Minecraft.getMinecraft().fontRendererObj,
                    "§7" + qualityText,
                    vidAttribX = (int) (vidAttribX + gapsize + LangUtils.getStringWidth(durationText)),
                    startLinkBoxY + (int) (65 * boxScale) - 20,
                    0xFFFFFF);
            thiz.drawString(Minecraft.getMinecraft().fontRendererObj,
                    "§7" + channelText,
                    vidAttribX = (int) (vidAttribX + gapsize + LangUtils.getStringWidth(qualityText)),
                    startLinkBoxY + (int) (65 * boxScale) - 20,
                    0xFFFFFF);
            thiz.drawString(Minecraft.getMinecraft().fontRendererObj,
                    "§7" + viewsText,
                    vidAttribX = (int) (vidAttribX + gapsize + LangUtils.getStringWidth(channelText)),
                    startLinkBoxY + (int) (65 * boxScale) - 20,
                    0xFFFFFF);

        }
    }

    public static void onGuiClosed() {
        currentImageLocation = null;
        currentImage = null;
    }
}
