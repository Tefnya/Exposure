package io.github.mortuusars.exposure.client.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import io.github.mortuusars.exposure.client.render.PhotographRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;

public class PhotographTooltip implements ClientTooltipComponent, TooltipComponent {
    public static final int SIZE = 72;
    private final Either<String, ResourceLocation> photograph;
    private final int photographs;

    public PhotographTooltip(Either<String, ResourceLocation> photograph, int photographs) {
        this.photograph = photograph;
        this.photographs = photographs;
    }

    public PhotographTooltip(Either<String, ResourceLocation> photograph) {
        this.photograph = photograph;
        this.photographs = 1;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        return SIZE;
    }

    @Override
    public int getHeight() {
        return SIZE + 2; // 2px bottom margin
    }

    @Override
    public void renderImage(@NotNull Font font, int mouseX, int mouseY, @NotNull PoseStack poseStack, @NotNull ItemRenderer itemRenderer, int blitOffset) {
        int additionalPhotographs = Math.min(2, photographs - 1);

        poseStack.pushPose();
        poseStack.translate(mouseX, mouseY, blitOffset);
        float scale = SIZE / (float) PhotographRenderer.SIZE;
        float nextPhotographOffset = 0.03f;
        scale *= 1f - (additionalPhotographs * nextPhotographOffset);
        poseStack.scale(scale, scale, scale);

        // Rendering paper bottom to top:
        for (int i = additionalPhotographs; i > 0; i--) {
            float posOffset = PhotographRenderer.SIZE * (nextPhotographOffset * i);
            float brightness = 1f - (0.25f * i);

            poseStack.pushPose();
            poseStack.translate(posOffset, posOffset, 0);

            // For some reason LightTexture has no effect here. So we change brightness in shader color.
            RenderSystem.setShaderColor(brightness, brightness, brightness, 1);
            PhotographRenderer.renderTexture(PhotographRenderer.PHOTOGRAPH_PAPER_TEXTURE, poseStack, LightTexture.FULL_BRIGHT);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            poseStack.popPose();
        }

        PhotographRenderer.renderOnPaper(photograph, poseStack);
        poseStack.popPose();

        // Stack count:
        if (photographs > 1) {
            poseStack.pushPose();
            String count = Integer.toString(photographs);
            int fontWidth = Minecraft.getInstance().font.width(count);
            float fontScale = 1.6f;
            poseStack.translate(
                    mouseX + PhotographRenderer.SIZE * scale - fontWidth * fontScale,
                    mouseY + PhotographRenderer.SIZE * scale - 8 * fontScale,
                    blitOffset);
            poseStack.scale(fontScale, fontScale, fontScale);
            Minecraft.getInstance().font.drawShadow(poseStack, count, 0, 0, 0xFFFFFFFF);
            poseStack.popPose();
        }
    }
}
