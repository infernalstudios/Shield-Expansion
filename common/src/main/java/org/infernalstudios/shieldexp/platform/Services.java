package org.infernalstudios.shieldexp.platform;

import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.platform.services.INetworkHelper;
import org.infernalstudios.shieldexp.platform.services.IPlatformHelper;
import org.infernalstudios.shieldexp.platform.services.IRegistryHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IRegistryHelper REGISTRY = load(IRegistryHelper.class);
    public static final INetworkHelper NETWORK = load(INetworkHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}