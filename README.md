# Occultism Additions XMod Compat

让 **Occultism Additions 家族**（Create: Occult Engineering / Occult AE / Occultism KubeJS）的扩展
真正接入 Occultism 的仪式体系：新的仪式种类、新的仪式 track、以及 Mekanism Magic 的粉笔兼容。

- Minecraft **1.21.1** · NeoForge **21.1.259** · Java 21
- mod id：`occultism_additions_xmod_compat`
- **硬依赖仅 Occultism 本体**；Occult AE / Occult Engineering / KubeJS / Mekanism Magic 全部为运行时软探测

## 功能

### 1. 可扩展的仪式种类（Occult AE）

Occult AE 把 `ritual_type` 写死在 `RitualPatternDetails.CRAFT_TYPES` / `ENTITY_SUMMON_TYPES` 等处。
本 mod 通过 `RitualPatternDetailsMixin` 把注册表并入 `isChamberAutomatable`：

```java
RitualKindRegistry.registerRitualType("mymod:my_ritual_type");
```

### 2. 新的独立 chamber track

在 Occult AE 自带的 5 个 track 之外新增 track（例如把 COE 的「变灵 / Puca」做成第 6 种）：

```java
TrackRegistry.registerTrack("puca", "变灵", List.of(
        List.of("occultengineering:craft_puca@foliot", "变灵法阵")));
```

- 第二个参数是 track 显示名（留空则回落到 `track.occultae.<id>`）
- 每一级是 `[pentacleId@tier, 显示名]`，显示名留空则回落到 `track_level.occultae.<path>`
- 通过 `MethodHandles` 合成 `ChamberTrack` 枚举实例，再由 `ChamberTrackMixin` 追加进 `values()` / `of()`
- 显示名由 `ChamberTrackMixin` / `ChamberTrackLevelMixin` 直接返回，**不需要 lang 文件**

### 3. KubeJS 接口

`XModKubeJSPlugin` 注册全局绑定，脚本里可直接调用：

```js
TrackRegistry.registerTrack('ashes', '灰烬之星', [
    ['kubejs:star_of_ashes@foliot', '灰烬法阵'],
    'kubejs:ember_star@djinni'
])
```

- `TrackRegistry` 仅在 **Occult AE 存在** 时绑定；
- `RitualKindRegistry` 始终绑定。

### 4. Mekanism Magic 粉笔兼容

Mekanism Magic 只认 `occultism:chalk_<color>` 与 16 色表。本 mod 放宽到任意命名空间：

| 注入点 | 效果 |
|---|---|
| `isAnyChalk` / `isChalkForColor` | 接受任意 `chalk_<color>` / `<color>_chalk` |
| `scanPentacle`（命名空间、色表、颜色提取） | 结构扫描识别 `chalk_glyph_<color>` 与 `<color>_chalk` |
| `MiniRitualJeiCategory.setRecipe` | JEI 粉笔槽按 `chalk_<color>` 跨命名空间解析 |

因此 Create: Occult Engineering 的 `chalk_copper` / `occultae:chalk_anima` 等可以正常识别并进入配方需求。

### Mixin 一览

| Mixin | 目标 | 门控 |
|---|---|---|
| `RitualPatternDetailsMixin` | `occultae...RitualPatternDetails` | Occult AE |
| `ChamberTrackMixin` | `occultae...ChamberTrack` | Occult AE |
| `ChamberTrackLevelMixin` | `occultae...ChamberTrack$Level` | Occult AE |
| `MekanismMagicChalkMixin` | `mekanismmagic...OccultismRecipeBridge` | Mekanism Magic |
| `MekanismMagicJeiChalkMixin` | `mekanismmagic...MiniRitualJeiCategory` | Mekanism Magic |

`XModCompatMixinPlugin` 在目标 mod 不存在时整组跳过（资源探测，不 `Class.forName`）。

## 构建

需要本地存在以下 **编译期** jar（运行时不打包、不声明依赖）：

```
occultism-1.21.1-neoforge-1.224.5.jar
occultae-0.3.0.jar
kubejs-neoforge-2101.7.2-build.377.jar
```

默认从 `../../mods` 读取，可用 `-Pmods_dir=<path>` 覆盖：

```bash
./gradlew build -Pmods_dir=/path/to/mods
```

产物：`build/libs/occultism_additions_xmod_compat-<version>.jar`

## 来源与致谢

本 mod 是**兼容层**，不复制任何上游代码，只通过 mixin / 公开 API 与其交互。参考的上游项目：

- **Occultism** — klikli-dev（仪式、五芒星、粉笔体系）
- **Occult AE** — dyvehn（`RitualPatternDetails`、`ChamberTrack`）
- **Create: Occult Engineering**（`craft_puca`、铜/锌/黄铜粉笔）
- **Occultism KubeJS** — klikli-dev（`occultism:ritual` 配方类型、KubeJS 插件范式）
- **Mekanism Magic** — `com.example.mekanismmagic`（微缩仪式、粉笔识别）

## AI 说明

本项目的代码与文档在 **AI 辅助** 下生成与迭代（DeepSeek Harness），包括架构、mixin、KubeJS 接口与文档。
所有改动均经过本地构建与游戏内验证；使用前请自行审阅。

## 许可

[MIT](LICENSE)

