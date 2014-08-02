package coloredlightscore.src.asm;

import java.util.Arrays;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import coloredlightscore.fmlevents.ChunkDataEventHandler;
import coloredlightscore.network.PacketHandler;
import coloredlightscore.src.api.CLApi;
import coloredlightscore.src.helper.CLEntityRendererHelper;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ColoredLightsCoreDummyContainer extends DummyModContainer {
    public ChunkDataEventHandler chunkDataEventHandler;

    public static org.apache.logging.log4j.Logger CLLog = FMLLog.getLogger();

    public static int VERSION_MAJOR = 1;
    public static int VERSION_MINOR = 1;
    public static int VERSION_REVISION = 2;

    public ColoredLightsCoreDummyContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "coloredlightscore";
        meta.name = "Colored Lights Core";
        meta.version = String.format("%s.%s.%s", VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION);
        meta.credits = "";
        meta.authorList = Arrays.asList("heaton84", "CptSpaceToaster");
        meta.description = "The coremod for Colored Lights " + (((VERSION_REVISION & 1) != 0) ? "(Unstable Revision)" : "");

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
        CLEntityRendererHelper.Initialize();

        // Inject RGB values into vanilla blocks		
        CLApi.setBlockColorRGB(Blocks.lava, 15, 11, 10);
        CLApi.setBlockColorRGB(Blocks.flowing_lava, 15, 11, 10);
        CLApi.setBlockColorRGB(Blocks.torch, 14, 13, 12);
        CLApi.setBlockColorRGB(Blocks.fire, 15, 13, 11);
        CLApi.setBlockColorRGB(Blocks.lit_redstone_ore, 9, 6, 6);
        CLApi.setBlockColorRGB(Blocks.redstone_torch, 7, 4, 4);
        CLApi.setBlockColorRGB(Blocks.portal, 6, 3, 11);
        CLApi.setBlockColorRGB(Blocks.lit_furnace, 13, 13, 12);
        CLApi.setBlockColorRGB(Blocks.powered_repeater, 9, 6, 7);
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