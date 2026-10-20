package com.occultismadditions.xmodcompat.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Mekanism Magic chalk widening.
 *
 * <p>Naming differs per mod:</p>
 * <ul>
 *   <li>Occultism: item {@code occultism:chalk_<color>}, block {@code occultism:chalk_glyph_<color>};</li>
 *   <li>Create: Occult Engineering: item {@code occultengineering:chalk_<color>},
 *       block {@code occultengineering:<color>_chalk}.</li>
 * </ul>
 *
 * <p>Both {@code isAnyChalk}/{@code isChalkForColor} and {@code scanPentacle} are widened so
 * extra chalks are recognised and end up in the scanned pentacle's chalk requirement set.</p>
 */
@Mixin(targets = "com.example.mekanismmagic.integration.occultism.OccultismRecipeBridge")
public abstract class MekanismMagicChalkMixin {

    private static final String GLYPH_PREFIX = "chalk_glyph_";
    private static final String CHALK_SUFFIX = "_chalk";

    @Inject(method = "isAnyChalk", at = @At("RETURN"), cancellable = true)
    private static void xmodcompat$anyChalk(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }
        String path = pathOf(stack);
        if (path != null && (path.startsWith("chalk_") || path.endsWith(CHALK_SUFFIX))) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isChalkForColor", at = @At("RETURN"), cancellable = true)
    private static void xmodcompat$chalkForColor(ItemStack stack, String color,
                                                 CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }
        if (color == null || color.isBlank()) {
            return;
        }
        String path = pathOf(stack);
        if (path != null && (path.equals("chalk_" + color) || path.equals(color + CHALK_SUFFIX))) {
            cir.setReturnValue(true);
        }
    }

    /** Drops the {@code "occultism".equals(blockId.getNamespace())} gate in {@code scanPentacle}. */
    @Redirect(method = "scanPentacle",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", ordinal = 0))
    private static boolean xmodcompat$scanAnyNamespace(String self, Object other) {
        return true;
    }

    /** Accept both {@code chalk_glyph_<color>} and {@code <color>_chalk} block paths. */
    @Redirect(method = "scanPentacle",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z"))
    private static boolean xmodcompat$scanStartsWith(String path, String prefix) {
        return path.startsWith(GLYPH_PREFIX) || path.endsWith(CHALK_SUFFIX);
    }

    /** Extract the colour for either naming scheme. */
    @Redirect(method = "scanPentacle",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;substring(I)Ljava/lang/String;"))
    private static String xmodcompat$scanColor(String path, int beginIndex) {
        if (path.startsWith(GLYPH_PREFIX)) {
            return path.substring(GLYPH_PREFIX.length());
        }
        if (path.endsWith(CHALK_SUFFIX)) {
            return path.substring(0, path.length() - CHALK_SUFFIX.length());
        }
        return "";
    }

    /** Drops the {@code RITUAL_CHALK_COLORS.contains(color)} gate in {@code scanPentacle}. */
    @Redirect(method = "scanPentacle",
            at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z", ordinal = 0))
    private static boolean xmodcompat$scanAnyColor(List<?> list, Object color) {
        return color instanceof String s && !s.isBlank();
    }

    private static String pathOf(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id == null ? null : id.getPath();
    }
}
