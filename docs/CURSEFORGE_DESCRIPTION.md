# Occultism Additions XMod Compat

A compatibility layer that lets the Occultism additions family — **Occult AE**, **Create: Occult Engineering**, **Occultism KubeJS** — and **Mekanism Magic** extend Occultism's ritual system.

Hard dependency: **Occultism** only. Every other integration is runtime-optional.

## Features

### Extendable ritual kinds (Occult AE)

Occult AE hardcodes its automatable ritual types. This mod opens that list up:

```java
RitualKindRegistry.registerRitualType("mymod:my_ritual_type");
```

### New chamber tracks

Register an entirely new ritual track on top of Occult AE's five built-in ones:

```java
TrackRegistry.registerTrack("puca", "Puca", List.of(
        List.of("occultengineering:craft_puca@foliot", "Puca Circle")));
```

- Track and per-level display names are passed inline — **no lang file needed**
- Levels are an ordered list, so the progression is exactly what you write
- Works with any pentacle from any mod that Occult AE can see

### KubeJS bindings

The mod ships a KubeJS plugin that exposes the registries as global bindings:

```js
TrackRegistry.registerTrack('ashes', 'Ashes', [
    ['kubejs:star_of_ashes@foliot', 'Ashes Circle'],
    'kubejs:ember_star@djinni'
])
```

Names may be omitted per level; the mod then falls back to Occult AE's own lang keys
(`track_level.occultae.<path>` / `track.occultae.<id>`).

### Mekanism Magic chalk support

Mekanism Magic only recognised `occultism:chalk_<color>` and the 16 vanilla colours, so extra chalks
rendered as empty slots and could not be used. Now they work everywhere:

- **chalk items** (`chalk_copper`, `chalk_zinc`, `chalk_brass`, `chalk_anima`, …) are accepted by the chalk module
- **pentacle scans** pick up `chalk_glyph_<color>` and `<color>_chalk` blocks from any namespace
- **JEI chalk slots** resolve the matching item across namespaces

## Supported mods

| Mod | Integration |
|---|---|
| Occultism | required |
| Occult AE | ritual kinds, chamber tracks |
| Create: Occult Engineering | Puca track, copper / zinc / brass chalks |
| Occultism KubeJS | KubeJS bindings |
| Mekanism Magic | chalk recognition |

## Requirements

- Minecraft **1.21.1**
- NeoForge **21.1.259+**
- **Occultism**

## Notes

- All compat modules are runtime-detected; the mod loads fine without any of the optional integrations.
- Mixins are applied per-target and skipped entirely when the target mod is absent.

## License

MIT

## Credits

Developed with AI assistance (DeepSeek Harness), reviewed and built locally.
Interacts with the mods above through mixins and public APIs only; no upstream code is copied.
