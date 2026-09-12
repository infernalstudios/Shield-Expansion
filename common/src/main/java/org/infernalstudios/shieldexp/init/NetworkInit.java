package org.infernalstudios.shieldexp.init;

import org.infernalstudios.shieldexp.network.SyncBlocking;
import org.infernalstudios.shieldexp.network.SyncConfig;
import org.infernalstudios.shieldexp.network.SyncShields;
import org.infernalstudios.shieldexp.network.SyncStamina;
import org.infernalstudios.shieldexp.platform.Services;

public class NetworkInit {
    public static void registerPackets() {
        Services.NETWORK.registerPacket(0, SyncShields.class);
        Services.NETWORK.registerPacket(1, SyncBlocking.class);
        Services.NETWORK.registerPacket(2, SyncConfig.class);
        Services.NETWORK.registerPacket(3, SyncStamina.class);
    }
}