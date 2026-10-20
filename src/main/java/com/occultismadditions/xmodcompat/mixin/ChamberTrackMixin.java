package com.occultismadditions.xmodcompat.mixin;

import com.dyvehn.occultae.chamber.ChamberTrack;
import com.occultismadditions.xmodcompat.api.TrackRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Turns Occult AE's enum-backed track list into an extensible one:
 * <ul>
 *   <li>{@code values()} is augmented with registered synthetic tracks;</li>
 *   <li>{@code of(pentacle)} resolves registered pentacles;</li>
 *   <li>{@code displayName()} returns the literal name given at registration.</li>
 * </ul>
 */
@Mixin(ChamberTrack.class)
public abstract class ChamberTrackMixin {

    @Shadow
    @Final
    private String id;

    @Inject(method = "values", at = @At("RETURN"), cancellable = true)
    private static void xmodcompat$appendExtraTracks(CallbackInfoReturnable<ChamberTrack[]> cir) {
        ChamberTrack[] original = cir.getReturnValue();
        ChamberTrack[] augmented = TrackRegistry.appendValues(original);
        if (augmented != original) {
            cir.setReturnValue(augmented);
        }
    }

    @Inject(method = "of", at = @At("RETURN"), cancellable = true)
    private static void xmodcompat$mapExtraPentacles(ResourceLocation id,
                                                     CallbackInfoReturnable<Optional<ChamberTrack>> cir) {
        Optional<ChamberTrack> current = cir.getReturnValue();
        if (current != null && current.isPresent()) {
            return;
        }
        TrackRegistry.byPentacle(id).ifPresent(track -> cir.setReturnValue(Optional.of(track)));
    }

    @Inject(method = "displayName", at = @At("RETURN"), cancellable = true)
    private void xmodcompat$displayName(CallbackInfoReturnable<Component> cir) {
        Optional<String> name = TrackRegistry.trackName(this.id);
        if (name.isPresent()) {
            cir.setReturnValue(Component.literal(name.get()));
        }
    }
}
