package com.occultismadditions.xmodcompat.compat.occultae;

import com.mojang.logging.LogUtils;
import com.occultismadditions.xmodcompat.ModIds;
import com.occultismadditions.xmodcompat.api.ICompatModule;
import org.slf4j.Logger;

/**
 * Bridges to Occult AE ({@code occultae}).
 *
 * <p>Occult AE adds anima chalk and infusion rituals (AE2 ritual autocrafting);
 * this module reconciles those chalk &amp; ritual additions with the rest of the
 * "Occultism Additions" ecosystem.</p>
 */
public class OccultAeCompat implements ICompatModule {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public String targetModId() {
        return ModIds.OCCULT_AE;
    }

    @Override
    public void commonSetup() {
        LOGGER.info("[OccultAE] compat module loaded");
    }
}
