package com.occultismadditions.xmodcompat.mixin;

import com.klikli_dev.occultism.crafting.recipe.RitualRecipe;
import com.occultismadditions.xmodcompat.api.RitualKindRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

/**
 * Extends Occult AE's hardcoded automation whitelist ({@code CRAFT_TYPES}) from
 * the compat registry.
 *
 * <p>Note: {@code isPreRitual} is deliberately NOT touched — it is an
 * <em>exclusion</em> gate inside {@code isChamberAutomatable}, so pushing extra
 * pentacles into it would remove rituals instead of adding them.</p>
 */
@Mixin(targets = "com.dyvehn.occultae.pattern.RitualPatternDetails")
public abstract class RitualPatternDetailsMixin {

    @Inject(method = "isChamberAutomatable", at = @At("RETURN"), cancellable = true)
    private static void xmodcompat$extendAutomatable(RitualRecipe recipe, Level level,
                                                     CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }
        Set<ResourceLocation> extra = RitualKindRegistry.extraRitualTypes();
        if (!extra.isEmpty() && extra.contains(recipe.getRitualType())) {
            cir.setReturnValue(true);
        }
    }
}
