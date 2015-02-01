package coloredlightscore.src.asm;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;
import java.util.Arrays;
import java.util.Iterator;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import coloredlightscore.fmlevents.ChunkDataEventHandler;
import coloredlightscore.network.PacketHandler;
import coloredlightscore.src.api.CLApi;
import coloredlightscore.src.helper.CLEntityRendererHelper;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ColoredLightsCoreDummyContainer extends DummyModContainer {
    public ChunkDataEventHandler chunkDataEventHandler;

    public static final String version = "1.3.4";

    public ColoredLightsCoreDummyContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "coloredlightscore";
        meta.name = "Colored Lights Core";
        meta.version = version;
        meta.logoFile = "/mod_ColoredLightCore.logo.png";
        meta.url = "http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/1445251-1-7-10-beta-wip-colored-light-progress-and";
        meta.credits = "";
        meta.authorList = Arrays.asList("heaton84", "Murray65536", "Kovu", "Biggerfisch", "CptSpaceToaster");
        meta.description = "The coremod for Colored Lights";
        meta.useDependencyInformation = true;
        chunkDataEventHandler = new ChunkDataEventHandler();
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);

        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent evt) {

        CLLog = evt.getModLog();

        CLLog.info("Starting up ColoredLightsCore");

        // Spin up network handler
        PacketHandler.init();

        // Hook into chunk events
        MinecraftForge.EVENT_BUS.register(chunkDataEventHandler);

    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent evt) {
        if (evt.getSide() == Side.CLIENT) {
            CLEntityRendererHelper.Initialize();
        }

        // Inject RGB values into vanilla blocks		
        CLApi.setBlockColorRGB(Blocks.lava, 15, 10, 0);
        CLApi.setBlockColorRGB(Blocks.flowing_lava, 15, 10, 0);
        CLApi.setBlockColorRGB(Blocks.torch, 14, 13, 10);
        CLApi.setBlockColorRGB(Blocks.fire, 15, 13, 0);
        CLApi.setBlockColorRGB(Blocks.lit_redstone_ore, 9, 0, 0);
        CLApi.setBlockColorRGB(Blocks.redstone_torch, 7, 0, 0);
        CLApi.setBlockColorRGB(Blocks.portal, 6, 3, 11);
        CLApi.setBlockColorRGB(Blocks.lit_furnace, 13, 12, 10);
        CLApi.setBlockColorRGB(Blocks.powered_repeater, 9, 0, 0);

        Object thisShouldBeABlock;
        int l;
        Iterator blockRegistryInterator = GameData.getBlockRegistry().iterator();
        while (blockRegistryInterator.hasNext()) {
            thisShouldBeABlock = blockRegistryInterator.next();
            if (thisShouldBeABlock instanceof Block) {
                l = ((Block)thisShouldBeABlock).lightValue;
                if ((l > 0) && (l <= 0xF)) {
                    CLLog.info(((Block)thisShouldBeABlock).getLocalizedName() + "has light:" + l + ", but no color");
                    ((Block)thisShouldBeABlock).lightValue = (l<<15) | (l<<10) | (l<<5) | l; //copy vanilla brightness into each color component to make it white/grey.
                }
            }
        }
    }

    /*
    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {

    	if (event.phase == Phase.END)
    	{
    		CLEntityRendererHelper.debugLightmap();
    	}
    }
    */

}