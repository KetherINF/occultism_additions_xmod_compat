# 更新日志

本项目的所有重要改动记录于此。

格式参考 [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)。
版本号：功能更新使用 `x.y.z`，上游适配 / 兼容修复使用 `fixN` 后缀。

[English](CHANGELOG.md) | 简体中文

## [未发布]

## [0.3.5fix3] - 2026-10-22

### 新增

- `MekanismMagicLegacyScanMixin`：适配 Mekanism Magic 1.0.6fix 及更早版本内联的法阵扫描，使其同样能识别额外粉笔符文。按「`PentacleMaterials` 不存在」门控，1.0.7+ 自动跳过，由 `MekanismMagicPentacleMaterialsMixin` 接管。

## [0.3.5fix2] - 2026-10-22

### 新增

- 在 `neoforge.mods.toml` 中声明可选依赖版本范围：`occultae` `[0.3.0,)`、`occultengineering` `[0.13.1,)`、`occultism_kubejs` `[1.11.0,)`、`kubejs` `[2101.7.2,)`、`mekanism_magic` `[1.0.6,)`。全部为可选，版本不匹配只产生警告，不会阻止加载。

## [0.3.5fix1] - 2026-10-22

### 变更

- Mekanism Magic 1.0.7 把法阵结构扫描从 `OccultismRecipeBridge.scanPentacle` 搬到了 `PentacleMaterials.scan`。新增 `MekanismMagicPentacleMaterialsMixin` 适配新位置，并移除已失效的 `scanPentacle` 重定向，避免整个粉笔 mixin 注入失败。

### 修复

- 额外粉笔符文（`occultae:chalk_glyph_anima`、COE 的 `<color>_chalk` 等）重新被判定为粉笔。1.0.7 会把扫描中无对应物品的方块直接判为失败，导致这些法阵的合成配方被静默移除。

### 新增

- `-PdisableMekMagic=true` 测试构建，可剔除 Mekanism Magic 的 mixin，用于单独验证上游的粉笔支持。

## [0.3.5] - 2026-10-21

### 变更

- 内置的 Create: Occult Engineering 集成改为通过 `track.occultae.puca` / `track_level.occultae.craft_puca` 注册变灵 track，不再硬编码中文字面量，英文客户端显示为 "Puca / Puca Circle"。

## [0.3.4] - 2026-10-21

### 新增

- 通过 `RitualKindRegistry` 为 Occult AE 提供可扩展的仪式种类。
- 通过 `TrackRegistry` 注册全新的 chamber track，支持有序多级五芒星，显示名可直接传入或回落到语言文件。
- KubeJS 插件，把 `TrackRegistry` 与 `RitualKindRegistry` 暴露为脚本全局绑定。
- Mekanism Magic 粉笔兼容：粉笔物品、法阵符文扫描、JEI 粉笔槽均支持任意命名空间。
