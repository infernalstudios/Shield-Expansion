package org.infernalstudios.shieldexp.compat;

import org.infernalstudios.shieldexp.config.ClothConfigIntegration;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ClothConfigIntegration::createScreen;
    }
}