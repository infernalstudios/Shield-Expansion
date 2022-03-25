/**
 * Copyright 2022 Infernal Studios
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infernalstudios.shieldexp.mixin;

import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mixin(targets = { "net/minecraft/item/Item", "net/minecraft/item/ShieldItem" })
public class HoverTextMixins {

	@OnlyIn(Dist.CLIENT)
	@Inject(method = "appendHoverText", at = @At("TAIL"))
	private void shieldexp$appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> hoverText, ITooltipFlag flag, CallbackInfo ci) {
		Minecraft minecraft = Minecraft.getInstance();
		if (stack.isShield(minecraft.player)) {
			if (Screen.hasShiftDown()) {
				hoverText.add(new TranslationTextComponent("shieldexp.tooltip.instructions.parry").withStyle(TextFormatting.YELLOW));
			} else {
				hoverText.add(new TranslationTextComponent("shieldexp.tooltip.instructions").withStyle(TextFormatting.DARK_GRAY));
			}
		}
	}

}
