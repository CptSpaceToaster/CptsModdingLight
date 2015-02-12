package yamhaven.easycoloredlights;

import cpw.mods.fml.common.registry.GameRegistry;
import yamhaven.easycoloredlights.lib.ModInfo;
import yamhaven.easycoloredlights.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class EasyColoredLights {
    @SidedProxy(clientSide = ModInfo.PROXY_LOCATION + ".ClientProxy", serverSide = ModInfo.PROXY_LOCATION + ".CommonProxy")
    public static CommonProxy proxy;

    public static CLWorldGen wg = new CLWorldGen();

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        CLMaterialsController.init();
        CLMaterialsController.registerMaterials();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        CLMaterialsController.addRecipes();
        GameRegistry.registerWorldGenerator(wg, 10000);
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {

    }
}