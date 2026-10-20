package com.occultismadditions.xmodcompat.kubejs;

import com.occultismadditions.xmodcompat.api.RitualKindRegistry;
import com.occultismadditions.xmodcompat.api.TrackRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import net.neoforged.fml.ModList;

/**
 * KubeJS integration.
 *
 * <p>Exposes the compat registries as global script bindings so scripts call
 * {@code TrackRegistry.registerTrack(...)} directly instead of
 * {@code Java.loadClass(...)}.</p>
 *
 * <p>Dual requirement: {@code TrackRegistry} references Occult AE types, so it is
 * only bound when Occult AE is loaded. KubeJS itself only loads this plugin when
 * KubeJS is present, which gives the KJS + Occult AE pairing.</p>
 */
public class XModKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBindings(BindingRegistry binder) {
        binder.add("RitualKindRegistry", RitualKindRegistry.class);

        if (ModList.get().isLoaded("occultae")) {
            binder.add("TrackRegistry", TrackRegistry.class);
        }
    }
}
