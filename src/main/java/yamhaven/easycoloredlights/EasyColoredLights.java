package yamhaven.easycoloredlights;

import yamhaven.easycoloredlights.blocks.CLBlocksController;
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

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        CLBlocksController.init();
        CLBlocksController.registerBlocks();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        CLBlocksController.addBlockRecipes();
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {

    }
}