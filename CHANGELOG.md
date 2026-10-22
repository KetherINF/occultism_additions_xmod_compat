# Changelog

All notable changes to this project are documented here.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Versions use `x.y.z` for feature releases and a `fixN` suffix for
upstream-adaptation / compatibility fixes.

English | [简体中文](CHANGELOG.zh_cn.md)

## [Unreleased]

## [0.3.5fix3] - 2026-10-22

### Added

- `MekanismMagicLegacyScanMixin` widens the inline pentacle scan used by Mekanism Magic
  1.0.6fix and older, so extra chalk glyphs are recognised there too. It is gated on
  `PentacleMaterials` being absent and is skipped on 1.0.7+, where
  `MekanismMagicPentacleMaterialsMixin` takes over.

## [0.3.5fix2] - 2026-10-22

### Added

- Optional dependency declarations with version ranges in `neoforge.mods.toml`:
  `occultae` `[0.3.0,)`, `occultengineering` `[0.13.1,)`, `occultism_kubejs` `[1.11.0,)`,
  `kubejs` `[2101.7.2,)`, `mekanism_magic` `[1.0.6,)`. All are optional, so a version
  mismatch only produces a warning instead of blocking the load.

## [0.3.5fix1] - 2026-10-22

### Changed

- Mekanism Magic 1.0.7 moved the pentacle structure scan out of
  `OccultismRecipeBridge.scanPentacle` into `PentacleMaterials.scan`. Added
  `MekanismMagicPentacleMaterialsMixin` for the new location and dropped the stale
  `scanPentacle` redirects, which would otherwise have failed the whole chalk mixin.

### Fixed

- Extra chalk glyphs (`occultae:chalk_glyph_anima`, COE `<color>_chalk`, ...) are
  classified as chalk again. 1.0.7 turns an item-less block in the scan into a hard
  failure, which silently removed the assembler recipe for those pentacles.

### Added

- `-PdisableMekMagic=true` test build that strips the Mekanism Magic mixins, used to
  verify upstream chalk support on its own.

## [0.3.5] - 2026-10-21

### Changed

- The bundled Create: Occult Engineering integration registers the Puca track through
  `track.occultae.puca` / `track_level.occultae.craft_puca` instead of hardcoded
  Chinese literals, so English clients read "Puca / Puca Circle".

## [0.3.4] - 2026-10-21

### Added

- Extendable ritual kinds for Occult AE through `RitualKindRegistry`.
- Synthetic chamber tracks with ordered multi-level pentacles through `TrackRegistry`,
  with inline display names or lang-key fallback.
- KubeJS plugin exposing `TrackRegistry` and `RitualKindRegistry` as global bindings.
- Mekanism Magic chalk support: chalk items, pentacle glyph scans and JEI chalk slots
  now recognise chalks from any namespace.
