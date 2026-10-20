package com.occultismadditions.xmodcompat;

import com.mojang.logging.LogUtils;
import com.occultismadditions.xmodcompat.compat.kubejs.OccultismKubeJsCompat;
import com.occultismadditions.xmodcompat.compat.occultae.OccultAeCompat;
import com.occultismadditions.xmodcompat.compat.occultengineering.OccultEngineeringCompat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * Occultism Additions XMod Compat — a cross-mod compatibility layer that unifies
 * the chalks (粉笔) and rituals (仪式) added by the "Occultism Additions" family
 * (Create: Occult Engineering, Occult AE, Occultism KubeJS).
 *
 * <p>Hard-depends on Occultism base only. Every addon bridge is self-guarding and
 * activates only when that addon is loaded, so the layer degrades gracefully.</p>
 */
@Mod(OccultismAdditionsCompat.MODID)
public class OccultismAdditionsCompat {
    public static final String MODID = "occultism_additions_xmod_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OccultismAdditionsCompat(IEventBus modEventBus, ModContainer modContainer) {
        // Register compat modules. Order is irrelevant; dispatch is load-driven.
        CompatManager.register(new OccultEngineeringCompat());
        CompatManager.register(new OccultAeCompat());
        CompatManager.register(new OccultismKubeJsCompat());

        // Dispatch common setup to all active modules once loading completes.
        modEventBus.addListener(CompatManager::onCommonSetup);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
