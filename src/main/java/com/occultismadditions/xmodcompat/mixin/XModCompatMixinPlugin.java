package com.occultismadditions.xmodcompat.mixin;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Applies each mixin only when its target mod is actually present.
 *
 * <p><b>Never {@code Class.forName} a target here</b> — loading the target while the
 * mixin config is prepared throws {@code MixinTargetAlreadyLoadedException}.</p>
 */
public class XModCompatMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOG = LogUtils.getLogger();

    private boolean occultaePresent;
    private boolean mekanismMagicPresent;

    @Override
    public void onLoad(String mixinPackage) {
        occultaePresent = detect("occultae",
                "com/dyvehn/occultae/pattern/RitualPatternDetails.class");
        mekanismMagicPresent = detect("mekanism_magic",
                "com/example/mekanismmagic/integration/occultism/OccultismRecipeBridge.class");
        LOG.info("[XModCompat] mixin plugin onLoad: occultae={} mekanism_magic={}",
                occultaePresent, mekanismMagicPresent);
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("MekanismMagic")) {
            return mekanismMagicPresent;
        }
        return occultaePresent;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean detect(String modId, String resource) {
        try {
            var list = FMLLoader.getLoadingModList();
            if (list != null) {
                return list.getModFileById(modId) != null;
            }
        } catch (Throwable ignored) {
            // fall through to resource probe
        }
        try {
            ClassLoader loader = XModCompatMixinPlugin.class.getClassLoader();
            return loader != null && loader.getResource(resource) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
