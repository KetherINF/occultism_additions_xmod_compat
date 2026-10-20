package com.occultismadditions.xmodcompat;

import com.occultismadditions.xmodcompat.api.ICompatModule;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Discovers and dispatches to the active compat modules.
 *
 * <p>Modules are registered in the mod constructor. At setup time only those
 * whose {@link ICompatModule#targetModId()} is present in the loaded mod list
 * are activated, so the layer degrades gracefully when a target mod is absent.</p>
 */
public final class CompatManager {
    private static final List<ICompatModule> MODULES = new ArrayList<>();

    private CompatManager() {
    }

    /** Registers a compat module. Call this from the mod constructor. */
    public static void register(ICompatModule module) {
        MODULES.add(module);
    }

    /** Whether a mod is currently loaded. */
    public static boolean isLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    /** The modules whose target mod is loaded. */
    public static List<ICompatModule> activeModules() {
        List<ICompatModule> active = new ArrayList<>();
        for (ICompatModule module : MODULES) {
            if (isLoaded(module.targetModId())) {
                active.add(module);
            }
        }
        return Collections.unmodifiableList(active);
    }

    /** Common setup hook — dispatch to every active module. */
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        for (ICompatModule module : activeModules()) {
            OccultismAdditionsCompat.LOGGER.info("[Compat] Activating module for '{}'", module.targetModId());
            module.commonSetup();
        }
    }

    /** Client setup hook — dispatch to every active module (client only). */
    public static void onClientSetup() {
        for (ICompatModule module : activeModules()) {
            module.clientSetup();
        }
    }
}
