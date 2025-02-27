package plus.dragons.createcentralkitchen.mixin.common.jei;

import mezz.jei.api.IModPlugin;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plus.dragons.createcentralkitchen.modules.farmersdelight.integration.jei.FarmersDelightJeiPlugin;
import plus.dragons.createcentralkitchen.modules.farmersrespite.integration.jei.FarmersRespiteJeiPlugin;

import java.util.List;

/**
 *
 */
@Pseudo
@Mixin(targets = "mezz.jei.forge.startup.ForgePluginFinder", remap = false)
public class DynamicJeiPluginMixin {
    
    @Inject(method = "getModPlugins", at = @At("RETURN"))
    private static void injectDynamicModPlugins(CallbackInfoReturnable<List<IModPlugin>> cir) {
        List<IModPlugin> list = cir.getReturnValue();
        ModList mods = ModList.get();
        if (mods.isLoaded("farmersdelight")) list.add(new FarmersDelightJeiPlugin());
        if (mods.isLoaded("farmersrespite")) list.add(new FarmersRespiteJeiPlugin());
        // if (mods.isLoaded("miners_delight")) list.add(new MinersDelightJeiPlugin()); Miner's Delight stopped at 1.18.2
    }
    
}
