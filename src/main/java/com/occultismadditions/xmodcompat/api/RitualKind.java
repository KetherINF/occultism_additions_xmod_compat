package com.occultismadditions.xmodcompat.api;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A single "ritual kind" extension: maps a pentacle and/or a set of ritual types
 * onto the AE2 ritual chamber's notion of a ritual kind.
 *
 * <p>Backed by {@link RitualKindRegistry}; registrable from KubeJS via
 * {@code Java.loadClass(...)}.</p>
 */
public record RitualKind(
        String track,
        ResourceLocation pentacle,
        String minTier,
        Set<ResourceLocation> ritualTypes
) {
    public RitualKind {
        ritualTypes = ritualTypes == null ? Set.of() : Set.copyOf(ritualTypes);
        if (minTier == null || minTier.isBlank()) {
            minTier = "foliot";
        }
    }

    /**
     * Convenience factory for script/registrar use.
     * {@code track} may be blank to only add ritual types; {@code pentacle} may be blank.
     */
    public static RitualKind of(String track, String pentacle, String minTier, String... ritualTypes) {
        Set<ResourceLocation> types = new LinkedHashSet<>();
        if (ritualTypes != null) {
            for (String id : ritualTypes) {
                ResourceLocation rl = ResourceLocation.tryParse(id);
                if (rl != null) {
                    types.add(rl);
                }
            }
        }
        return new RitualKind(track, parse(pentacle), minTier, types);
    }

    private static ResourceLocation parse(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return ResourceLocation.tryParse(id);
    }
}
