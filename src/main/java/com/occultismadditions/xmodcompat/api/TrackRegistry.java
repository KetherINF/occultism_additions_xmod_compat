package com.occultismadditions.xmodcompat.api;

import com.dyvehn.occultae.chamber.ChamberTier;
import com.dyvehn.occultae.chamber.ChamberTrack;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for entirely <b>new</b> chamber tracks (仪式种类).
 *
 * <p>Display names are supplied at registration time and returned by mixins on
 * {@code ChamberTrack.displayName()} / {@code ChamberTrack.Level.displayName()},
 * so no lang file is needed for script-defined tracks.</p>
 *
 * <p>Levels are passed as an <b>ordered list of pairs</b>, which keeps the exact
 * order of the array — no reliance on map iteration or tier sorting:</p>
 * <pre>{@code
 * TrackRegistry.registerTrack('ashes', '灰烬之星', [
 *     ['kubejs:star_of_ashes@foliot', '灰烬法阵'],
 *     ['kubejs:ember_star@djinni', '余烬法阵']
 * ])
 * }</pre>
 *
 * <p><b>Only usable when Occult AE is loaded.</b></p>
 */
public final class TrackRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    private static final List<ChamberTrack> EXTRA = new CopyOnWriteArrayList<>();
    private static final Map<ResourceLocation, ChamberTrack> BY_PENTACLE = new ConcurrentHashMap<>();
    private static final Map<String, ChamberTrack> BY_ID = new ConcurrentHashMap<>();
    private static final Map<String, String> TRACK_NAMES = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, String> LEVEL_NAMES = new ConcurrentHashMap<>();
    private static volatile boolean loggedAugment;

    private TrackRegistry() {
    }

    /**
     * @param id        stable track id
     * @param trackName literal display name; blank falls back to {@code track.occultae.<id>}
     * @param levels    ordered list of {@code [pentacleId@tier, levelDisplayName]} pairs
     */
    public static void registerTrack(String id, String trackName, List<?> levels) {
        if (id == null || id.isBlank()) {
            LOG.warn("[XModCompat] registerTrack rejected: blank id");
            return;
        }
        String key = id.toLowerCase(Locale.ROOT);
        if (BY_ID.containsKey(key)) {
            return;
        }
        if (levels == null || levels.isEmpty()) {
            LOG.warn("[XModCompat] track '{}' rejected: no levels", key);
            return;
        }

        List<ChamberTrack.Level> parsed = new ArrayList<>();
        for (Object raw : levels) {
            Spec spec = toSpec(raw);
            if (spec == null) {
                continue;
            }
            ResourceLocation pentacle = ResourceLocation.tryParse(spec.pentacle());
            if (pentacle == null) {
                LOG.warn("[XModCompat] track '{}': bad pentacle '{}'", key, spec.pentacle());
                continue;
            }
            parsed.add(new ChamberTrack.Level(pentacle, parseTier(spec.tier())));
            if (spec.name() != null && !spec.name().isBlank()) {
                LEVEL_NAMES.put(pentacle, spec.name().trim());
            } else {
                LOG.info("[XModCompat] level '{}' has no display name; falling back to lang key track_level.occultae.{}",
                        pentacle, pentacle.getPath());
            }
        }

        if (parsed.isEmpty()) {
            LOG.warn("[XModCompat] track '{}' rejected: no valid levels", key);
            return;
        }

        ChamberTrack track = createSynthetic(key, parsed.toArray(new ChamberTrack.Level[0]));
        if (track == null) {
            LOG.warn("[XModCompat] failed to synthesise track '{}'", key);
            return;
        }

        EXTRA.add(track);
        BY_ID.put(key, track);
        for (ChamberTrack.Level level : parsed) {
            BY_PENTACLE.put(level.pentacle(), track);
        }
        if (trackName != null && !trackName.isBlank()) {
            TRACK_NAMES.put(key, trackName.trim());
        } else {
            LOG.info("[XModCompat] track '{}' has no display name; falling back to lang key track.occultae.{}", key, key);
        }
        LOG.info("[XModCompat] synthetic track '{}' registered with {} level(s), extra tracks now {}",
                key, parsed.size(), EXTRA.size());
    }

    private record Spec(String pentacle, String tier, String name) {
    }

    private static Spec toSpec(Object raw) {
        if (raw == null) {
            return null;
        }
        String spec;
        String name = null;
        if (raw instanceof List<?> pair) {
            if (pair.isEmpty()) {
                return null;
            }
            spec = String.valueOf(pair.get(0));
            if (pair.size() > 1 && pair.get(1) != null) {
                name = String.valueOf(pair.get(1));
            }
        } else if (raw instanceof Object[] pair) {
            if (pair.length == 0) {
                return null;
            }
            spec = String.valueOf(pair[0]);
            if (pair.length > 1 && pair[1] != null) {
                name = String.valueOf(pair[1]);
            }
        } else {
            String text = String.valueOf(raw);
            int sep = text.indexOf('|');
            if (sep >= 0) {
                spec = text.substring(0, sep);
                name = text.substring(sep + 1);
            } else {
                spec = text;
            }
        }
        if (spec == null || spec.isBlank()) {
            return null;
        }
        String[] parts = spec.split("@", 2);
        return new Spec(parts[0].trim(), parts.length > 1 ? parts[1].trim() : null, name);
    }

    public static Optional<String> trackName(String id) {
        return Optional.ofNullable(id == null ? null : TRACK_NAMES.get(id.toLowerCase(Locale.ROOT)));
    }

    public static Optional<String> levelName(ResourceLocation pentacle) {
        return Optional.ofNullable(pentacle == null ? null : LEVEL_NAMES.get(pentacle));
    }

    public static ChamberTrack[] appendValues(ChamberTrack[] original) {
        if (EXTRA.isEmpty()) {
            return original;
        }
        if (!loggedAugment) {
            loggedAugment = true;
            LOG.info("[XModCompat] ChamberTrack.values() augmented with {} extra track(s): {}", EXTRA.size(), EXTRA);
        }
        ChamberTrack[] out = Arrays.copyOf(original, original.length + EXTRA.size());
        for (int i = 0; i < EXTRA.size(); i++) {
            out[original.length + i] = EXTRA.get(i);
        }
        return out;
    }

    public static Optional<ChamberTrack> byPentacle(ResourceLocation pentacle) {
        return Optional.ofNullable(pentacle == null ? null : BY_PENTACLE.get(pentacle));
    }

    public static Optional<ChamberTrack> byId(String id) {
        return Optional.ofNullable(id == null ? null : BY_ID.get(id.toLowerCase(Locale.ROOT)));
    }

    public static List<ChamberTrack> extras() {
        return new ArrayList<>(EXTRA);
    }

    private static ChamberTrack createSynthetic(String id, ChamberTrack.Level[] levels) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ChamberTrack.class, MethodHandles.lookup());
            MethodHandle ctor = lookup.findConstructor(
                    ChamberTrack.class,
                    MethodType.methodType(void.class, String.class, int.class, String.class,
                            ChamberTrack.Level[].class));
            ChamberTrack track = (ChamberTrack) ctor.invoke(
                    id.toUpperCase(Locale.ROOT),
                    ChamberTrack.values().length,
                    id,
                    levels);
            LOG.info("[XModCompat] synthetic track instance created: id={} ordinal={} levels={}",
                    id, track.ordinal(), track.levels().size());
            return track;
        } catch (Throwable t) {
            LOG.warn("[XModCompat] synthetic track creation failed for '{}'", id, t);
            return null;
        }
    }

    private static ChamberTier parseTier(String minTier) {
        if (minTier == null || minTier.isBlank()) {
            return ChamberTier.FOLIOT;
        }
        try {
            return ChamberTier.valueOf(minTier.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            LOG.warn("[XModCompat] unknown chamber tier '{}', falling back to FOLIOT", minTier);
            return ChamberTier.FOLIOT;
        }
    }
}
