package com.occultismadditions.xmodcompat;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// Client-only entry point. Never loads on a dedicated server.
@Mod(value = OccultismAdditionsCompat.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OccultismAdditionsCompat.MODID, value = Dist.CLIENT)
public class OccultismAdditionsCompatClient {
    public OccultismAdditionsCompatClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        CompatManager.onClientSetup();
    }
}
