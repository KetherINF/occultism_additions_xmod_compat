package com.occultismadditions.xmodcompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Pentacle glyph scan widening for <b>Mekanism Magic 1.0.7+</b>.
 *
 * <p>1.0.7 moved the structure scan out of {@code OccupismRecipeBridge.scanPentacle}
 * into {@code PentacleMaterials.scan}. The new code still requires the block to live in
 * the {@code occultism} namespace and to use the {@code chalk_glyph_<color>} prefix, so
 * COE's {@code <color>_chalk} blocks and Occult AE's {@code occultae:chalk_glyph_anima}
 * are skipped.</p>
 *
 * <p>Redirects below drop the namespace gate and accept both naming schemes. The
 * 16-colour whitelist was removed upstream, so there is no colour gate to patch.</p>
 */
@Mixin(targets = "com.example.mekanismmagic.integration.occultism.PentacleMaterials")
public abstract class MekanismMagicPentacleMaterialsMixin {

    private static final String GLYPH_PREFIX = "chalk_glyph_";
    private static final String CHALK_SUFFIX = "_chalk";

    /** Drops the {@code "occultism".equals(blockId.getNamespace())} gate. */
    @Redirect(method = "scan",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", ordinal = 0))
    private static boolean xmodcompat$anyNamespace(String self, Object other) {
        return true;
    }

    /** Accepts both {@code chalk_glyph_<color>} and {@code <color>_chalk} block paths. */
    @Redirect(method = "scan",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z"))
    private static boolean xmodcompat$startsWith(String path, String prefix) {
        return path.startsWith(GLYPH_PREFIX) || path.endsWith(CHALK_SUFFIX);
    }

    /** Extracts the colour for either naming scheme. */
    @Redirect(method = "scan",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;substring(I)Ljava/lang/String;"))
    private static String xmodcompat$color(String path, int beginIndex) {
        if (path.startsWith(GLYPH_PREFIX)) {
            return path.substring(GLYPH_PREFIX.length());
        }
        if (path.endsWith(CHALK_SUFFIX)) {
            return path.substring(0, path.length() - CHALK_SUFFIX.length());
        }
        return "";
    }
}
