package plus.dragons.createcentralkitchen.modules.farmersdelight.entry;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;
import plus.dragons.createcentralkitchen.CentralKitchen;
import plus.dragons.createcentralkitchen.core.fluid.SimpleTintedFluidType;
import plus.dragons.createcentralkitchen.data.recipe.RecipeGen;
import plus.dragons.createcentralkitchen.modules.farmersdelight.FarmersDelightModule;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.HashMap;
import java.util.Map;

import static plus.dragons.createcentralkitchen.CentralKitchen.REGISTRATE;

public class FdFluids {

    public static final FluidEntry<VirtualFluid>
        APPLE_CIDER = wateryDrink("apple_cider", 0xc68a47, ModItems.APPLE_CIDER)
            .register(),
        HOT_COCOA = thickDrink("hot_cocoa", 0xaf6c4c, ModItems.HOT_COCOA)
            .register(),
        MELON_JUICE = wateryDrink("melon_juice", 0xe24334, ModItems.MELON_JUICE)
            .register(),
        TOMATO_SAUCE = customDrink("tomato_sauce", ModItems.TOMATO_SAUCE)
            .register();
    
    private static FluidBuilder<VirtualFluid, CreateRegistrate> wateryDrink(String name,
                                                                            int color,
                                                                            RegistryObject<? extends ItemLike> drink)
    {
        return REGISTRATE.virtualFluid(name,
                new ResourceLocation("forge", "block/milk_still"),
                new ResourceLocation("forge", "block/milk_flowing"),
                SimpleTintedFluidType.factory(0xFF000000 | color),
                VirtualFluid::new)
            .defaultLang()
            //.properties(prop -> prop.descriptionId(Util.makeDescriptionId("item", drink.getId())))
            .transform(RecipeGen.fluidHandling(drink, 250));
    }
    
    private static FluidBuilder<VirtualFluid, CreateRegistrate> thickDrink(String name,
                                                                           int color,
                                                                           RegistryObject<? extends ItemLike> drink)
    {
        return REGISTRATE.virtualFluid(name,
                Create.asResource("fluid/milk_still"),
                Create.asResource("fluid/milk_flow"),
                SimpleTintedFluidType.factory(0xFF000000 | color),
                VirtualFluid::new)
            .defaultLang()
            //.properties(prop -> prop.descriptionId(Util.makeDescriptionId("item", drink.getId())))
            .transform(RecipeGen.fluidHandling(drink, 250));
    }

    private static FluidBuilder<VirtualFluid, CreateRegistrate> customDrink(String name,
                                                                            RegistryObject<? extends ItemLike> drink)
    {
        return REGISTRATE.virtualFluid(name,
                CentralKitchen.genRL("fluid/" + name + "_still"),
                CentralKitchen.genRL("fluid/" + name + "_flow"))
            .defaultLang()
            //.properties(prop -> prop.descriptionId(Util.makeDescriptionId("item", drink.getId())))
            .transform(RecipeGen.fluidHandling(drink, 250));
    }

    private static final Map<ResourceLocation, FluidEntry<?>> REMAP = new HashMap<>();
    static  {
        REMAP.put(FarmersDelightModule.genRL("apple_cider"), APPLE_CIDER);
        REMAP.put(FarmersDelightModule.genRL("hot_cocoa"), HOT_COCOA);
        REMAP.put(FarmersDelightModule.genRL("melon_juice"), MELON_JUICE);
        REMAP.put(FarmersDelightModule.genRL("tomato_sauce"), TOMATO_SAUCE);
    }
    
    public static void remap(MissingMappingsEvent event) {
        for (var mapping : event.getMappings(Registry.FLUID_REGISTRY,FarmersDelightModule.ID)) {
            if (REMAP.containsKey(mapping.getKey())) {
                var entry = REMAP.get(mapping.getKey());
                mapping.remap(entry.get());
                FarmersDelightModule.LOGGER.warn("Remapping fluid '{}' to '{}'...", mapping.getKey(), entry.getId());
            }
        }
    }
    
    public static void register() {}
    
}