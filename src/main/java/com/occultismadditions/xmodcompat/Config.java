package com.occultismadditions.xmodcompat;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common config for the compat layer. Per-module enable toggles can be added
 * here once the target-mod set is finalized.
 */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LOG_COMPAT_STATE = BUILDER
            .comment("Log which compat modules are active on startup.")
            .define("logCompatState", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
