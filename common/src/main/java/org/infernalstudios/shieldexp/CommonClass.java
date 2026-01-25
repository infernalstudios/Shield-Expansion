package org.infernalstudios.shieldexp;

import org.infernalstudios.shieldexp.config.ShieldExpansionConfig;
import org.infernalstudios.shieldexp.init.ItemsInit;
import org.infernalstudios.shieldexp.init.NetworkInit;
import org.infernalstudios.shieldexp.init.SoundsInit;

public class CommonClass {
    public static void init() {
        ShieldExpansionConfig.load();
        ItemsInit.init();
        SoundsInit.init();
        NetworkInit.registerPackets();
    }
}