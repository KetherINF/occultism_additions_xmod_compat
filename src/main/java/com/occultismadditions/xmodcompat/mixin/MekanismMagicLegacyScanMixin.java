package com.occultismadditions.xmodcompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * Pentacle glyph scan widening for <b>Mekanism Magic 1.0.6fix and older</b>.
 *
 * <p>Those versions keep the whole scan inline in
 * {@code OccultismRecipeBridge.scanPentacle}: a glyph colour is only recorded when the
 * block is in the {@code occultism} namespace, its path starts with {@code chalk_glyph_}
 * and the colour is in {@code RITUAL_CHALK_COLORS}.</p>
 *
 * <p>This mixin drops all three gates so extra chalks (COE {@code <color>_chalk},
 * Occult AE {@code occultae:chalk_glyph_anima}, ...) are recognised as chalk.
 * It is only applied when {@code PentacleMaterials} does not exist, i.e. before 1.0.7;
 * {@link MekanismMagicPentacleMaterialsMixin} covers 1.0.7+.</p>
 */
@Mixin(targets = "com.example.mekanismmagic.integration.occultism.OccultismRecipeBridge")
public abstract class MekanismMagicLegacyScanMixin {

    private static final String GLYPH_PREFIX = "chalk_glyph_";
    private static final String CHALK_SUFFIX = "_chalk";

    /** Drops the {@code "occultism".equals(blockId.getNamespace())} gate. */
    @Redirect(method = "scanPentacle",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", ordinal = 0))
    private static boolean xmodcompat$anyNamespace(String self, Object other) {
        return true;
    }

    /** Accepts both {@code chalk_glyph_<color>} and {@code <color>_chalk} block paths. */
    @Redirect(method = "scanPentacle",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z"))
    private static boolean xmodcompat$startsWith(String path, String prefix) {
        return path.startsWith(GLYPH_PREFIX) || path.endsWith(CHALK_SUFFIX);
    }

    /** Extracts the colour for either naming scheme. */
    @Redirect(method = "scanPentacle",
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

    /** Drops the {@code RITUAL_CHALK_COLORS.contains(color)} gate. */
    @Redirect(method = "scanPentacle",
            at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z", ordinal = 0))
    private static boolean xmodcompat$anyColor(List<?> list, Object color) {
        return color instanceof String s && !s.isBlank();
    }
}
