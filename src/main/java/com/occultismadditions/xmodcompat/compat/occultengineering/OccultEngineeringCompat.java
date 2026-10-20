package com.occultismadditions.xmodcompat.compat.occultengineering;

import com.mojang.logging.LogUtils;
import com.occultismadditions.xmodcompat.CompatManager;
import com.occultismadditions.xmodcompat.ModIds;
import com.occultismadditions.xmodcompat.api.ICompatModule;
import com.occultismadditions.xmodcompat.api.RitualKindRegistry;
import com.occultismadditions.xmodcompat.api.TrackRegistry;
import org.slf4j.Logger;

/**
 * Bridges to Create: Occult Engineering ({@code occultengineering}).
 *
 * <p>COE adds a new spirit — <b>Puca / 变灵</b> — plus the
 * {@code occultengineering:craft_puca} pentacle. Its rituals are
 * {@code ritual_type = occultism:craft}. This module:</p>
 * <ul>
 *   <li>registers the Puca pentacle as a <b>new chamber track</b> (“puca”), so it
 *       shows up as a 6th ritual kind in the AE2 ritual chamber;</li>
 *   <li>re-registers the craft ritual type defensively.</li>
 * </ul>
 */
public class OccultEngineeringCompat implements ICompatModule {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public String targetModId() {
        return ModIds.OCCULT_ENGINEERING;
    }

    @Override
    public void commonSetup() {
        RitualKindRegistry.registerRitualType("occultism:craft");

        // 变灵 / Puca as the 6th independent track — only when Occult AE is present,
        // because TrackRegistry references Occult AE types.
        // Name and per-level name are left blank on purpose: the track then resolves
        // track.occultae.puca / track_level.occultae.craft_puca from the lang files,
        // so each language gets its own text.
        if (CompatManager.isLoaded(ModIds.OCCULT_AE)) {
            TrackRegistry.registerTrack("puca", "",
                    java.util.List.of(java.util.List.of("occultengineering:craft_puca@foliot", "")));
            LOGGER.info("[OccultEngineering] registered Puca as a new chamber track");
        } else {
            LOGGER.info("[OccultEngineering] Occult AE absent; Puca track registration skipped");
        }
    }
}
