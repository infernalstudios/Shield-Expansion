package org.infernalstudios.shieldexp.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.network.ClearShields;
import org.infernalstudios.shieldexp.network.SyncShields;

public class NetworkInit {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerPackets() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ShieldExpansion.MOD_ID, "packets"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(), SyncShields.class, SyncShields::encode, SyncShields::new, SyncShields::handle);
        INSTANCE.registerMessage(nextID(), ClearShields.class, ClearShields::encode, ClearShields::new, ClearShields::handle);
    }
}
