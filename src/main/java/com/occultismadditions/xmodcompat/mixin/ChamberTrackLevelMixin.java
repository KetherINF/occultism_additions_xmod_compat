package com.occultismadditions.xmodcompat.mixin;

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
 * Overrides {@code ChamberTrack.Level.displayName()} with the literal level name
 * supplied at registration, so no lang file is needed for KubeJS-defined tracks.
 */
@Mixin(targets = "com.dyvehn.occultae.chamber.ChamberTrack$Level")
public abstract class ChamberTrackLevelMixin {

    @Shadow
    @Final
    private ResourceLocation pentacle;

    @Inject(method = "displayName", at = @At("RETURN"), cancellable = true)
    private void xmodcompat$displayName(CallbackInfoReturnable<Component> cir) {
        Optional<String> name = TrackRegistry.levelName(this.pentacle);
        if (name.isPresent()) {
            cir.setReturnValue(Component.literal(name.get()));
        }
    }
}
