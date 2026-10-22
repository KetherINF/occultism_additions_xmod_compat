package com.occultismadditions.xmodcompat.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mekanism Magic chalk item widening (works for 1.0.6fix and 1.0.7+).
 *
 * <p>{@code isAnyChalk} / {@code isChalkForColor} only accept
 * {@code occultism:chalk_<color>}. Extra chalk items (COE copper/zinc/brass,
 * Occult AE anima, ...) are accepted here for any namespace.</p>
 *
 * <p>The pentacle-structure scan lives in {@code PentacleMaterials} since 1.0.7 and is
 * widened by {@link MekanismMagicPentacleMaterialsMixin}.</p>
 */
@Mixin(targets = "com.example.mekanismmagic.integration.occultism.OccultismRecipeBridge")
public abstract class MekanismMagicChalkMixin {

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

    private static String pathOf(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id == null ? null : id.getPath();
    }
}
