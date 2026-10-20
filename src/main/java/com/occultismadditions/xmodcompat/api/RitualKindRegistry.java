package com.occultismadditions.xmodcompat.api;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for cross-addon "ritual kind" extensions.
 *
 * <p>Two independent facets:</p>
 * <ul>
 *   <li><b>extra ritual types</b> — OR-ed into {@code RitualPatternDetails}'s hardcoded
 *       {@code CRAFT_TYPES}, so the AE2 chamber treats them as automatable;</li>
 *   <li><b>extra pentacle → track mappings</b> — OR-ed into
 *       {@code ChamberTrack.of(ResourceLocation)}, so foreign pentacles resolve to a track.</li>
 * </ul>
 *
 * <p>KubeJS usage (script):</p>
 * <pre>{@code
 * const Registry = Java.loadClass('com.occultismadditions.xmodcompat.api.RitualKindRegistry')
 * Registry.registerRitualType('mymod:my_ritual')
 * Registry.registerPentacle('SUMMON', 'mymod:my_pentacle', 'foliot')
 * }</pre>
 */
public final class RitualKindRegistry {
    private static final Set<ResourceLocation> EXTRA_RITUAL_TYPES = ConcurrentHashMap.newKeySet();
    private static final ConcurrentHashMap<ResourceLocation, TrackMapping> PENTACLE_TRACKS =
            new ConcurrentHashMap<>();

    private static final String CHAMBER_TRACK_CLASS = "com.dyvehn.occultae.chamber.ChamberTrack";

    private RitualKindRegistry() {
    }

    /** One pentacle → (track name, min tier name) mapping. */
    public record TrackMapping(String track, String minTier) {
    }

    /** Registers a ritual kind; populates both facets when present. */
    public static void register(RitualKind kind) {
        if (kind == null) {
            return;
        }
        for (ResourceLocation type : kind.ritualTypes()) {
            EXTRA_RITUAL_TYPES.add(type);
        }
        if (kind.pentacle() != null) {
            PENTACLE_TRACKS.put(kind.pentacle(), new TrackMapping(kind.track(), kind.minTier()));
        }
    }

    /** Adds a single ritual_type id (automatable whitelist). */
    public static void registerRitualType(String ritualTypeId) {
        ResourceLocation rl = ResourceLocation.tryParse(ritualTypeId);
        if (rl != null) {
            EXTRA_RITUAL_TYPES.add(rl);
        }
    }

    /** Adds a pentacle → track mapping ({@code track} is a ChamberTrack enum name, e.g. "SUMMON"). */
    public static void registerPentacle(String track, String pentacleId, String minTier) {
        ResourceLocation rl = ResourceLocation.tryParse(pentacleId);
        if (rl != null && track != null && !track.isBlank()) {
            PENTACLE_TRACKS.put(rl, new TrackMapping(track, minTier));
        }
    }

    public static Set<ResourceLocation> extraRitualTypes() {
        return Set.copyOf(EXTRA_RITUAL_TYPES);
    }

    public static boolean hasPentacle(ResourceLocation pentacle) {
        return pentacle != null && PENTACLE_TRACKS.containsKey(pentacle);
    }

    public static Optional<TrackMapping> trackFor(ResourceLocation pentacle) {
        return Optional.ofNullable(PENTACLE_TRACKS.get(pentacle));
    }

    /**
     * Reflectively resolves a track id to the actual {@code ChamberTrack} enum constant,
     * keeping the mixins free of a compile dependency on Occult AE.
     */
    public static Optional<Object> resolveTrack(String track) {
        if (track == null || track.isBlank()) {
            return Optional.empty();
        }
        try {
            Class<?> cls = Class.forName(CHAMBER_TRACK_CLASS);
            for (Object constant : cls.getEnumConstants()) {
                if (((Enum<?>) constant).name().equalsIgnoreCase(track)) {
                    return Optional.of(constant);
                }
            }
        } catch (Throwable ignored) {
            // Occult AE absent: harmless no-op.
        }
        return Optional.empty();
    }
}
