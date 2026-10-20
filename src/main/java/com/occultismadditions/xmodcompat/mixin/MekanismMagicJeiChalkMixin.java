package com.occultismadditions.xmodcompat.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The mini-ritual JEI page resolves each required chalk colour with
 * {@code ResourceLocation.fromNamespaceAndPath("occultism", "chalk_" + color)},
 * so colours that live in another mod's namespace (COE copper/zinc/brass,
 * Occult AE anima) render as empty slots.
 *
 * <p>This redirect keeps the vanilla lookup when it exists and otherwise searches
 * every namespace for an item whose path is {@code chalk_<color>}.</p>
 */
@Mixin(targets = "com.example.mekanismmagic.integration.jei.MiniRitualJeiCategory")
public abstract class MekanismMagicJeiChalkMixin {

    @Redirect(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/example/mekanismmagic/integration/occultism/OccultismRecipeBridge$PentacleJeiData;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation xmodcompat$resolveChalk(String namespace, String path) {
        ResourceLocation direct = ResourceLocation.fromNamespaceAndPath(namespace, path);
        if (BuiltInRegistries.ITEM.get(direct) != Items.AIR) {
            return direct;
        }
        for (ResourceLocation id : BuiltInRegistries.ITEM.keySet()) {
            if (id.getPath().equals(path)) {
                return id;
            }
        }
        return direct;
    }
}
