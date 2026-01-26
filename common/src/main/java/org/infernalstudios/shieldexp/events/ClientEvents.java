package org.infernalstudios.shieldexp.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import org.infernalstudios.shieldexp.CommonClass;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.infernalstudios.shieldexp.init.ItemsInit;
import org.infernalstudios.shieldexp.mixin.ItemPropertiesAccessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class ClientEvents {

    public static void initShields() {
        ClampedItemPropertyFunction blockFn = (stack, world, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;

        for (Supplier<ShieldItem> shieldItem : ItemsInit.SHIELDS) {
            ItemPropertiesAccessor.register(shieldItem.get(), ResourceLocation.parse("minecraft:blocking"), blockFn);
        }
    }

    public static void copyOptionalResourcePackIfMissing() {
        File dir = new File(".", "resourcepacks");
        File target = new File(dir, "SE Vanilla Consistency 1.20.1.zip");

        if (!target.exists()) {
            try {
                dir.mkdirs();
                InputStream in = CommonClass.class.getResourceAsStream("/assets/shieldexp/SE_Vanilla_Consistency_1.20.1.zip");
                if (in != null) {
                    FileOutputStream out = new FileOutputStream(target);
                    byte[] buf = new byte[16384];
                    int len;
                    while ((len = in.read(buf)) > 0)
                        out.write(buf, 0, len);
                    in.close();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}