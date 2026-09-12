package org.infernalstudios.shieldexp.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.infernalstudios.shieldexp.config.ShieldExpansionConfig;
import org.infernalstudios.shieldexp.events.ShieldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Unique
    private static final ResourceLocation SHIELD_INDICATOR_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "hud/shield_indicator_background");
    @Unique
    private static final ResourceLocation SHIELD_INDICATOR_PROGRESS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "hud/shield_indicator_progress");
    @Unique
    private static final ResourceLocation SHIELD_INDICATOR_FULL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "hud/shield_indicator_full");

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    private void shieldexp$renderShieldIndicator(GuiGraphics guiGraphics, net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!ShieldExpansionConfig.HUD_INDICATOR) return;

        Minecraft mc = Minecraft.getInstance();

        if (!mc.options.getCameraType().isFirstPerson()) return;

        Player player = mc.player;
        if (player == null) return;

        Item shieldItem = null;
        if (ShieldExpansionConfig.isShield(player.getOffhandItem().getItem())) {
            shieldItem = player.getOffhandItem().getItem();
        } else if (ShieldExpansionConfig.isShield(player.getMainHandItem().getItem())) {
            shieldItem = player.getMainHandItem().getItem();
        }

        if (shieldItem != null) {
            float readiness = 1.0F;
            boolean show = false;

            if (player.getCooldowns().isOnCooldown(shieldItem)) {
                readiness = 1.0F - player.getCooldowns().getCooldownPercent(shieldItem, 0.0F);
                show = true;
            } else if (player.isUsingItem() && player.getUseItem().getItem() == shieldItem) {
                int maxStamina = ShieldEvents.getShieldValue(shieldItem, "stamina").intValue();
                if (ShieldExpansionConfig.lenientStaminaEnabled()) maxStamina *= 2;

                int usedStamina = LivingEntityAccess.get(player).shieldexp$getUsedStamina();
                readiness = Math.max(0.0F, 1.0F - ((float) usedStamina / maxStamina));
                show = true;
            }

            if (show) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                int y = guiGraphics.guiHeight() / 2 - 9 + ShieldExpansionConfig.HUD_Y_OFFSET;
                int x = guiGraphics.guiWidth() / 2 - 23 + ShieldExpansionConfig.HUD_X_OFFSET;

                if (readiness >= 1.0F) {
                    guiGraphics.blitSprite(SHIELD_INDICATOR_FULL, x, y, 16, 16);
                } else {
                    int progressWidth = (int) (readiness * 17.0F);
                    guiGraphics.blitSprite(SHIELD_INDICATOR_BACKGROUND, x, y, 16, 16);
                    guiGraphics.blitSprite(SHIELD_INDICATOR_PROGRESS, 16, 16, 0, 0, x, y, progressWidth, 16);
                }

                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
            }
        }
    }
}