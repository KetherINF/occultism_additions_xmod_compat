package com.occultismadditions.xmodcompat;

/**
 * Central registry of target mod ids this compatibility layer bridges to.
 * Add a constant here when introducing a new compat module.
 */
public final class ModIds {
    /** Occultism base mod — the only hard dependency of this layer. */
    public static final String OCCULTISM = "occultism";

    // "Occultism Additions" family — soft-detected at runtime, never hard-depended on.
    public static final String OCCULT_ENGINEERING = "occultengineering"; // Create: Occult Engineering
    public static final String OCCULT_AE = "occultae";                   // Occult AE
    public static final String OCCULTISM_KUBEJS = "occultism_kubejs";    // Occultism KubeJS

    private ModIds() {
    }
}
