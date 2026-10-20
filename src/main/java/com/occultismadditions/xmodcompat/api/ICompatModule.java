package com.occultismadditions.xmodcompat.api;

/**
 * A single cross-mod compatibility module.
 *
 * <p>Each implementation bridges the "Occultism Additions" layer to one target
 * mod. Modules must be self-guarding: {@link com.occultismadditions.xmodcompat.CompatManager}
 * only dispatches to a module when {@link #targetModId()} is actually loaded,
 * but a module may still add its own runtime checks inside the hooks.</p>
 */
public interface ICompatModule {
    /** The mod id this module bridges to. */
    String targetModId();

    /** Runs during common setup, only when the target mod is loaded. */
    default void commonSetup() {
    }

    /** Runs during client setup (client distribution only), only when the target mod is loaded. */
    default void clientSetup() {
    }
}
