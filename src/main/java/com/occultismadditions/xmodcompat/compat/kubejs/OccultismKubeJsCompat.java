package com.occultismadditions.xmodcompat.compat.kubejs;

import com.mojang.logging.LogUtils;
import com.occultismadditions.xmodcompat.ModIds;
import com.occultismadditions.xmodcompat.api.ICompatModule;
import org.slf4j.Logger;

/**
 * Bridges to Occultism KubeJS ({@code occultism_kubejs}).
 *
 * <p>The KubeJS addon lets scripts define new chalks and rituals via a recipe
 * schema; this module reconciles script-defined content with the rest of the
 * "Occultism Additions" ecosystem.</p>
 */
public class OccultismKubeJsCompat implements ICompatModule {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public String targetModId() {
        return ModIds.OCCULTISM_KUBEJS;
    }

    @Override
    public void commonSetup() {
        LOGGER.info("[KubeJS] compat module loaded");
    }
}
